package ru.diaries.mydiaries.feature.food.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import ru.diaries.mydiaries.feature.food.data.model.ServingSize
import ru.diaries.mydiaries.feature.food.ml.FoodPrediction

@Composable
fun AddFoodDialog(
    selectedPhotoUri: Uri?,
    isAnalyzing: Boolean,
    isDownloadingModel: Boolean = false,
    downloadProgress: Float = 0f,
    predictions: List<FoodPrediction>,
    selectedPrediction: FoodPrediction?,
    selectedServingSize: ServingSize,
    isSaving: Boolean,
    error: String? = null,
    onTakePhoto: () -> Unit,
    onPickFromGallery: () -> Unit,
    onPredictionSelected: (FoodPrediction) -> Unit,
    onServingSizeSelected: (ServingSize) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    onClearPhoto: () -> Unit = {},
    onRetryDownload: () -> Unit = {},
    dialogTitle: String = "Добавить еду",
    takePhotoText: String = "Сфотографировать",
    takePhotoDescription: String = "Использовать камеру",
    pickFromGalleryText: String = "Из галереи",
    pickFromGalleryDescription: String = "Выбрать фото еды",
    analyzingText: String = "Анализ...",
    downloadingModelText: String = "Загрузка модели...",
    recognizedAsText: String = "Распознано как",
    servingSizeText: String = "Размер порции",
    caloriesText: String = "Калории",
    saveText: String = "Добавить",
    cancelText: String = "Отмена",
    retakeText: String = "Переснять",
    retryText: String = "Повторить"
) {
    val mintGreen = Color(0xFF8ECDA0)
    val sageGreen = Color(0xFFA5C9A1)

    val configuration = LocalConfiguration.current
    val maxContentHeight = (configuration.screenHeightDp * 0.6f).dp

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(mintGreen, sageGreen)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable content area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = maxContentHeight)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedPhotoUri != null) {
                        // Photo preview with retake button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(4f / 3f)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            AsyncImage(
                                model = selectedPhotoUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Analyzing/Downloading overlay
                            if (isAnalyzing || isDownloadingModel) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (isDownloadingModel) {
                                            CircularProgressIndicator(
                                                progress = { downloadProgress },
                                                color = mintGreen,
                                                modifier = Modifier.size(48.dp),
                                                strokeWidth = 4.dp
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = downloadingModelText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "${(downloadProgress * 100).toInt()}%",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        } else {
                                            CircularProgressIndicator(
                                                color = mintGreen,
                                                modifier = Modifier.size(40.dp)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = analyzingText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            // Retake button overlay
                            if (!isAnalyzing) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        IconButton(
                                            onClick = onClearPhoto,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Refresh,
                                                contentDescription = retakeText,
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Error message with retry button
                        if (error != null && !isAnalyzing && !isDownloadingModel) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = error,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = onRetryDownload,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = retryText)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Predictions
                        if (!isAnalyzing && !isDownloadingModel && predictions.isNotEmpty()) {
                            Text(
                                text = recognizedAsText,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Prediction chips
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                predictions.take(3).forEach { prediction ->
                                    PredictionChip(
                                        prediction = prediction,
                                        isSelected = selectedPrediction == prediction,
                                        onClick = { onPredictionSelected(prediction) },
                                        accentColor = mintGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Serving size selector
                            Text(
                                text = servingSizeText,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ServingSize.entries.forEach { size ->
                                    FilterChip(
                                        selected = selectedServingSize == size,
                                        onClick = { onServingSizeSelected(size) },
                                        label = {
                                            Text(
                                                text = size.displayNameRu,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        leadingIcon = if (selectedServingSize == size) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = mintGreen.copy(alpha = 0.2f),
                                            selectedLabelColor = mintGreen,
                                            selectedLeadingIconColor = mintGreen
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Calorie display
                            if (selectedPrediction != null) {
                                Spacer(modifier = Modifier.height(16.dp))

                                val estimatedCalories = (selectedPrediction.caloriesPer100g * selectedServingSize.multiplier).toInt()

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = mintGreen.copy(alpha = 0.15f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = caloriesText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "$estimatedCalories ккал",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = mintGreen
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Photo source selection
                        FoodSourceOption(
                            icon = Icons.Outlined.CameraAlt,
                            title = takePhotoText,
                            subtitle = takePhotoDescription,
                            gradientColors = listOf(mintGreen, sageGreen),
                            onClick = onTakePhoto
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FoodSourceOption(
                            icon = Icons.Outlined.FolderOpen,
                            title = pickFromGalleryText,
                            subtitle = pickFromGalleryDescription,
                            gradientColors = listOf(sageGreen, mintGreen),
                            onClick = onPickFromGallery
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons - always visible at bottom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = cancelText)
                    }

                    Button(
                        onClick = onSave,
                        enabled = selectedPrediction != null && !isSaving && !isAnalyzing,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = mintGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = saveText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PredictionChip(
    prediction: FoodPrediction,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            accentColor.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, accentColor)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = prediction.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${prediction.caloriesPer100g} ккал/100г • ${(prediction.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = accentColor
                )
            }
        }
    }
}

@Composable
private fun FoodSourceOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(colors = gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
