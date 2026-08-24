package com.jerries.expense.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.ui.graphics.vector.ImageVector
import com.jerries.expense.R

/** Destinations shown in the app-level bottom navigation bar. */
enum class TopLevelDestination(
    val route: Any,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelRes: Int,
) {
    Dashboard(
        route = DashboardRoute,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        labelRes = R.string.nav_dashboard,
    ),
    Transactions(
        route = TransactionsRoute,
        selectedIcon = Icons.AutoMirrored.Filled.ReceiptLong,
        unselectedIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
        labelRes = R.string.nav_transactions,
    ),
    Budgets(
        route = BudgetsRoute,
        selectedIcon = Icons.Filled.Savings,
        unselectedIcon = Icons.Outlined.Savings,
        labelRes = R.string.nav_budgets,
    ),
    Analytics(
        route = AnalyticsRoute,
        selectedIcon = Icons.Filled.PieChart,
        unselectedIcon = Icons.Outlined.PieChart,
        labelRes = R.string.nav_analytics,
    ),
    Settings(
        route = SettingsRoute,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        labelRes = R.string.nav_settings,
    ),
}
