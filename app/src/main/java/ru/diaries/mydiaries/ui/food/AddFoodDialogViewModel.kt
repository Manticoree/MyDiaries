package ru.diaries.mydiaries.ui.food

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.diaries.mydiaries.domain.usecase.food.SaveFoodUseCase
import ru.diaries.mydiaries.feature.food.data.model.FoodEntry
import ru.diaries.mydiaries.feature.food.data.model.ServingSize
import ru.diaries.mydiaries.feature.food.ml.CompositeFoodClassifier
import ru.diaries.mydiaries.feature.food.ml.FoodClassifierContract
import ru.diaries.mydiaries.feature.food.ml.FoodPrediction
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddFoodDialogViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val saveFoodUseCase: SaveFoodUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddFoodDialogState())
    val state: StateFlow<AddFoodDialogState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddFoodDialogEffect>()
    val effect: SharedFlow<AddFoodDialogEffect> = _effect.asSharedFlow()

    private var foodClassifier: FoodClassifierContract? = null

    init {
        initializeClassifier()
    }

    private fun initializeClassifier() {
        viewModelScope.launch(Dispatchers.IO) {
            val classifier = CompositeFoodClassifier(context)
            foodClassifier = classifier

            // Initialize classifier (tries TFLite first, falls back to ML Kit)
            classifier.initialize()
        }
    }

    fun handleIntent(intent: AddFoodDialogIntent) {
        when (intent) {
            is AddFoodDialogIntent.PhotoSelected -> selectPhoto(intent.uri)
            is AddFoodDialogIntent.PredictionSelected -> selectPrediction(intent.prediction)
            is AddFoodDialogIntent.ServingSizeSelected -> selectServingSize(intent.size)
            is AddFoodDialogIntent.Save -> saveFood()
            is AddFoodDialogIntent.ClearError -> clearError()
            is AddFoodDialogIntent.Clear -> clearState()
            is AddFoodDialogIntent.ClearPhoto -> clearPhoto()
            is AddFoodDialogIntent.RetryDownload -> retryDownload()
        }
    }

    private fun selectPhoto(uri: Uri) {
        _state.update {
            it.copy(
                selectedPhotoUri = uri,
                isAnalyzing = true,
                isDownloadingModel = false,
                downloadProgress = 0f,
                predictions = emptyList(),
                selectedPrediction = null,
                error = null
            )
        }

        viewModelScope.launch {
            analyzeImage(uri)
        }
    }

    private suspend fun analyzeImage(uri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                val classifier = foodClassifier ?: CompositeFoodClassifier(context).apply {
                    initialize()
                }.also { foodClassifier = it }

                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    val predictions = classifier.classify(bitmap)
                    bitmap.recycle()

                    if (predictions.isEmpty()) {
                        _state.update {
                            it.copy(
                                isAnalyzing = false,
                                error = "Не удалось распознать еду на фото. Попробуйте другое фото."
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isAnalyzing = false,
                                predictions = predictions,
                                selectedPrediction = predictions.firstOrNull()
                            )
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isAnalyzing = false,
                            error = "Не удалось загрузить изображение"
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        isDownloadingModel = false,
                        error = e.message ?: "Ошибка при анализе изображения"
                    )
                }
            }
        }
    }

    private fun selectPrediction(prediction: FoodPrediction) {
        _state.update { it.copy(selectedPrediction = prediction, error = null) }
    }

    private fun selectServingSize(size: ServingSize) {
        _state.update { it.copy(selectedServingSize = size) }
    }

    private fun saveFood() {
        val currentState = _state.value
        val photoUri = currentState.selectedPhotoUri
        val prediction = currentState.selectedPrediction

        if (photoUri == null || prediction == null) {
            _state.update { it.copy(error = "Выберите фото и тип еды") }
            return
        }

        _state.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            try {
                val entry = FoodEntry(
                    id = UUID.randomUUID().toString(),
                    photoUri = photoUri.toString(),
                    foodName = prediction.label,
                    displayName = prediction.displayName,
                    calories = prediction.caloriesPer100g,
                    servingSize = currentState.selectedServingSize,
                    confidence = prediction.confidence
                )

                saveFoodUseCase(entry)

                _state.update { it.copy(isSaving = false) }
                _effect.emit(AddFoodDialogEffect.SaveSuccess)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Ошибка сохранения"
                    )
                }
            }
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun retryDownload() {
        val currentPhotoUri = _state.value.selectedPhotoUri ?: return

        _state.update {
            it.copy(
                isAnalyzing = true,
                isDownloadingModel = false,
                downloadProgress = 0f,
                error = null
            )
        }

        viewModelScope.launch {
            analyzeImage(currentPhotoUri)
        }
    }

    private fun clearPhoto() {
        _state.update {
            it.copy(
                selectedPhotoUri = null,
                isAnalyzing = false,
                isDownloadingModel = false,
                downloadProgress = 0f,
                predictions = emptyList(),
                selectedPrediction = null,
                error = null
            )
        }
    }

    private fun clearState() {
        _state.value = AddFoodDialogState()
    }

    override fun onCleared() {
        super.onCleared()
        foodClassifier?.close()
    }
}
