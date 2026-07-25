package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.FoodLogEntry
import com.example.data.model.SavedFood
import com.example.ui.components.AddFoodDialog
import com.example.ui.components.FoodImage
import com.example.ui.components.LogFoodDialog
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.DailySummary
import com.example.ui.viewmodel.NutriViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntakeScreen(
    viewModel: NutriViewModel,
    dailySummary: DailySummary,
    savedFoods: List<SavedFood>,
    foodLogs: List<FoodLogEntry>,
    targetCalories: Int,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showAddFoodDialog by remember { mutableStateOf(false) }
    var foodToEdit by remember { mutableStateOf<SavedFood?>(null) }
    var foodToLog by remember { mutableStateOf<SavedFood?>(null) }

    val categories = listOf("All", "Breakfast", "Lunch", "Dinner", "Snack")

    val filteredSavedFoods = savedFoods.filter { food ->
        val matchesSearch = food.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryFilter == "All" || food.category.equals(selectedCategoryFilter, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddFoodDialog = true },
                icon = { Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add Food Item") },
                text = { Text("Add Custom Food") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_custom_food_fab")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 88.dp, start = 16.dp, end = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Selector Bar
            item {
                DateSelectorRow(
                    displayDate = dailySummary.displayDate,
                    onPrev = { viewModel.navigateDate(-1) },
                    onNext = { viewModel.navigateDate(1) },
                    onToday = { viewModel.setSelectedDate(NutriViewModel.getTodayDateString()) }
                )
            }

            // Intake Summary Header Card
            item {
                IntakeHeaderCard(
                    intakeCalories = dailySummary.intakeCalories,
                    targetCalories = targetCalories,
                    protein = dailySummary.totalProtein,
                    carbs = dailySummary.totalCarbs,
                    fat = dailySummary.totalFat
                )
            }

            // Logged Foods Section for Selected Date
            item {
                Text(
                    text = "Today's Consumed Foods (${foodLogs.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (foodLogs.isEmpty()) {
                item {
                    EmptyLogCard(
                        title = "No Foods Logged Yet",
                        subtitle = "Select a food item below or create a custom food to log your intake for ${dailySummary.displayDate}."
                    )
                }
            } else {
                items(foodLogs, key = { it.id }) { log ->
                    LoggedFoodCard(log = log, onDelete = { viewModel.deleteFoodLog(log) })
                }
            }

            // Food Library Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Food Library & Saved Items",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tap any food item to add it to your daily intake",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Search Bar & Filter Chips
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search foods, meals, recipes...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("food_search_input")
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategoryFilter == category,
                            onClick = { selectedCategoryFilter = category },
                            label = { Text(category) },
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // Saved Foods List
            if (filteredSavedFoods.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No matching foods found",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap '+ Add Custom Food' below to save a new item with photo!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredSavedFoods, key = { it.id }) { food ->
                    SavedFoodItemRow(
                        food = food,
                        onSelect = { foodToLog = food },
                        onEdit = { foodToEdit = food },
                        onDelete = { viewModel.deleteSavedFood(food) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddFoodDialog) {
        AddFoodDialog(
            foodToEdit = null,
            onDismiss = { showAddFoodDialog = false },
            onSave = { savedFood ->
                viewModel.saveFoodItem(savedFood)
            }
        )
    }

    foodToEdit?.let { food ->
        AddFoodDialog(
            foodToEdit = food,
            onDismiss = { foodToEdit = null },
            onSave = { updatedFood ->
                viewModel.saveFoodItem(updatedFood)
            }
        )
    }

    foodToLog?.let { food ->
        LogFoodDialog(
            food = food,
            onDismiss = { foodToLog = null },
            onLog = { servings, mealType ->
                viewModel.logFoodIntake(food, servings, mealType)
            }
        )
    }
}

@Composable
fun DateSelectorRow(
    displayDate: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrev) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToday() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = onNext) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Day")
            }
        }
    }
}

@Composable
fun IntakeHeaderCard(
    intakeCalories: Int,
    targetCalories: Int,
    protein: Float,
    carbs: Float,
    fat: Float
) {
    val progress = if (targetCalories > 0) (intakeCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f) else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Calories Intake",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$intakeCalories",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "/ $targetCalories kcal goal",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RestaurantMenu,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = EmeraldPrimary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
            )

            // Macro summary chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroPill(label = "Protein", value = "${protein.toInt()}g", color = Color(0xFF3B82F6))
                MacroPill(label = "Carbs", value = "${carbs.toInt()}g", color = Color(0xFFEAB308))
                MacroPill(label = "Fat", value = "${fat.toInt()}g", color = Color(0xFFEC4899))
            }
        }
    }
}

@Composable
fun MacroPill(label: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun LoggedFoodCard(
    log: FoodLogEntry,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FoodImage(imageUri = log.imageUri, foodName = log.foodName, size = 52.dp)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.foodName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${log.mealType} • ${log.servings}x serving",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "P: ${log.protein.toInt()}g  C: ${log.carbs.toInt()}g  F: ${log.fat.toInt()}g",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${log.calories} kcal",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldPrimary
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Log",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SavedFoodItemRow(
    food: SavedFood,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FoodImage(imageUri = food.imageUri, foodName = food.name, size = 60.dp)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${food.caloriesPerServing} kcal / ${food.servingUnit}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "P: ${food.protein.toInt()}g · C: ${food.carbs.toInt()}g · F: ${food.fat.toInt()}g",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• ${food.category}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Food Item",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Button(
                    onClick = onSelect,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log")
                }
            }
        }
    }
}

@Composable
fun EmptyLogCard(title: String, subtitle: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Fastfood,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
