package com.jerries.expense.core.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * App-level UI state holder: owns the [NavHostController] and derives shell
 * visibility (bottom bar, FAB) from the navigation back stack.
 */
class JerriesExpenseAppState(
    val navController: NavHostController,
    val windowWidthSizeClass: WindowWidthSizeClass,
) {

    fun navigateToTopLevelDestination(destination: TopLevelDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToAddTransaction() {
        navController.navigate(AddTransactionRoute)
    }

    fun navigateUp() = navController.navigateUp()

    /**
     * Bottom bar and FAB are shown only while a top-level tab is visible.
     * On expanded widths this shell could become a nav rail in Phase 2.
     */
    fun shouldShowBottomBar(currentDestination: NavDestination?): Boolean =
        TopLevelDestination.entries.any { destination ->
            currentDestination?.hierarchy?.any {
                it.hasRoute(destination.route::class)
            } == true
        }
}
