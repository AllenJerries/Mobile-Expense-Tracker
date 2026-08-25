package com.jerries.expense.core.navigation

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
import com.jerries.expense.feature.settings.SettingsScreen
import com.jerries.expense.feature.transactiondetail.TransactionDetailScreen
import com.jerries.expense.feature.transactions.TransactionsScreen

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
            BackupScreen()
        }
        composable<SettingsRoute> {
            SettingsScreen(
                onOpenAccounts = { navController.navigate(AccountsRoute) },
                onOpenCategories = { navController.navigate(CategoriesRoute) },
                onOpenBackup = { navController.navigate(BackupRoute) },
            )
        }
    }
}
