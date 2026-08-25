package com.jerries.expense

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jerries.expense.core.designsystem.component.GlassBottomBar
import com.jerries.expense.core.designsystem.component.GlassNavItem
import com.jerries.expense.core.designsystem.component.glassConfig
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.core.navigation.AppNavHost
import com.jerries.expense.core.navigation.JerriesExpenseAppState
import com.jerries.expense.core.navigation.TopLevelDestination

/**
 * App shell: hosts the NavHost inside an app-level scaffold with the
 * glassmorphic bottom navigation bar and the global "Add transaction" FAB.
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
    val config = glassConfig()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = appState.shouldShowBottomBar(currentDestination)

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                GlassBottomBar {
                    TopLevelDestination.entries.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(destination.route::class)
                        } == true
                        GlassNavItem(
                            selected = selected,
                            onClick = { appState.navigateToTopLevelDestination(destination) },
                            icon = {
                                val icon = if (selected) destination.selectedIcon else destination.unselectedIcon
                                Icon(imageVector = icon, contentDescription = null)
                            },
                            label = {
                                Text(
                                    text = stringResource(destination.labelRes),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                val fabColor by animateColorAsState(
                    targetValue = MaterialTheme.colorScheme.primaryContainer,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                    label = "fabColor",
                )
                FloatingActionButton(
                    onClick = appState::navigateToAddTransaction,
                    shape = CircleShape,
                    containerColor = fabColor,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 10.dp,
                    ),
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            }
        },
    ) { padding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(padding),
        )
    }
}
