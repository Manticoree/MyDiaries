package ru.diaries.mydiaries.feature.food.ml

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Prediction result from food classification.
 */
data class FoodPrediction(
    val label: String,
    val displayName: String,
    val confidence: Float,
    val caloriesPer100g: Int
)

/**
 * Food classifier using Google ML Kit Image Labeling.
 * Works offline with built-in model - no download required.
 * Used as fallback when TFLite model is unavailable.
 */
class FoodClassifier(private val context: Context) : FoodClassifierContract {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()
    )

    private var isInitialized = true // ML Kit is always ready

    companion object {
        private const val MAX_RESULTS = 5

        // Food-related labels that ML Kit can recognize
        private val FOOD_LABELS = setOf(
            "food", "dish", "cuisine", "meal", "snack", "dessert", "breakfast",
            "lunch", "dinner", "fruit", "vegetable", "meat", "seafood", "fish",
            "pizza", "burger", "sandwich", "salad", "soup", "pasta", "rice",
            "bread", "cake", "cookie", "ice cream", "chocolate", "candy",
            "cheese", "egg", "chicken", "beef", "pork", "steak", "sushi",
            "noodle", "dumpling", "taco", "burrito", "hot dog", "french fries",
            "fried", "grilled", "baked", "roasted", "fast food", "junk food",
            "produce", "ingredient", "recipe", "cooking", "baking",
            "apple", "banana", "orange", "strawberry", "grape", "watermelon",
            "tomato", "potato", "carrot", "broccoli", "lettuce", "onion",
            "mushroom", "corn", "pepper", "cucumber", "avocado",
            "coffee", "tea", "juice", "milk", "smoothie", "cocktail", "beer", "wine",
            "pancake", "waffle", "donut", "muffin", "croissant", "bagel",
            "yogurt", "cereal", "oatmeal", "bacon", "sausage"
        )
    }

    override fun initialize(): Boolean = true // ML Kit doesn't need initialization

    suspend fun initializeWithDownload(onProgress: (Float) -> Unit = {}): Boolean {
        onProgress(1f)
        return true
    }

    override fun isReady(): Boolean = isInitialized

    override fun needsDownload(): Boolean = false // ML Kit has built-in model

    /**
     * Classify a food image using ML Kit.
     */
    override suspend fun classifyAsync(bitmap: Bitmap): List<FoodPrediction> {
        return suspendCancellableCoroutine { continuation ->
            val image = InputImage.fromBitmap(bitmap, 0)

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    val predictions = labels
                        .filter { label ->
                            // Filter for food-related labels
                            FOOD_LABELS.any { foodLabel ->
                                label.text.lowercase().contains(foodLabel) ||
                                        foodLabel.contains(label.text.lowercase())
                            }
                        }
                        .map { label ->
                            val labelText = label.text.lowercase()
                            FoodPrediction(
                                label = labelText,
                                displayName = CalorieDatabase.getDisplayName(labelText),
                                confidence = label.confidence,
                                caloriesPer100g = CalorieDatabase.getCaloriesPer100g(labelText)
                            )
                        }
                        .sortedByDescending { it.confidence }
                        .take(MAX_RESULTS)
                        .ifEmpty {
                            // If no food labels found, return top labels anyway
                            labels.take(MAX_RESULTS).map { label ->
                                val labelText = label.text.lowercase()
                                FoodPrediction(
                                    label = labelText,
                                    displayName = CalorieDatabase.getDisplayName(labelText),
                                    confidence = label.confidence,
                                    caloriesPer100g = CalorieDatabase.getCaloriesPer100g(labelText)
                                )
                            }
                        }

                    continuation.resume(predictions)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    continuation.resume(emptyList())
                }
        }
    }

    /**
     * Synchronous classify - wraps async version.
     * For compatibility with existing code.
     */
    override fun classify(bitmap: Bitmap): List<FoodPrediction> {
        // This is a blocking call - should be called from coroutine
        var result: List<FoodPrediction> = emptyList()
        val image = InputImage.fromBitmap(bitmap, 0)

        val task = labeler.process(image)

        // Wait for result (blocking)
        try {
            val labels = com.google.android.gms.tasks.Tasks.await(task)
            result = labels
                .filter { label ->
                    FOOD_LABELS.any { foodLabel ->
                        label.text.lowercase().contains(foodLabel) ||
                                foodLabel.contains(label.text.lowercase())
                    }
                }
                .map { label ->
                    val labelText = label.text.lowercase()
                    FoodPrediction(
                        label = labelText,
                        displayName = CalorieDatabase.getDisplayName(labelText),
                        confidence = label.confidence,
                        caloriesPer100g = CalorieDatabase.getCaloriesPer100g(labelText)
                    )
                }
                .sortedByDescending { it.confidence }
                .take(MAX_RESULTS)
                .ifEmpty {
                    labels.take(MAX_RESULTS).map { label ->
                        val labelText = label.text.lowercase()
                        FoodPrediction(
                            label = labelText,
                            displayName = CalorieDatabase.getDisplayName(labelText),
                            confidence = label.confidence,
                            caloriesPer100g = CalorieDatabase.getCaloriesPer100g(labelText)
                        )
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**
     * Get mock predictions for testing.
     */
    fun getMockPrediction(): List<FoodPrediction> {
        val mockLabels = listOf("pizza", "hamburger", "sushi", "salad", "ice cream")
        return mockLabels.mapIndexed { index, label ->
            FoodPrediction(
                label = label,
                displayName = CalorieDatabase.getDisplayName(label),
                confidence = 0.9f - (index * 0.15f),
                caloriesPer100g = CalorieDatabase.getCaloriesPer100g(label)
            )
        }
    }

    override fun close() {
        labeler.close()
        isInitialized = false
    }
}
