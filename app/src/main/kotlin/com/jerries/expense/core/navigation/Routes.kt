package com.jerries.expense.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations (Navigation Compose 2.8+).
 * Every feature registers exactly one route here, keeping the graph
 * definition in AppNavHost free of stringly-typed paths.
 */
@Serializable
data object DashboardRoute

@Serializable
data object TransactionsRoute

@Serializable
data object AddTransactionRoute

@Serializable
data object BudgetsRoute

@Serializable
data object AnalyticsRoute

@Serializable
data object AccountsRoute

@Serializable
data object RecurringRoute

@Serializable
data object CategoriesRoute

@Serializable
data object GoalsRoute

@Serializable
data object InsightsRoute

@Serializable
data object ReportsRoute

@Serializable
data object BackupRoute

@Serializable
data object SettingsRoute
