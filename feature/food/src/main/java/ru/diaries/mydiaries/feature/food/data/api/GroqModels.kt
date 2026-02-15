package ru.diaries.mydiaries.feature.food.data.api

import com.google.gson.annotations.SerializedName

data class GroqChatRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Double = 0.3,
    @SerializedName("max_tokens") val maxTokens: Int = 1024
)

data class GroqMessage(
    val role: String,
    val content: List<GroqContent>
)

sealed class GroqContent {
    abstract val type: String
}

data class GroqTextContent(
    override val type: String = "text",
    val text: String
) : GroqContent()

data class GroqImageContent(
    override val type: String = "image_url",
    @SerializedName("image_url") val imageUrl: GroqImageUrl
) : GroqContent()

data class GroqImageUrl(
    val url: String
)

data class GroqChatResponse(
    val choices: List<GroqChoice>
)

data class GroqChoice(
    val message: GroqResponseMessage
)

data class GroqResponseMessage(
    val content: String
)
