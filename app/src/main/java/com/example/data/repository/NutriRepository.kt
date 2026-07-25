package com.example.data.repository

import com.example.data.dao.ActivityDao
import com.example.data.dao.FoodDao
import com.example.data.dao.UserDao
import com.example.data.model.ActivityLogEntry
import com.example.data.model.FoodLogEntry
import com.example.data.model.SavedFood
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

class NutriRepository(
    private val foodDao: FoodDao,
    private val activityDao: ActivityDao,
    private val userDao: UserDao
) {
    val savedFoods: Flow<List<SavedFood>> = foodDao.getSavedFoods()
    val userProfile: Flow<UserProfile?> = userDao.getUserProfile()

    fun getFoodLogsByDate(date: String): Flow<List<FoodLogEntry>> =
        foodDao.getFoodLogsByDate(date)

    fun getActivityLogsByDate(date: String): Flow<List<ActivityLogEntry>> =
        activityDao.getActivityLogsByDate(date)

    fun getFoodLogsFrom(startDate: String): Flow<List<FoodLogEntry>> =
        foodDao.getFoodLogsFrom(startDate)

    fun getActivityLogsFrom(startDate: String): Flow<List<ActivityLogEntry>> =
        activityDao.getActivityLogsFrom(startDate)

    suspend fun saveFoodItem(savedFood: SavedFood): Long =
        foodDao.insertSavedFood(savedFood)

    suspend fun deleteSavedFood(savedFood: SavedFood) =
        foodDao.deleteSavedFood(savedFood)

    suspend fun logFoodIntake(entry: FoodLogEntry) =
        foodDao.insertFoodLog(entry)

    suspend fun deleteFoodLog(entry: FoodLogEntry) =
        foodDao.deleteFoodLog(entry)

    suspend fun logActivity(entry: ActivityLogEntry) =
        activityDao.insertActivityLog(entry)

    suspend fun deleteActivityLog(entry: ActivityLogEntry) =
        activityDao.deleteActivityLog(entry)

    suspend fun updateProfile(profile: UserProfile) =
        userDao.insertOrUpdateProfile(profile)
}
