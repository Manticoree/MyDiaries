package ru.diaries.mydiaries.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// PreferencesManager uses @Inject constructor and is automatically provided by Hilt
// No need for explicit provider module
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    // Empty module for future data dependencies
}
