package ru.diaries.mydiaries.di

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.diaries.mydiaries.BuildConfig
import ru.diaries.mydiaries.feature.food.data.api.GroqApi
import ru.diaries.mydiaries.feature.food.data.api.GroqContent
import ru.diaries.mydiaries.feature.food.data.api.GroqImageContent
import ru.diaries.mydiaries.feature.food.data.api.GroqTextContent
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GroqRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @GroqRetrofit
    fun provideGroqRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .registerTypeAdapter(GroqContent::class.java, GroqContentSerializer())
            .create()

        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideGroqApi(@GroqRetrofit retrofit: Retrofit): GroqApi {
        return retrofit.create(GroqApi::class.java)
    }

    @Provides
    @Singleton
    @Named("groq_api_key")
    fun provideGroqApiKey(): String {
        return BuildConfig.GROQ_API_KEY
    }
}

private class GroqContentSerializer : JsonSerializer<GroqContent>, JsonDeserializer<GroqContent> {

    override fun serialize(src: GroqContent, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val obj = JsonObject()
        obj.addProperty("type", src.type)
        when (src) {
            is GroqTextContent -> obj.addProperty("text", src.text)
            is GroqImageContent -> obj.add("image_url", context.serialize(src.imageUrl))
        }
        return obj
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): GroqContent {
        val obj = json.asJsonObject
        val type = obj.get("type").asString
        return when (type) {
            "text" -> GroqTextContent(text = obj.get("text").asString)
            "image_url" -> context.deserialize(json, GroqImageContent::class.java)
            else -> throw IllegalArgumentException("Unknown GroqContent type: $type")
        }
    }
}
