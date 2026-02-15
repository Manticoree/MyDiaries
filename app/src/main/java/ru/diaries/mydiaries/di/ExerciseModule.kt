package ru.diaries.mydiaries.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.diaries.mydiaries.feature.workout.data.local.ExerciseBrowserRepository
import ru.diaries.mydiaries.feature.workout.data.local.LocalExerciseBrowserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExerciseModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideExerciseBrowserRepository(
        impl: LocalExerciseBrowserRepository
    ): ExerciseBrowserRepository = impl
}
