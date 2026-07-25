package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ActivityLogEntry
import com.example.ui.theme.CoralBurned
import com.example.ui.viewmodel.DailySummary
import com.example.ui.viewmodel.NutriViewModel

data class ActivityPreset(
    val name: String,
    val icon: ImageVector,
    val calPerMin: Float,
    val defaultMins: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BurnedScreen(
    viewModel: NutriViewModel,
    dailySummary: DailySummary,
    activityLogs: List<ActivityLogEntry>,
    modifier: Modifier = Modifier
) {
    var stepInputStr by remember { mutableStateOf("") }
    var showCustomActivityDialog by remember { mutableStateOf(false) }
    var selectedPresetToLog by remember { mutableStateOf<ActivityPreset?>(null) }

    val stepsCount = stepInputStr.toIntOrNull() ?: 0
    val computedStepCalories = (stepsCount * 0.04f).toInt()

    val presets = listOf(
        ActivityPreset("Run", Icons.Default.DirectionsRun, calPerMin = 10f, defaultMins = 30),
        ActivityPreset("Jog", Icons.Default.DirectionsWalk, calPerMin = 7.5f, defaultMins = 30),
        ActivityPreset("Gym 2hr", Icons.Default.FitnessCenter, calPerMin = 5f, defaultMins = 120),
        ActivityPreset("Cycling", Icons.Default.DirectionsBike, calPerMin = 8f, defaultMins = 45),
        ActivityPreset("Swimming", Icons.Default.Pool, calPerMin = 9f, defaultMins = 30),
        ActivityPreset("Brisk Walk", Icons.Default.Hiking, calPerMin = 4.5f, defaultMins = 40)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCustomActivityDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Activity") },
                text = { Text("Log Custom Activity") },
                containerColor = CoralBurned,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_custom_activity_fab")
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

            // Burned Header Summary Card
            item {
                BurnedHeaderCard(burnedCalories = dailySummary.burnedCalories)
            }

            // Steps Calculator Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(CoralBurned.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    tint = CoralBurned
                                )
                            }
                            Column {
                                Text(
                                    text = "Step Counter Calculator",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Enter your steps count to auto-calculate burned calories",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = stepInputStr,
                                onValueChange = { stepInputStr = it },
                                placeholder = { Text("e.g. 8000 steps") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("steps_input_field")
                            )

                            Button(
                                onClick = {
                                    if (stepsCount > 0) {
                                        viewModel.logSteps(stepsCount)
                                        stepInputStr = ""
                                    }
                                },
                                enabled = stepsCount > 0,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CoralBurned),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Text("Log Steps", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (stepsCount > 0) {
                            Text(
                                text = "Estimated Calories Burned: ~$computedStepCalories kcal",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = CoralBurned
                            )
                        }
                    }
                }
            }

            // Quick Activity Presets
            item {
                Text(
                    text = "Popular Activities & Workouts",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(presets) { preset ->
                        PresetActivityCard(
                            preset = preset,
                            onClick = { selectedPresetToLog = preset }
                        )
                    }
                }
            }

            // Today's Burned Log List
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Today's Logged Activities (${activityLogs.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (activityLogs.isEmpty()) {
                item {
                    EmptyLogCard(
                        title = "No Workouts or Steps Logged",
                        subtitle = "Enter steps above or select a workout activity to track your burned calories."
                    )
                }
            } else {
                items(activityLogs, key = { it.id }) { log ->
                    LoggedActivityCard(log = log, onDelete = { viewModel.deleteActivityLog(log) })
                }
            }
        }
    }

    // Preset Activity Logging Modal
    selectedPresetToLog?.let { preset ->
        LogPresetActivityDialog(
            preset = preset,
            onDismiss = { selectedPresetToLog = null },
            onLog = { durationMins, calBurned ->
                viewModel.logActivity(
                    activityName = preset.name,
                    durationMins = durationMins,
                    stepsCount = 0,
                    caloriesBurned = calBurned
                )
            }
        )
    }

    // Custom Activity Modal
    if (showCustomActivityDialog) {
        CustomActivityDialog(
            onDismiss = { showCustomActivityDialog = false },
            onLog = { name, duration, burned ->
                viewModel.logActivity(
                    activityName = name,
                    durationMins = duration,
                    stepsCount = 0,
                    caloriesBurned = burned
                )
            }
        )
    }
}

@Composable
fun BurnedHeaderCard(burnedCalories: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CoralBurned),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total Calories Burned",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$burnedCalories",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                    Text(
                        text = "kcal",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun PresetActivityCard(preset: ActivityPreset, onClick: () -> Unit) {
    val estimatedEst = (preset.defaultMins * preset.calPerMin).toInt()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CoralBurned.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = preset.icon,
                    contentDescription = preset.name,
                    tint = CoralBurned,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = preset.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${preset.defaultMins} min • ~$estimatedEst kcal",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LoggedActivityCard(log: ActivityLogEntry, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CoralBurned.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (log.stepsCount > 0) Icons.Default.DirectionsWalk else Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = CoralBurned,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.activityName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (log.stepsCount > 0) "${log.stepsCount} steps" else "${log.durationMins} minutes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "-${log.caloriesBurned} kcal",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CoralBurned
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LogPresetActivityDialog(
    preset: ActivityPreset,
    onDismiss: () -> Unit,
    onLog: (durationMins: Int, calBurned: Int) -> Unit
) {
    var durationStr by remember { mutableStateOf(preset.defaultMins.toString()) }
    val durationVal = durationStr.toIntOrNull() ?: preset.defaultMins
    val calculatedBurned = (durationVal * preset.calPerMin).toInt()

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
                    Text(
                        text = "Log ${preset.name}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = durationStr,
                    onValueChange = { durationStr = it },
                    label = { Text("Duration (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = CoralBurned.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimated Calories Burned:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$calculatedBurned kcal",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = CoralBurned
                        )
                    }
                }

                Button(
                    onClick = {
                        onLog(durationVal, calculatedBurned)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralBurned),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Add Burned Activity", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun CustomActivityDialog(
    onDismiss: () -> Unit,
    onLog: (name: String, duration: Int, burned: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var durationStr by remember { mutableStateOf("") }
    var burnedStr by remember { mutableStateOf("") }

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
                    Text(
                        text = "Log Custom Workout",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Activity / Workout Name") },
                    placeholder = { Text("e.g. HIIT Training") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = durationStr,
                        onValueChange = { durationStr = it },
                        label = { Text("Duration (mins)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = burnedStr,
                        onValueChange = { burnedStr = it },
                        label = { Text("Calories Burned") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Button(
                    onClick = {
                        val dur = durationStr.toIntOrNull() ?: 0
                        val cal = burnedStr.toIntOrNull() ?: 0
                        if (name.isNotBlank() && cal > 0) {
                            onLog(name, dur, cal)
                            onDismiss()
                        }
                    },
                    enabled = name.isNotBlank() && (burnedStr.toIntOrNull() ?: 0) > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = CoralBurned),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Save Activity Log", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
