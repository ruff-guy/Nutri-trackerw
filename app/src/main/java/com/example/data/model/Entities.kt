package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_foods")
data class SavedFood(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val caloriesPerServing: Int,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val servingUnit: String = "1 serving",
    val imageUri: String? = null,
    val category: String = "Snack"
)

@Entity(tableName = "food_logs")
data class FoodLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // ISO Format YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val foodName: String,
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val servings: Float = 1.0f,
    val calories: Int,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val imageUri: String? = null
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val activityName: String, // e.g. "Run", "Jog", "Gym 2hr", "Steps"
    val durationMins: Int = 0,
    val stepsCount: Int = 0,
    val caloriesBurned: Int
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val targetCalories: Int = 2000,
    val targetProtein: Int = 120,
    val targetCarbs: Int = 250,
    val targetFat: Int = 65
)
