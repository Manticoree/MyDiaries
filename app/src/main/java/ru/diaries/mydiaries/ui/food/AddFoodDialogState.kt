package ru.diaries.mydiaries.ui.food

import android.net.Uri
import ru.diaries.mydiaries.feature.food.data.model.ServingSize
import ru.diaries.mydiaries.feature.food.ml.FoodPrediction

data class AddFoodDialogState(
    val selectedPhotoUri: Uri? = null,
    val isAnalyzing: Boolean = false,
    val isDownloadingModel: Boolean = false,
    val downloadProgress: Float = 0f,
    val predictions: List<FoodPrediction> = emptyList(),
    val selectedPrediction: FoodPrediction? = null,
    val selectedServingSize: ServingSize = ServingSize.MEDIUM,
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed class AddFoodDialogIntent {
    data class PhotoSelected(val uri: Uri) : AddFoodDialogIntent()
    data class PredictionSelected(val prediction: FoodPrediction) : AddFoodDialogIntent()
    data class ServingSizeSelected(val size: ServingSize) : AddFoodDialogIntent()
    data object Save : AddFoodDialogIntent()
    data object ClearError : AddFoodDialogIntent()
    data object Clear : AddFoodDialogIntent()
    data object ClearPhoto : AddFoodDialogIntent()
    data object RetryDownload : AddFoodDialogIntent()
}

sealed class AddFoodDialogEffect {
    data object SaveSuccess : AddFoodDialogEffect()
}
