package ru.diaries.mydiaries.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.diaries.mydiaries.data.local.entity.UserProfileEntity

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profiles SET userName = :userName WHERE id = 1")
    suspend fun updateUserName(userName: String)

    @Query("UPDATE user_profiles SET totalSteps = totalSteps + :steps WHERE id = 1")
    suspend fun addTotalSteps(steps: Long)

    @Query("UPDATE user_profiles SET currentStreak = :streak WHERE id = 1")
    suspend fun updateCurrentStreak(streak: Int)

    @Query("UPDATE user_profiles SET longestStreak = MAX(longestStreak, :streak) WHERE id = 1")
    suspend fun updateLongestStreakIfGreater(streak: Int)
}
