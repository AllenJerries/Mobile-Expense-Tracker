package com.jerries.expense

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.core.navigation.AppNavHost
import com.jerries.expense.core.navigation.JerriesExpenseAppState
import com.jerries.expense.core.navigation.TopLevelDestination

/**
 * App shell: hosts the NavHost inside an app-level scaffold with the bottom
 * navigation bar and the global "Add transaction" FAB.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JerriesExpenseApp(
    windowWidthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val appState = remember(windowWidthSizeClass) {
        JerriesExpenseAppState(navController, windowWidthSizeClass)
    }
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = appState.shouldShowBottomBar(currentDestination)

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(destination.route::class)
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = { appState.navigateToTopLevelDestination(destination) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) {
                                        destination.selectedIcon
                                    } else {
                                        destination.unselectedIcon
                                    },
                                    contentDescription = null,
                                )
                            },
                            label = { Text(stringResource(destination.labelRes)) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                ExtendedFloatingActionButton(
                    onClick = appState::navigateToAddTransaction,
                    icon = {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    },
                    text = { Text(text = stringResource(R.string.add_transaction)) },
                )
            }
        },
    ) { padding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(padding),
        )
    }
}
