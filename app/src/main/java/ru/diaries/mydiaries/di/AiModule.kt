package ru.diaries.mydiaries.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.diaries.mydiaries.feature.workout.data.ai.AiWorkoutGenerator
import ru.diaries.mydiaries.feature.workout.data.ai.GroqWorkoutGenerator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindAiWorkoutGenerator(
        groqWorkoutGenerator: GroqWorkoutGenerator
    ): AiWorkoutGenerator
}
