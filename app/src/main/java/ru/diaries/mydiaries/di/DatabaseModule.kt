package ru.diaries.mydiaries.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.diaries.mydiaries.data.local.DiaryDatabase
import ru.diaries.mydiaries.data.local.dao.AchievementDao
import ru.diaries.mydiaries.data.local.dao.DiaryDao
import ru.diaries.mydiaries.data.local.dao.ExpenseDao
import ru.diaries.mydiaries.data.local.dao.FoodDao
import ru.diaries.mydiaries.data.local.dao.PhotoDao
import ru.diaries.mydiaries.data.local.dao.TaskDao
import ru.diaries.mydiaries.data.local.dao.TrackDao
import ru.diaries.mydiaries.data.local.dao.UserProfileDao
import ru.diaries.mydiaries.data.local.dao.VideoDao
import ru.diaries.mydiaries.data.local.dao.WorkoutDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): DiaryDatabase {
        return Room.databaseBuilder(
            context,
            DiaryDatabase::class.java,
            "diary_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDiaryDao(database: DiaryDatabase): DiaryDao {
        return database.diaryDao()
    }

    @Provides
    @Singleton
    fun providePhotoDao(database: DiaryDatabase): PhotoDao {
        return database.photoDao()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: DiaryDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: DiaryDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideVideoDao(database: DiaryDatabase): VideoDao {
        return database.videoDao()
    }

    @Provides
    @Singleton
    fun provideFoodDao(database: DiaryDatabase): FoodDao {
        return database.foodDao()
    }

    @Provides
    @Singleton
    fun provideTrackDao(database: DiaryDatabase): TrackDao {
        return database.trackDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(database: DiaryDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    @Singleton
    fun provideAchievementDao(database: DiaryDatabase): AchievementDao {
        return database.achievementDao()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(database: DiaryDatabase): UserProfileDao {
        return database.userProfileDao()
    }
}
