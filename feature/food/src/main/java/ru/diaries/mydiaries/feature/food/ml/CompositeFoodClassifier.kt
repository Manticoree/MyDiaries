package ru.diaries.mydiaries.feature.food.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log

/**
 * Composite food classifier that tries TFLite Food-101 model first,
 * falls back to ML Kit on error.
 */
class CompositeFoodClassifier(private val context: Context) : FoodClassifierContract {

    private var tfliteClassifier: TFLiteFoodClassifier? = null
    private var mlKitClassifier: FoodClassifier? = null
    private var useTFLite = true

    companion object {
        private const val TAG = "CompositeFoodClassifier"
    }

    override fun initialize(): Boolean {
        // Try to initialize TFLite classifier first
        tfliteClassifier = TFLiteFoodClassifier(context)
        val tfliteInitialized = tfliteClassifier?.initialize() == true

        if (tfliteInitialized) {
            Log.d(TAG, "TFLite Food-101 classifier initialized successfully")
            useTFLite = true
            return true
        }

        // Fall back to ML Kit
        Log.w(TAG, "TFLite initialization failed, falling back to ML Kit")
        useTFLite = false
        mlKitClassifier = FoodClassifier(context)
        return mlKitClassifier?.initialize() == true
    }

    override fun isReady(): Boolean {
        return if (useTFLite) {
            tfliteClassifier?.isReady() == true
        } else {
            mlKitClassifier?.isReady() == true
        }
    }

    override fun needsDownload(): Boolean {
        return if (useTFLite) {
            tfliteClassifier?.needsDownload() == true
        } else {
            mlKitClassifier?.needsDownload() == true
        }
    }

    override suspend fun classifyAsync(bitmap: Bitmap): List<FoodPrediction> {
        if (useTFLite && tfliteClassifier?.isReady() == true) {
            try {
                val results = tfliteClassifier?.classifyAsync(bitmap) ?: emptyList()
                if (results.isNotEmpty()) {
                    return results
                }
                Log.w(TAG, "TFLite returned empty results, trying ML Kit")
            } catch (e: Exception) {
                Log.e(TAG, "TFLite classification failed, falling back to ML Kit", e)
            }
        }

        // Fallback to ML Kit
        return getOrCreateMLKitClassifier().classifyAsync(bitmap)
    }

    override fun classify(bitmap: Bitmap): List<FoodPrediction> {
        if (useTFLite && tfliteClassifier?.isReady() == true) {
            try {
                val results = tfliteClassifier?.classify(bitmap) ?: emptyList()
                if (results.isNotEmpty()) {
                    return results
                }
                Log.w(TAG, "TFLite returned empty results, trying ML Kit")
            } catch (e: Exception) {
                Log.e(TAG, "TFLite classification failed, falling back to ML Kit", e)
            }
        }

        // Fallback to ML Kit
        return getOrCreateMLKitClassifier().classify(bitmap)
    }

    private fun getOrCreateMLKitClassifier(): FoodClassifier {
        if (mlKitClassifier == null) {
            mlKitClassifier = FoodClassifier(context).apply { initialize() }
        }
        return mlKitClassifier!!
    }

    override fun close() {
        tfliteClassifier?.close()
        tfliteClassifier = null
        mlKitClassifier?.close()
        mlKitClassifier = null
    }
}
