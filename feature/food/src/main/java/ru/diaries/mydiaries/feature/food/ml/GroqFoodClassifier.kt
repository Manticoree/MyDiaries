package ru.diaries.mydiaries.feature.food.ml

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import org.json.JSONArray
import ru.diaries.mydiaries.feature.food.data.api.GroqApi
import ru.diaries.mydiaries.feature.food.data.api.GroqChatRequest
import ru.diaries.mydiaries.feature.food.data.api.GroqImageContent
import ru.diaries.mydiaries.feature.food.data.api.GroqImageUrl
import ru.diaries.mydiaries.feature.food.data.api.GroqMessage
import ru.diaries.mydiaries.feature.food.data.api.GroqTextContent
import java.io.ByteArrayOutputStream

class GroqFoodClassifier(
    private val groqApi: GroqApi,
    private val apiKey: String
) : FoodClassifierContract {

    companion object {
        private const val TAG = "GroqFoodClassifier"
        private const val MODEL = "meta-llama/llama-4-scout-17b-16e-instruct"

        private val PROMPT = """
            Ты — эксперт по еде и питанию. Проанализируй фотографию и определи, какая еда на ней изображена.

            Верни JSON-массив из 1-5 блюд, которые ты видишь на фото. Для каждого блюда укажи:
            - "name": название блюда на русском языке
            - "calories_per_100g": примерная калорийность на 100 грамм (число)
            - "confidence": уверенность в распознавании от 0.0 до 1.0

            Пример ответа:
            [
              {"name": "Борщ", "calories_per_100g": 45, "confidence": 0.95},
              {"name": "Хлеб чёрный", "calories_per_100g": 200, "confidence": 0.85}
            ]

            Если на фото нет еды, верни пустой массив [].
            Отвечай ТОЛЬКО валидным JSON, без дополнительного текста.
        """.trimIndent()
    }

    override fun initialize(): Boolean = true

    override fun isReady(): Boolean = true

    override fun needsDownload(): Boolean = false

    override suspend fun classifyAsync(bitmap: Bitmap): List<FoodPrediction> {
        return try {
            val base64Image = bitmapToBase64(bitmap)
            val dataUrl = "data:image/jpeg;base64,$base64Image"

            val request = GroqChatRequest(
                model = MODEL,
                messages = listOf(
                    GroqMessage(
                        role = "user",
                        content = listOf(
                            GroqImageContent(imageUrl = GroqImageUrl(url = dataUrl)),
                            GroqTextContent(text = PROMPT)
                        )
                    )
                )
            )

            val response = groqApi.chatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            val text = response.choices.firstOrNull()?.message?.content ?: return emptyList()
            parseResponse(text)
        } catch (e: Exception) {
            Log.e(TAG, "Groq classification failed", e)
            emptyList()
        }
    }

    override fun classify(bitmap: Bitmap): List<FoodPrediction> {
        throw UnsupportedOperationException("Use classifyAsync for Groq classifier")
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val bytes = outputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun parseResponse(text: String): List<FoodPrediction> {
        return try {
            val cleaned = text.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val jsonArray = JSONArray(cleaned)
            val predictions = mutableListOf<FoodPrediction>()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val name = item.getString("name")
                val calories = item.getInt("calories_per_100g")
                val confidence = item.getDouble("confidence").toFloat()

                predictions.add(
                    FoodPrediction(
                        label = name,
                        displayName = name,
                        confidence = confidence,
                        caloriesPer100g = calories
                    )
                )
            }

            predictions.sortedByDescending { it.confidence }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse Groq response: $text", e)
            emptyList()
        }
    }

    override fun close() {
        // No resources to release
    }
}
