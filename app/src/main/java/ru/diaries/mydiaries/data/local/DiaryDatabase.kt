package ru.diaries.mydiaries.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.diaries.mydiaries.data.local.dao.DiaryDao
import ru.diaries.mydiaries.data.local.dao.ExpenseDao
import ru.diaries.mydiaries.data.local.dao.PhotoDao
import ru.diaries.mydiaries.data.local.entity.DiaryEntryEntity
import ru.diaries.mydiaries.data.local.entity.ExpenseEntity
import ru.diaries.mydiaries.data.local.entity.PhotoEntity

@Database(
    entities = [DiaryEntryEntity::class, PhotoEntity::class, ExpenseEntity::class],
    version = 3,
    exportSchema = false
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
    abstract fun photoDao(): PhotoDao
    abstract fun expenseDao(): ExpenseDao
}
