package com.jerries.expense.core.navigation

import kotlinx.serialization.Serializable

@Serializable data object DashboardRoute
@Serializable data object TransactionsRoute
@Serializable data object AddTransactionRoute
@Serializable data class EditTransactionRoute(val transactionId: String)
@Serializable data class TransactionDetailRoute(val transactionId: String)
@Serializable data object BudgetsRoute
@Serializable data object AddBudgetRoute
@Serializable data class EditBudgetRoute(val budgetId: String)
@Serializable data object AnalyticsRoute
@Serializable data object AccountsRoute
@Serializable data object RecurringRoute
@Serializable data object AddRecurringRoute
@Serializable data object CategoriesRoute
@Serializable data object GoalsRoute
@Serializable data object AddGoalRoute
@Serializable data class EditGoalRoute(val goalId: String)
@Serializable data object InsightsRoute
@Serializable data object ReportsRoute
@Serializable data object BackupRoute
@Serializable data object SettingsRoute
@Serializable data object PinEntryRoute
@Serializable data object SecurityRoute
