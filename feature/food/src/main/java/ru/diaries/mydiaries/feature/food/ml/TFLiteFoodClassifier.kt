package ru.diaries.mydiaries.feature.food.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Food classifier using TensorFlow Lite with Food-101 model.
 * Provides more accurate food classification than general ML Kit.
 */
class TFLiteFoodClassifier(private val context: Context) : FoodClassifierContract {

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()
    private var isInitialized = false
    private var isFloatModel = true

    companion object {
        private const val TAG = "TFLiteFoodClassifier"
        private const val MODEL_PATH = "food101_model.tflite"
        private const val LABELS_PATH = "food_labels.txt"
        private const val IMAGE_SIZE = 192  // Model expects 192x192
        private const val PIXEL_SIZE = 3
        private const val MAX_RESULTS = 5
    }

    override fun initialize(): Boolean {
        return try {
            // Load model
            val modelBuffer = FileUtil.loadMappedFile(context, MODEL_PATH)
            val options = Interpreter.Options().apply {
                setNumThreads(4)
            }
            interpreter = Interpreter(modelBuffer, options)

            // Detect model input format
            val inputTensor = interpreter!!.getInputTensor(0)
            isFloatModel = inputTensor.dataType() == DataType.FLOAT32
            Log.d(TAG, "Model input: dtype=${inputTensor.dataType()}, shape=${inputTensor.shape().contentToString()}")
            Log.d(TAG, "Model output: dtype=${interpreter!!.getOutputTensor(0).dataType()}, shape=${interpreter!!.getOutputTensor(0).shape().contentToString()}")

            // Load labels
            labels = FileUtil.loadLabels(context, LABELS_PATH)

            isInitialized = true
            true
        } catch (e: Exception) {
            Log.e(TAG, "Initialization failed", e)
            isInitialized = false
            false
        }
    }

    override fun isReady(): Boolean = isInitialized && interpreter != null

    override fun needsDownload(): Boolean = false // Model is bundled in assets

    override suspend fun classifyAsync(bitmap: Bitmap): List<FoodPrediction> {
        return withContext(Dispatchers.Default) {
            classify(bitmap)
        }
    }

    override fun classify(bitmap: Bitmap): List<FoodPrediction> {
        if (!isReady()) {
            return emptyList()
        }

        val interpreter = this.interpreter ?: return emptyList()

        // Preprocess image
        val inputBuffer = preprocessImage(bitmap)

        // Run inference
        val outputArray = Array(1) { FloatArray(labels.size) }
        interpreter.run(inputBuffer, outputArray)

        // Process results
        val probabilities = outputArray[0]
        return processResults(probabilities)
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)

        val bytesPerChannel = if (isFloatModel) 4 else 1
        val bufferSize = IMAGE_SIZE * IMAGE_SIZE * PIXEL_SIZE * bytesPerChannel
        val inputBuffer = ByteBuffer.allocateDirect(bufferSize)
        inputBuffer.order(ByteOrder.nativeOrder())
        inputBuffer.rewind()

        val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)
        scaledBitmap.getPixels(pixels, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE)

        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF

            if (isFloatModel) {
                inputBuffer.putFloat(r / 255.0f)
                inputBuffer.putFloat(g / 255.0f)
                inputBuffer.putFloat(b / 255.0f)
            } else {
                inputBuffer.put(r.toByte())
                inputBuffer.put(g.toByte())
                inputBuffer.put(b.toByte())
            }
        }

        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }

        inputBuffer.rewind()
        return inputBuffer
    }

    private fun processResults(probabilities: FloatArray): List<FoodPrediction> {
        // Create list of (index, probability) pairs
        val indexedProbabilities = probabilities.mapIndexed { index, probability ->
            index to probability
        }

        // Sort by probability descending and take top results
        return indexedProbabilities
            .sortedByDescending { it.second }
            .take(MAX_RESULTS)
            .filter { it.second > 0.01f } // Filter out very low confidence
            .map { (index, probability) ->
                val label = labels.getOrElse(index) { "unknown" }
                val displayName = CalorieDatabase.getDisplayName(label)
                Log.d("TFLiteFoodClassifier", "Label: '$label' -> DisplayName: '$displayName'")
                FoodPrediction(
                    label = label,
                    displayName = displayName,
                    confidence = probability,
                    caloriesPer100g = CalorieDatabase.getCaloriesPer100g(label)
                )
            }
    }

    override fun close() {
        interpreter?.close()
        interpreter = null
        isInitialized = false
    }
}
