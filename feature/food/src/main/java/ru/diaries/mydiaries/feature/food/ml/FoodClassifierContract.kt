package ru.diaries.mydiaries.feature.food.ml

import android.graphics.Bitmap
import java.io.Closeable

/**
 * Contract for food classifiers.
 * Implemented by TFLiteFoodClassifier and FoodClassifier (ML Kit).
 */
interface FoodClassifierContract : Closeable {

    /**
     * Initialize the classifier.
     * @return true if initialization successful
     */
    fun initialize(): Boolean

    /**
     * Check if classifier is ready to use.
     */
    fun isReady(): Boolean

    /**
     * Check if model needs to be downloaded.
     */
    fun needsDownload(): Boolean

    /**
     * Classify a food image asynchronously.
     * @param bitmap Image to classify
     * @return List of predictions sorted by confidence
     */
    suspend fun classifyAsync(bitmap: Bitmap): List<FoodPrediction>

    /**
     * Classify a food image synchronously.
     * @param bitmap Image to classify
     * @return List of predictions sorted by confidence
     */
    fun classify(bitmap: Bitmap): List<FoodPrediction>
}
