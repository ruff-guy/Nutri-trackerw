package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.ActivityLogEntry
import com.example.data.model.FoodLogEntry
import com.example.data.model.SavedFood
import com.example.data.model.UserProfile
import com.example.data.repository.NutriRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DailySummary(
    val date: String,
    val displayDate: String,
    val intakeCalories: Int,
    val burnedCalories: Int,
    val netCalories: Int,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float
)

data class DayChartData(
    val dayName: String,
    val dateStr: String,
    val intakeCalories: Int,
    val burnedCalories: Int
)

class NutriViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NutriRepository

    private val _selectedDate = MutableStateFlow(getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val savedFoods: StateFlow<List<SavedFood>>
    val userProfile: StateFlow<UserProfile>
    val foodLogsForSelectedDate: StateFlow<List<FoodLogEntry>>
    val activityLogsForSelectedDate: StateFlow<List<ActivityLogEntry>>

    val dailySummary: StateFlow<DailySummary>
    val weeklyChartData: StateFlow<List<DayChartData>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = NutriRepository(
            database.foodDao(),
            database.activityDao(),
            database.userDao()
        )

        savedFoods = repository.savedFoods.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        userProfile = repository.userProfile.map { profile ->
            profile ?: UserProfile(
                id = 1,
                targetCalories = 2200,
                targetProtein = 140,
                targetCarbs = 250,
                targetFat = 65
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserProfile()
        )

        @OptIn(ExperimentalCoroutinesApi::class)
        foodLogsForSelectedDate = _selectedDate.flatMapLatest { date ->
            repository.getFoodLogsByDate(date)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        @OptIn(ExperimentalCoroutinesApi::class)
        activityLogsForSelectedDate = _selectedDate.flatMapLatest { date ->
            repository.getActivityLogsByDate(date)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        dailySummary = combine(
            _selectedDate,
            foodLogsForSelectedDate,
            activityLogsForSelectedDate
        ) { date, foodLogs, actLogs ->
            val totalIntake = foodLogs.sumOf { it.calories }
            val totalBurned = actLogs.sumOf { it.caloriesBurned }
            val protein = foodLogs.sumOf { it.protein.toDouble() }.toFloat()
            val carbs = foodLogs.sumOf { it.carbs.toDouble() }.toFloat()
            val fat = foodLogs.sumOf { it.fat.toDouble() }.toFloat()

            DailySummary(
                date = date,
                displayDate = formatDisplayDate(date),
                intakeCalories = totalIntake,
                burnedCalories = totalBurned,
                netCalories = totalIntake - totalBurned,
                totalProtein = protein,
                totalCarbs = carbs,
                totalFat = fat
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DailySummary(
                date = getTodayDateString(),
                displayDate = "Today",
                intakeCalories = 0,
                burnedCalories = 0,
                netCalories = 0,
                totalProtein = 0f,
                totalCarbs = 0f,
                totalFat = 0f
            )
        )

        // 7-day chart data flow
        val sevenDaysAgoStr = getDateDaysAgo(6)
        weeklyChartData = combine(
            repository.getFoodLogsFrom(sevenDaysAgoStr),
            repository.getActivityLogsFrom(sevenDaysAgoStr)
        ) { foods, activities ->
            val last7Days = getLast7DateStrings()
            last7Days.map { dateStr ->
                val intake = foods.filter { it.date == dateStr }.sumOf { it.calories }
                val burned = activities.filter { it.date == dateStr }.sumOf { it.caloriesBurned }
                val dayLabel = formatShortDayLabel(dateStr)
                DayChartData(
                    dayName = dayLabel,
                    dateStr = dateStr,
                    intakeCalories = intake,
                    burnedCalories = burned
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun navigateDate(offsetDays: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        try {
            val current = sdf.parse(_selectedDate.value)
            if (current != null) {
                cal.time = current
            }
        } catch (_: Exception) {}
        cal.add(Calendar.DAY_OF_YEAR, offsetDays)
        _selectedDate.value = sdf.format(cal.time)
    }

    fun saveFoodItem(savedFood: SavedFood) {
        viewModelScope.launch {
            repository.saveFoodItem(savedFood)
        }
    }

    fun addCustomFood(
        name: String,
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float,
        servingUnit: String,
        category: String,
        imageUri: String?
    ) {
        viewModelScope.launch {
            val savedFood = SavedFood(
                name = name,
                caloriesPerServing = calories,
                protein = protein,
                carbs = carbs,
                fat = fat,
                servingUnit = servingUnit,
                category = category,
                imageUri = imageUri
            )
            repository.saveFoodItem(savedFood)
        }
    }

    fun logFoodIntake(savedFood: SavedFood, servings: Float = 1.0f, mealType: String = savedFood.category) {
        viewModelScope.launch {
            val entry = FoodLogEntry(
                date = _selectedDate.value,
                foodName = savedFood.name,
                mealType = mealType,
                servings = servings,
                calories = (savedFood.caloriesPerServing * servings).toInt(),
                protein = savedFood.protein * servings,
                carbs = savedFood.carbs * servings,
                fat = savedFood.fat * servings,
                imageUri = savedFood.imageUri
            )
            repository.logFoodIntake(entry)
        }
    }

    fun deleteFoodLog(entry: FoodLogEntry) {
        viewModelScope.launch {
            repository.deleteFoodLog(entry)
        }
    }

    fun deleteSavedFood(savedFood: SavedFood) {
        viewModelScope.launch {
            repository.deleteSavedFood(savedFood)
        }
    }

    fun logActivity(
        activityName: String,
        durationMins: Int,
        stepsCount: Int = 0,
        caloriesBurned: Int
    ) {
        viewModelScope.launch {
            val entry = ActivityLogEntry(
                date = _selectedDate.value,
                activityName = activityName,
                durationMins = durationMins,
                stepsCount = stepsCount,
                caloriesBurned = caloriesBurned
            )
            repository.logActivity(entry)
        }
    }

    fun logSteps(stepsCount: Int) {
        // Standard formula ~40 kcal per 1,000 steps
        val burned = (stepsCount * 0.04f).toInt()
        logActivity(
            activityName = "Daily Steps ($stepsCount steps)",
            durationMins = 0,
            stepsCount = stepsCount,
            caloriesBurned = burned
        )
    }

    fun deleteActivityLog(entry: ActivityLogEntry) {
        viewModelScope.launch {
            repository.deleteActivityLog(entry)
        }
    }

    fun updateGoals(targetCalories: Int, protein: Int, carbs: Int, fat: Int) {
        viewModelScope.launch {
            repository.updateProfile(
                UserProfile(
                    id = 1,
                    targetCalories = targetCalories,
                    targetProtein = protein,
                    targetCarbs = carbs,
                    targetFat = fat
                )
            )
        }
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

        fun formatDisplayDate(dateStr: String): String {
            if (dateStr == getTodayDateString()) return "Today"
            val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sdfOutput = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            return try {
                val date = sdfInput.parse(dateStr)
                if (date != null) sdfOutput.format(date) else dateStr
            } catch (e: Exception) {
                dateStr
            }
        }

        fun formatShortDayLabel(dateStr: String): String {
            val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sdfOutput = SimpleDateFormat("EEE", Locale.getDefault())
            return try {
                val date = sdfInput.parse(dateStr)
                if (date != null) sdfOutput.format(date) else dateStr
            } catch (e: Exception) {
                dateStr
            }
        }

        fun getDateDaysAgo(daysAgo: Int): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            return sdf.format(cal.time)
        }

        fun getLast7DateStrings(): List<String> {
            val list = mutableListOf<String>()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -6)
            for (i in 0..6) {
                list.add(sdf.format(cal.time))
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            return list
        }
    }
}
