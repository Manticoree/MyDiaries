package ru.diaries.mydiaries.feature.food.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import ru.diaries.mydiaries.feature.food.data.api.GroqApi

/**
 * Composite food classifier with priority chain: Groq → TFLite → ML Kit.
 * Falls back to next classifier if the current one fails.
 */
class CompositeFoodClassifier(
    private val context: Context,
    private val groqApi: GroqApi? = null,
    private val groqApiKey: String = ""
) : FoodClassifierContract {

    private var groqClassifier: GroqFoodClassifier? = null
    private var tfliteClassifier: TFLiteFoodClassifier? = null
    private var mlKitClassifier: FoodClassifier? = null
    private var useTFLite = true

    companion object {
        private const val TAG = "CompositeFoodClassifier"
    }

    override fun initialize(): Boolean {
        if (groqApi != null && groqApiKey.isNotBlank()) {
            groqClassifier = GroqFoodClassifier(groqApi, groqApiKey)
            Log.d(TAG, "Groq classifier initialized")
        }

        tfliteClassifier = TFLiteFoodClassifier(context)
        val tfliteInitialized = tfliteClassifier?.initialize() == true

        if (tfliteInitialized) {
            Log.d(TAG, "TFLite Food-101 classifier initialized successfully")
            useTFLite = true
            return true
        }

        Log.w(TAG, "TFLite initialization failed, falling back to ML Kit")
        useTFLite = false
        mlKitClassifier = FoodClassifier(context)
        return mlKitClassifier?.initialize() == true
    }

    override fun isReady(): Boolean {
        if (groqClassifier != null) return true
        return if (useTFLite) {
            tfliteClassifier?.isReady() == true
        } else {
            mlKitClassifier?.isReady() == true
        }
    }

    override fun needsDownload(): Boolean {
        if (groqClassifier != null) return false
        return if (useTFLite) {
            tfliteClassifier?.needsDownload() == true
        } else {
            mlKitClassifier?.needsDownload() == true
        }
    }

    override suspend fun classifyAsync(bitmap: Bitmap): List<FoodPrediction> {
        // Try Groq first
        if (groqClassifier != null) {
            try {
                val results = groqClassifier?.classifyAsync(bitmap) ?: emptyList()
                if (results.isNotEmpty()) {
                    Log.d(TAG, "Groq returned ${results.size} predictions")
                    return results
                }
                Log.w(TAG, "Groq returned empty results, trying local classifiers")
            } catch (e: Exception) {
                Log.e(TAG, "Groq classification failed, falling back to local", e)
            }
        }

        // Try TFLite
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
        // Groq only supports async, skip it in sync path
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

        return getOrCreateMLKitClassifier().classify(bitmap)
    }

    private fun getOrCreateMLKitClassifier(): FoodClassifier {
        if (mlKitClassifier == null) {
            mlKitClassifier = FoodClassifier(context).apply { initialize() }
        }
        return mlKitClassifier!!
    }

    override fun close() {
        groqClassifier?.close()
        groqClassifier = null
        tfliteClassifier?.close()
        tfliteClassifier = null
        mlKitClassifier?.close()
        mlKitClassifier = null
    }
}
