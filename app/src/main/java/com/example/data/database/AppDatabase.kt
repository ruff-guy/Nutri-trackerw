package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ActivityDao
import com.example.data.dao.FoodDao
import com.example.data.dao.UserDao
import com.example.data.model.ActivityLogEntry
import com.example.data.model.FoodLogEntry
import com.example.data.model.SavedFood
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SavedFood::class, FoodLogEntry::class, ActivityLogEntry::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun activityDao(): ActivityDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutritrack_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            suspend fun populateInitialData(db: AppDatabase) {
                // Initial Default User Profile
                db.userDao().insertOrUpdateProfile(
                    UserProfile(
                        id = 1,
                        targetCalories = 2200,
                        targetProtein = 140,
                        targetCarbs = 250,
                        targetFat = 65
                    )
                )

                // Pre-populated Default Lifestyle Foods
                val initialFoods = listOf(
                    SavedFood(
                        name = "Avocado Toast with Egg",
                        caloriesPerServing = 320,
                        protein = 12f,
                        carbs = 28f,
                        fat = 18f,
                        servingUnit = "1 slice",
                        category = "Breakfast",
                        imageUri = "preset_avocado_toast"
                    ),
                    SavedFood(
                        name = "Oatmeal with Honey & Berries",
                        caloriesPerServing = 280,
                        protein = 9f,
                        carbs = 52f,
                        fat = 5f,
                        servingUnit = "1 bowl (250g)",
                        category = "Breakfast",
                        imageUri = "preset_oatmeal"
                    ),
                    SavedFood(
                        name = "Grilled Chicken Breast & Rice",
                        caloriesPerServing = 480,
                        protein = 42f,
                        carbs = 45f,
                        fat = 8f,
                        servingUnit = "1 plate (350g)",
                        category = "Lunch",
                        imageUri = "preset_chicken_rice"
                    ),
                    SavedFood(
                        name = "Salmon Quinoa Bowl",
                        caloriesPerServing = 540,
                        protein = 38f,
                        carbs = 42f,
                        fat = 22f,
                        servingUnit = "1 bowl (300g)",
                        category = "Dinner",
                        imageUri = "preset_salmon_bowl"
                    ),
                    SavedFood(
                        name = "Greek Yogurt with Almonds",
                        caloriesPerServing = 210,
                        protein = 18f,
                        carbs = 14f,
                        fat = 9f,
                        servingUnit = "1 cup (200g)",
                        category = "Snack",
                        imageUri = "preset_yogurt"
                    ),
                    SavedFood(
                        name = "Whey Protein Shake",
                        caloriesPerServing = 160,
                        protein = 28f,
                        carbs = 4f,
                        fat = 2.5f,
                        servingUnit = "1 scoop + water",
                        category = "Snack",
                        imageUri = "preset_protein_shake"
                    ),
                    SavedFood(
                        name = "Fresh Red Apple",
                        caloriesPerServing = 95,
                        protein = 0.5f,
                        carbs = 25f,
                        fat = 0.3f,
                        servingUnit = "1 medium apple",
                        category = "Snack",
                        imageUri = "preset_apple"
                    ),
                    SavedFood(
                        name = "Caesar Salad with Croutons",
                        caloriesPerServing = 310,
                        protein = 10f,
                        carbs = 18f,
                        fat = 22f,
                        servingUnit = "1 bowl",
                        category = "Lunch",
                        imageUri = "preset_salad"
                    )
                )

                for (food in initialFoods) {
                    db.foodDao().insertSavedFood(food)
                }
            }
        }
    }
}
