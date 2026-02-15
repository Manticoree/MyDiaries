package ru.diaries.mydiaries.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.diaries.mydiaries.data.local.dao.DiaryDao
import ru.diaries.mydiaries.data.local.dao.ExpenseDao
import ru.diaries.mydiaries.data.local.dao.FoodDao
import ru.diaries.mydiaries.data.local.dao.PhotoDao
import ru.diaries.mydiaries.data.local.dao.TaskDao
import ru.diaries.mydiaries.data.local.dao.TrackDao
import ru.diaries.mydiaries.data.local.dao.VideoDao
import ru.diaries.mydiaries.data.local.dao.WorkoutDao
import ru.diaries.mydiaries.data.local.entity.DailyTrackEntity
import ru.diaries.mydiaries.data.local.entity.DiaryEntryEntity
import ru.diaries.mydiaries.data.local.entity.ExerciseEntity
import ru.diaries.mydiaries.data.local.entity.ExerciseSetEntity
import ru.diaries.mydiaries.data.local.entity.ExpenseEntity
import ru.diaries.mydiaries.data.local.entity.FoodEntity
import ru.diaries.mydiaries.data.local.entity.LocationPointEntity
import ru.diaries.mydiaries.data.local.entity.PhotoEntity
import ru.diaries.mydiaries.data.local.entity.TaskEntity
import ru.diaries.mydiaries.data.local.entity.VideoEntity
import ru.diaries.mydiaries.data.local.entity.WorkoutEntity

@Database(
    entities = [DiaryEntryEntity::class, PhotoEntity::class, ExpenseEntity::class, TaskEntity::class, VideoEntity::class, FoodEntity::class, DailyTrackEntity::class, LocationPointEntity::class, WorkoutEntity::class, ExerciseEntity::class, ExerciseSetEntity::class],
    version = 8,
    exportSchema = false
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
    abstract fun photoDao(): PhotoDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun taskDao(): TaskDao
    abstract fun videoDao(): VideoDao
    abstract fun foodDao(): FoodDao
    abstract fun trackDao(): TrackDao
    abstract fun workoutDao(): WorkoutDao
}
