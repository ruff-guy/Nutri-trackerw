package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SavedFood

@Composable
fun LogFoodDialog(
    food: SavedFood,
    onDismiss: () -> Unit,
    onLog: (servings: Float, mealType: String) -> Unit
) {
    var servingsStr by remember { mutableStateOf("1.0") }
    var selectedMealType by remember { mutableStateOf(food.category) }
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    val servingsVal = servingsStr.toFloatOrNull() ?: 1.0f
    val computedCalories = (food.caloriesPerServing * servingsVal).toInt()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Log ${food.name}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${food.caloriesPerServing} kcal per ${food.servingUnit}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Food Image Preview
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FoodImage(imageUri = food.imageUri, foodName = food.name, size = 64.dp)
                    Column {
                        Text(
                            text = "Calculated Total:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$computedCalories kcal",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Meal Category
                Text(
                    text = "Log to Meal",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mealTypes.forEach { meal ->
                        FilterChip(
                            selected = selectedMealType == meal,
                            onClick = { selectedMealType = meal },
                            label = { Text(meal) },
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }

                // Serving Multiplier Options
                Text(
                    text = "Servings / Portion",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { portion ->
                        FilterChip(
                            selected = servingsVal == portion,
                            onClick = { servingsStr = portion.toString() },
                            label = { Text("${portion}x") },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = servingsStr,
                    onValueChange = { servingsStr = it },
                    label = { Text("Custom Serving Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        onLog(servingsVal, selectedMealType)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Add to Calories Intake", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
