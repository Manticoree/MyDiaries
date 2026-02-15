package ru.diaries.mydiaries.feature.workout.data.ai

import org.json.JSONObject
import ru.diaries.mydiaries.feature.food.data.api.GroqApi
import ru.diaries.mydiaries.feature.food.data.api.GroqChatRequest
import ru.diaries.mydiaries.feature.food.data.api.GroqMessage
import ru.diaries.mydiaries.feature.food.data.api.GroqTextContent
import ru.diaries.mydiaries.feature.workout.data.model.Exercise
import ru.diaries.mydiaries.feature.workout.data.model.ExerciseSet
import ru.diaries.mydiaries.feature.workout.data.model.ExerciseType
import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutType
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

class GroqWorkoutGenerator @Inject constructor(
    private val groqApi: GroqApi,
    @Named("groq_api_key") private val apiKey: String
) : AiWorkoutGenerator {

    companion object {
        private const val MODEL = "meta-llama/llama-4-scout-17b-16e-instruct"
    }

    override suspend fun generateWorkout(history: List<Workout>): Result<Workout> {
        return try {
            val prompt = buildPrompt(history)

            val request = GroqChatRequest(
                model = MODEL,
                messages = listOf(
                    GroqMessage(
                        role = "user",
                        content = listOf(GroqTextContent(text = prompt))
                    )
                ),
                maxTokens = 2048
            )

            val response = groqApi.chatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            val text = response.choices.firstOrNull()?.message?.content
            if (text.isNullOrBlank()) {
                return Result.failure(IllegalStateException("Empty response from Groq"))
            }

            val workout = parseWorkoutFromJson(text)
            Result.success(workout)
        } catch (e: Exception) {
            Result.failure(
                IllegalStateException("AI generation error: ${e::class.simpleName}: ${e.message}", e)
            )
        }
    }

    private fun buildPrompt(history: List<Workout>): String {
        val historyJson = if (history.isEmpty()) {
            "No workout history available."
        } else {
            history.takeLast(10).joinToString(",\n") { workout ->
                buildString {
                    append("{")
                    append("\"name\":\"${workout.name}\",")
                    append("\"type\":\"${workout.type.name}\",")
                    append("\"date\":\"${workout.date}\",")
                    append("\"durationMinutes\":${workout.durationMinutes},")
                    append("\"exercises\":[")
                    append(workout.exercises.joinToString(",") { ex ->
                        buildString {
                            append("{\"name\":\"${ex.name}\",\"type\":\"${ex.type.name}\",")
                            append("\"sets\":[")
                            append(ex.sets.joinToString(",") { set ->
                                "{\"reps\":${set.reps},\"weight\":${set.weight},\"durationSeconds\":${set.durationSeconds}}"
                            })
                            append("]}")
                        }
                    })
                    append("]}")
                }
            }
        }

        return """
            You are a professional fitness coach AI. Based on the user's workout history, generate the next optimal workout.

            Consider:
            - Progressive overload (gradually increase weight/reps)
            - Muscle group rotation (don't train the same muscles on consecutive days)
            - Balance between strength and cardio
            - If no history, generate a balanced full-body workout for a beginner

            Workout history (last 10):
            [$historyJson]

            Respond ONLY with a valid JSON object (no markdown, no extra text):
            {
              "name": "Workout name in Russian",
              "type": "STRENGTH" or "CARDIO" or "MIXED",
              "durationMinutes": estimated duration,
              "exercises": [
                {
                  "name": "Exercise name in Russian",
                  "type": "STRENGTH" or "CARDIO",
                  "sets": [
                    {"reps": number, "weight": number_kg, "durationSeconds": 0},
                    ...
                  ]
                },
                ...
              ]
            }

            Generate 4-6 exercises with 3-4 sets each.
        """.trimIndent()
    }

    private fun parseWorkoutFromJson(responseText: String): Workout {
        val jsonString = extractJsonFromResponse(responseText)
        val json = JSONObject(jsonString)
        val workoutId = UUID.randomUUID().toString()

        val exercisesArray = json.getJSONArray("exercises")
        val exercises = (0 until exercisesArray.length()).map { i ->
            val exJson = exercisesArray.getJSONObject(i)
            val exerciseId = UUID.randomUUID().toString()
            val setsArray = exJson.getJSONArray("sets")

            Exercise(
                id = exerciseId,
                workoutId = workoutId,
                name = exJson.getString("name"),
                type = parseExerciseType(exJson.optString("type", "STRENGTH")),
                order = i,
                sets = (0 until setsArray.length()).map { j ->
                    val setJson = setsArray.getJSONObject(j)
                    ExerciseSet(
                        id = UUID.randomUUID().toString(),
                        exerciseId = exerciseId,
                        setNumber = j + 1,
                        reps = setJson.optInt("reps", 0),
                        weight = setJson.optDouble("weight", 0.0),
                        durationSeconds = setJson.optInt("durationSeconds", 0)
                    )
                }
            )
        }

        return Workout(
            id = workoutId,
            name = json.getString("name"),
            date = LocalDate.now(),
            type = parseWorkoutType(json.optString("type", "STRENGTH")),
            durationMinutes = json.optInt("durationMinutes", 45),
            exercises = exercises
        )
    }

    private fun extractJsonFromResponse(text: String): String {
        val trimmed = text.trim()
        if (trimmed.startsWith("{")) return trimmed

        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')
        if (start != -1 && end != -1 && end > start) {
            return trimmed.substring(start, end + 1)
        }
        throw IllegalStateException("Could not find JSON in AI response")
    }

    private fun parseWorkoutType(type: String): WorkoutType = when (type.uppercase()) {
        "CARDIO" -> WorkoutType.CARDIO
        "MIXED" -> WorkoutType.MIXED
        else -> WorkoutType.STRENGTH
    }

    private fun parseExerciseType(type: String): ExerciseType = when (type.uppercase()) {
        "CARDIO" -> ExerciseType.CARDIO
        else -> ExerciseType.STRENGTH
    }
}
