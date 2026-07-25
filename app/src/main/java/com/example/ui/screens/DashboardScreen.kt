package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.components.GoalSettingDialog
import com.example.ui.theme.CoralBurned
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.DailySummary
import com.example.ui.viewmodel.DayChartData
import com.example.ui.viewmodel.NutriViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: NutriViewModel,
    dailySummary: DailySummary,
    userProfile: UserProfile,
    weeklyChartData: List<DayChartData>,
    modifier: Modifier = Modifier
) {
    var showGoalDialog by remember { mutableStateOf(false) }

    val goalCalories = userProfile.targetCalories
    val intake = dailySummary.intakeCalories
    val burned = dailySummary.burnedCalories
    val netBalance = dailySummary.netCalories
    val remainingCalories = goalCalories - intake + burned

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 88.dp, start = 16.dp, end = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner Header
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner_1784924764486),
                            contentDescription = "Header Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "NutriTrack Dashboard",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color.White
                            )
                            Text(
                                text = "Net Calorie Balance & Nutritional Goals",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Date Selector Bar
            item {
                DateSelectorRow(
                    displayDate = dailySummary.displayDate,
                    onPrev = { viewModel.navigateDate(-1) },
                    onNext = { viewModel.navigateDate(1) },
                    onToday = { viewModel.setSelectedDate(NutriViewModel.getTodayDateString()) }
                )
            }

            // Main Net Balance Dashboard Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
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
                            Column {
                                Text(
                                    text = "Daily Calorie Summary",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Target Goal: $goalCalories kcal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            FilledTonalButton(
                                onClick = { showGoalDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("edit_goals_button")
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Goals", style = MaterialTheme.typography.labelLarge)
                            }
                        }

                        // Circular Progress Ring & Numbers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ring Gauge
                            NetProgressRing(
                                intake = intake,
                                burned = burned,
                                goal = goalCalories,
                                remaining = remainingCalories,
                                size = 150.dp
                            )

                            // Side Metrics Column
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(start = 12.dp)
                            ) {
                                MetricBadge(
                                    label = "Intake (+)",
                                    value = "$intake kcal",
                                    color = EmeraldPrimary,
                                    icon = Icons.Default.Restaurant
                                )
                                MetricBadge(
                                    label = "Burned (-)",
                                    value = "$burned kcal",
                                    color = CoralBurned,
                                    icon = Icons.Default.LocalFireDepartment
                                )
                                MetricBadge(
                                    label = "Net Balance",
                                    value = "$netBalance kcal",
                                    color = MaterialTheme.colorScheme.primary,
                                    icon = Icons.Default.Balance
                                )
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Remaining / Status Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (remainingCalories >= 0) EmeraldPrimary.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                                )
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (remainingCalories >= 0) "Remaining Calories" else "Over Target Goal",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${kotlin.math.abs(remainingCalories)} kcal",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = if (remainingCalories >= 0) EmeraldPrimary else MaterialTheme.colorScheme.error
                                )
                            }

                            Text(
                                text = if (remainingCalories >= 0) "Within Daily Target ✓" else "Target Exceeded!",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (remainingCalories >= 0) EmeraldPrimary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Macronutrient Goals Progress Reports
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Macronutrient Reports",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        MacroProgressItem(
                            name = "Protein",
                            consumed = dailySummary.totalProtein,
                            target = userProfile.targetProtein.toFloat(),
                            unit = "g",
                            color = Color(0xFF3B82F6)
                        )

                        MacroProgressItem(
                            name = "Carbohydrates",
                            consumed = dailySummary.totalCarbs,
                            target = userProfile.targetCarbs.toFloat(),
                            unit = "g",
                            color = Color(0xFFEAB308)
                        )

                        MacroProgressItem(
                            name = "Fats",
                            consumed = dailySummary.totalFat,
                            target = userProfile.targetFat.toFloat(),
                            unit = "g",
                            color = Color(0xFFEC4899)
                        )
                    }
                }
            }

            // 7-Day Interactive Progress Chart
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
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
                            Column {
                                Text(
                                    text = "7-Day Calories Progress Report",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Intake (Green) vs Burned (Orange)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Weekly Bar Chart
                        WeeklyBarChart(
                            chartData = weeklyChartData,
                            targetGoal = goalCalories,
                            onSelectDay = { dateStr -> viewModel.setSelectedDate(dateStr) }
                        )
                    }
                }
            }
        }
    }

    if (showGoalDialog) {
        GoalSettingDialog(
            currentProfile = userProfile,
            onDismiss = { showGoalDialog = false },
            onSave = { cal, p, c, f ->
                viewModel.updateGoals(cal, p, c, f)
            }
        )
    }
}

@Composable
fun NetProgressRing(
    intake: Int,
    burned: Int,
    goal: Int,
    remaining: Int,
    size: androidx.compose.ui.unit.Dp
) {
    val intakeSweepAngle = if (goal > 0) (intake.toFloat() / goal.toFloat() * 260f).coerceAtMost(260f) else 0f
    val burnedSweepAngle = if (goal > 0) (burned.toFloat() / goal.toFloat() * 260f).coerceAtMost(260f) else 0f

    val animIntake by animateFloatAsState(targetValue = intakeSweepAngle, label = "intake_anim")
    val animBurned by animateFloatAsState(targetValue = burnedSweepAngle, label = "burned_anim")

    val trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val arcSize = Size(size.toPx() - strokeWidth, size.toPx() - strokeWidth)
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // Track arc
            drawArc(
                color = trackColor,
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Intake Arc (Emerald)
            drawArc(
                color = EmeraldPrimary,
                startAngle = 140f,
                sweepAngle = animIntake,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Burned Arc overlay offset (Coral)
            drawArc(
                color = CoralBurned,
                startAngle = 140f + animIntake,
                sweepAngle = animBurned,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth * 0.7f, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$remaining",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "kcal remaining",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MetricBadge(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun MacroProgressItem(
    name: String,
    consumed: Float,
    target: Float,
    unit: String,
    color: Color
) {
    val progress = if (target > 0) (consumed / target).coerceIn(0f, 1f) else 0f

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "${consumed.toInt()} / ${target.toInt()} $unit",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun WeeklyBarChart(
    chartData: List<DayChartData>,
    targetGoal: Int,
    onSelectDay: (String) -> Unit
) {
    val maxCal = (chartData.flatMap { listOf(it.intakeCalories, it.burnedCalories) }.maxOrNull() ?: targetGoal).coerceAtLeast(targetGoal)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        chartData.forEach { day ->
            val intakeRatio = if (maxCal > 0) day.intakeCalories.toFloat() / maxCal.toFloat() else 0f
            val burnedRatio = if (maxCal > 0) day.burnedCalories.toFloat() / maxCal.toFloat() else 0f

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelectDay(day.dateStr) }
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .height(110.dp)
                        .fillMaxWidth(0.85f)
                ) {
                    // Intake Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(intakeRatio.coerceAtLeast(0.05f))
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(EmeraldPrimary)
                    )
                    // Burned Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(burnedRatio.coerceAtLeast(0.05f))
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(CoralBurned)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = day.dayName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
