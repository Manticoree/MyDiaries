package ru.diaries.mydiaries.feature.workout.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ru.diaries.mydiaries.feature.workout.data.api.WgerCategory
import ru.diaries.mydiaries.feature.workout.data.api.WgerExerciseInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalExerciseBrowserRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : ExerciseBrowserRepository {

    private var cachedExercises: List<ExerciseDbJson>? = null
    private var cachedUiModels: List<WgerExerciseInfo>? = null
    private var cachedCategories: List<WgerCategory>? = null
    private var idLookup: Map<Int, WgerExerciseInfo>? = null
    private val mutex = Mutex()

    private suspend fun ensureLoaded() {
        if (cachedExercises != null) return
        mutex.withLock {
            if (cachedExercises != null) return
            withContext(Dispatchers.IO) {
                val json = context.assets.open("exercises.json")
                    .bufferedReader()
                    .use { it.readText() }
                val type = object : TypeToken<List<ExerciseDbJson>>() {}.type
                val exercises: List<ExerciseDbJson> = gson.fromJson(json, type)
                val uiModels = exercises.map { it.toWgerExerciseInfo() }

                cachedExercises = exercises
                cachedUiModels = uiModels
                cachedCategories = exercises.extractCategories()
                idLookup = uiModels.associateBy { it.id }
            }
        }
    }

    override suspend fun getCategories(): List<WgerCategory> {
        ensureLoaded()
        return cachedCategories.orEmpty()
    }

    override suspend fun getExercises(category: Int?, limit: Int, offset: Int): ExercisePage {
        ensureLoaded()
        val all = if (category == null) {
            cachedUiModels.orEmpty()
        } else {
            cachedUiModels.orEmpty().filter { it.category?.id == category }
        }
        val end = (offset + limit).coerceAtMost(all.size)
        val page = if (offset < all.size) all.subList(offset, end) else emptyList()
        return ExercisePage(
            exercises = page,
            hasMore = end < all.size
        )
    }

    override suspend fun searchExercises(term: String): List<WgerExerciseInfo> {
        ensureLoaded()
        val lowerTerm = term.lowercase()
        return cachedUiModels.orEmpty().filter { exercise ->
            exercise.name.lowercase().contains(lowerTerm)
        }
    }

    override suspend fun getExerciseById(id: Int): WgerExerciseInfo? {
        ensureLoaded()
        return idLookup?.get(id)
    }
}
