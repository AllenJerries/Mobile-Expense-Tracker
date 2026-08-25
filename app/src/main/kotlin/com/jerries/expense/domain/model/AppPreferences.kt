package com.jerries.expense.domain.model

enum class ThemeSetting { SYSTEM, LIGHT, DARK }

enum class FirstDayOfWeek { SUNDAY, MONDAY, SATURDAY }

data class AppPreferences(
    val theme: ThemeSetting = ThemeSetting.SYSTEM,
    val useDynamicColors: Boolean = false,
    val currencyCode: String = "USD",
    val seeded: Boolean = false,
    val firstDayOfWeek: FirstDayOfWeek = FirstDayOfWeek.MONDAY,
    val notificationBudgetWarnings: Boolean = true,
    val notificationRecurringReminders: Boolean = true,
    val notificationSavingsReminders: Boolean = true,
    val biometricEnabled: Boolean = false,
    val pinEnabled: Boolean = false,
    val pinHash: String? = null,
    val autoLockTimeoutMinutes: Int = 5,
    val hideSensitiveInfo: Boolean = false,
)
