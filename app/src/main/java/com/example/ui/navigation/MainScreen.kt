package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.BurnedScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.IntakeScreen
import com.example.ui.viewmodel.NutriViewModel

sealed class WindowTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Intake : WindowTab("intake", "Calories Intake", Icons.Filled.Restaurant, Icons.Outlined.Restaurant, "tab_intake")
    object Burned : WindowTab("burned", "Calories Burned", Icons.Filled.LocalFireDepartment, Icons.Outlined.LocalFireDepartment, "tab_burned")
    object Dashboard : WindowTab("dashboard", "Net Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "tab_dashboard")
}

@Composable
fun MainScreen(viewModel: NutriViewModel) {
    var selectedTab by remember { mutableStateOf<WindowTab>(WindowTab.Intake) }

    val savedFoods by viewModel.savedFoods.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val foodLogs by viewModel.foodLogsForSelectedDate.collectAsStateWithLifecycle()
    val activityLogs by viewModel.activityLogsForSelectedDate.collectAsStateWithLifecycle()
    val dailySummary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val weeklyChartData by viewModel.weeklyChartData.collectAsStateWithLifecycle()

    val tabs = listOf(WindowTab.Intake, WindowTab.Burned, WindowTab.Dashboard)

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title) },
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                WindowTab.Intake -> IntakeScreen(
                    viewModel = viewModel,
                    dailySummary = dailySummary,
                    savedFoods = savedFoods,
                    foodLogs = foodLogs,
                    targetCalories = userProfile.targetCalories
                )
                WindowTab.Burned -> BurnedScreen(
                    viewModel = viewModel,
                    dailySummary = dailySummary,
                    activityLogs = activityLogs
                )
                WindowTab.Dashboard -> DashboardScreen(
                    viewModel = viewModel,
                    dailySummary = dailySummary,
                    userProfile = userProfile,
                    weeklyChartData = weeklyChartData
                )
            }
        }
    }
}
