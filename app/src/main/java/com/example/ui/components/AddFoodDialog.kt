package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SavedFood

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodDialog(
    foodToEdit: SavedFood? = null,
    onDismiss: () -> Unit,
    onSave: (savedFood: SavedFood) -> Unit
) {
    var name by remember { mutableStateOf(foodToEdit?.name ?: "") }
    var caloriesStr by remember { mutableStateOf(foodToEdit?.caloriesPerServing?.toString() ?: "") }
    var proteinStr by remember { mutableStateOf(if ((foodToEdit?.protein ?: 0f) > 0f) foodToEdit?.protein.toString() else "") }
    var carbsStr by remember { mutableStateOf(if ((foodToEdit?.carbs ?: 0f) > 0f) foodToEdit?.carbs.toString() else "") }
    var fatStr by remember { mutableStateOf(if ((foodToEdit?.fat ?: 0f) > 0f) foodToEdit?.fat.toString() else "") }
    var servingUnit by remember { mutableStateOf(foodToEdit?.servingUnit ?: "1 serving") }
    var selectedCategory by remember { mutableStateOf(foodToEdit?.category ?: "Breakfast") }
    var imageUriStr by remember { mutableStateOf<String?>(foodToEdit?.imageUri) }

    val categories = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUriStr = uri.toString()
        }
    }

    val titleText = if (foodToEdit != null) "Edit Food Item" else "Add Custom Food"
    val submitText = if (foodToEdit != null) "Update Food Item" else "Save Food Item"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Image Picker Preview Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!imageUriStr.isNullOrBlank()) {
                        FoodImage(
                            imageUri = imageUriStr,
                            foodName = name.ifBlank { "Food" },
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change Image",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Select Photo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Tap to choose/change food photo",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Food Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food Name *") },
                    placeholder = { Text("e.g. Salmon Poke Bowl") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Category Selection
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategory.equals(category, ignoreCase = true),
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }

                // Calories and Serving Unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = caloriesStr,
                        onValueChange = { caloriesStr = it },
                        label = { Text("Calories (kcal) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = servingUnit,
                        onValueChange = { servingUnit = it },
                        label = { Text("Serving Unit") },
                        placeholder = { Text("1 bowl") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Macros Section
                Text(
                    text = "Macronutrients (Optional)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = proteinStr,
                        onValueChange = { proteinStr = it },
                        label = { Text("Protein (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = carbsStr,
                        onValueChange = { carbsStr = it },
                        label = { Text("Carbs (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = fatStr,
                        onValueChange = { fatStr = it },
                        label = { Text("Fat (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Actions
                Button(
                    onClick = {
                        val cal = caloriesStr.toIntOrNull() ?: 0
                        val p = proteinStr.toFloatOrNull() ?: 0f
                        val c = carbsStr.toFloatOrNull() ?: 0f
                        val f = fatStr.toFloatOrNull() ?: 0f
                        if (name.isNotBlank() && cal > 0) {
                            val savedFood = SavedFood(
                                id = foodToEdit?.id ?: 0L,
                                name = name,
                                caloriesPerServing = cal,
                                protein = p,
                                carbs = c,
                                fat = f,
                                servingUnit = servingUnit.ifBlank { "1 serving" },
                                category = selectedCategory,
                                imageUri = imageUriStr
                            )
                            onSave(savedFood)
                            onDismiss()
                        }
                    },
                    enabled = name.isNotBlank() && (caloriesStr.toIntOrNull() ?: 0) > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(submitText, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

