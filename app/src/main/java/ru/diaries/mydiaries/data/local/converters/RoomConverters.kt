package ru.diaries.mydiaries.data.local.converters

import androidx.room.TypeConverter
import ru.diaries.mydiaries.data.local.entity.AchievementCategory
import java.time.LocalDate

/**
 * Room type converters for handling non-primitive types.
 */
class RoomConverters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromAchievementCategory(category: AchievementCategory?): String? {
        return category?.name
    }

    @TypeConverter
    fun toAchievementCategory(categoryName: String?): AchievementCategory? {
        return categoryName?.let { AchievementCategory.valueOf(it) }
    }
}
