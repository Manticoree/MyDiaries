package ru.diaries.mydiaries.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.diaries.mydiaries.data.local.dao.DiaryDao
import ru.diaries.mydiaries.data.local.dao.ExpenseDao
import ru.diaries.mydiaries.data.local.dao.PhotoDao
import ru.diaries.mydiaries.data.local.dao.TaskDao
import ru.diaries.mydiaries.data.local.entity.DiaryEntryEntity
import ru.diaries.mydiaries.data.local.entity.ExpenseEntity
import ru.diaries.mydiaries.data.local.entity.PhotoEntity
import ru.diaries.mydiaries.data.local.entity.TaskEntity

@Database(
    entities = [DiaryEntryEntity::class, PhotoEntity::class, ExpenseEntity::class, TaskEntity::class],
    version = 4,
    exportSchema = false
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
    abstract fun photoDao(): PhotoDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun taskDao(): TaskDao
}
