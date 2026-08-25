package com.jerries.expense.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jerries.expense.feature.accounts.AccountsScreen
import com.jerries.expense.feature.addtransaction.AddTransactionScreen
import com.jerries.expense.feature.analytics.AnalyticsScreen
import com.jerries.expense.feature.backup.BackupScreen
import com.jerries.expense.feature.budgets.BudgetsScreen
import com.jerries.expense.feature.categories.CategoriesScreen
import com.jerries.expense.feature.dashboard.DashboardScreen
import com.jerries.expense.feature.edittransaction.EditTransactionScreen
import com.jerries.expense.feature.goals.GoalsScreen
import com.jerries.expense.feature.insights.InsightsScreen
import com.jerries.expense.feature.recurring.RecurringScreen
import com.jerries.expense.feature.reports.ReportsScreen
import com.jerries.expense.feature.security.PinEntryScreen
import com.jerries.expense.feature.security.SecurityScreen
import com.jerries.expense.feature.settings.SettingsScreen
import com.jerries.expense.feature.transactiondetail.TransactionDetailScreen
import com.jerries.expense.feature.transactions.TransactionsScreen

private const val TRANSITION_DURATION = 300

private val slideInFromRight: EnterTransition =
    slideInHorizontally(
        animationSpec = tween(TRANSITION_DURATION),
        initialOffsetX = { fullWidth -> fullWidth / 3 },
    ) + fadeIn(
        animationSpec = tween(TRANSITION_DURATION),
    )

private val slideOutToRight: ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(TRANSITION_DURATION),
        targetOffsetX = { fullWidth -> fullWidth / 3 },
    ) + fadeOut(
        animationSpec = tween(TRANSITION_DURATION),
    )

private val slideInFromLeft: EnterTransition =
    slideInHorizontally(
        animationSpec = tween(TRANSITION_DURATION),
        initialOffsetX = { fullWidth -> -fullWidth / 3 },
    ) + fadeIn(
        animationSpec = tween(TRANSITION_DURATION),
    )

private val slideOutToLeft: ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(TRANSITION_DURATION),
        targetOffsetX = { fullWidth -> -fullWidth / 3 },
    ) + fadeOut(
        animationSpec = tween(TRANSITION_DURATION),
    )

private val fadeInOnly: EnterTransition = fadeIn(
    animationSpec = tween(TRANSITION_DURATION),
)

private val fadeOutOnly: ExitTransition = fadeOut(
    animationSpec = tween(TRANSITION_DURATION),
)

/**
 * Single navigation graph for the app. Each feature owns its screen; this
 * file only wires destinations and cross-screen callbacks.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoute,
        modifier = modifier,
        enterTransition = { slideInFromRight },
        exitTransition = { slideOutToLeft },
        popEnterTransition = { slideInFromLeft },
        popExitTransition = { slideOutToRight },
    ) {
        composable<DashboardRoute> {
            DashboardScreen(
                onNavigateToAddExpense = {
                    navController.navigate(AddTransactionRoute)
                },
                onNavigateToAddIncome = {
                    navController.navigate(AddTransactionRoute)
                },
                onNavigateToTransfer = {
                    navController.navigate(AddTransactionRoute)
                },
                onNavigateToBudgets = {
                    navController.navigate(BudgetsRoute)
                },
                onNavigateToTransactions = {
                    navController.navigate(TransactionsRoute)
                },
                onNavigateToTransactionDetail = { id ->
                    navController.navigate(TransactionDetailRoute(id))
                },
            )
        }
        composable<TransactionsRoute> {
            TransactionsScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(TransactionDetailRoute(id))
                },
            )
        }
        composable<AddTransactionRoute> {
            AddTransactionScreen(onNavigateUp = navController::navigateUp)
        }
        composable<EditTransactionRoute> {
            EditTransactionScreen(onNavigateUp = navController::navigateUp)
        }
        composable<TransactionDetailRoute> {
            TransactionDetailScreen(
                onNavigateUp = navController::navigateUp,
                onNavigateToEdit = { id ->
                    navController.navigate(EditTransactionRoute(id))
                },
            )
        }
        composable<BudgetsRoute> {
            BudgetsScreen()
        }
        composable<AnalyticsRoute> {
            AnalyticsScreen()
        }
        composable<AccountsRoute> {
            AccountsScreen()
        }
        composable<RecurringRoute> {
            RecurringScreen()
        }
        composable<CategoriesRoute> {
            CategoriesScreen()
        }
        composable<GoalsRoute> {
            GoalsScreen()
        }
        composable<InsightsRoute> {
            InsightsScreen()
        }
        composable<ReportsRoute> {
            ReportsScreen()
        }
        composable<BackupRoute> {
            BackupScreen(
                onNavigateUp = navController::navigateUp,
            )
        }
        composable<SecurityRoute> {
            SecurityScreen(
                onNavigateUp = navController::navigateUp,
            )
        }
        composable<PinEntryRoute> {
            PinEntryScreen(
                onUnlocked = navController::navigateUp,
            )
        }
        composable<SettingsRoute> {
            SettingsScreen(
                onOpenAccounts = { navController.navigate(AccountsRoute) },
                onOpenCategories = { navController.navigate(CategoriesRoute) },
                onOpenBackup = { navController.navigate(BackupRoute) },
                onOpenSecurity = { navController.navigate(SecurityRoute) },
            )
        }
    }
}
