package com.example.data.dao

import androidx.room.*
import com.example.data.model.ActivityLogEntry
import com.example.data.model.FoodLogEntry
import com.example.data.model.SavedFood
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM saved_foods ORDER BY name ASC")
    fun getSavedFoods(): Flow<List<SavedFood>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedFood(savedFood: SavedFood): Long

    @Delete
    suspend fun deleteSavedFood(savedFood: SavedFood)

    @Query("SELECT * FROM food_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getFoodLogsByDate(date: String): Flow<List<FoodLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodLog(entry: FoodLogEntry)

    @Delete
    suspend fun deleteFoodLog(entry: FoodLogEntry)

    @Query("SELECT * FROM food_logs WHERE date >= :startDate ORDER BY date ASC")
    fun getFoodLogsFrom(startDate: String): Flow<List<FoodLogEntry>>
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getActivityLogsByDate(date: String): Flow<List<ActivityLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(entry: ActivityLogEntry)

    @Delete
    suspend fun deleteActivityLog(entry: ActivityLogEntry)

    @Query("SELECT * FROM activity_logs WHERE date >= :startDate ORDER BY date ASC")
    fun getActivityLogsFrom(startDate: String): Flow<List<ActivityLogEntry>>
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)
}
