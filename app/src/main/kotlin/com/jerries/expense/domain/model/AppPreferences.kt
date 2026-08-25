package com.jerries.expense.domain.model

enum class ThemeSetting { SYSTEM, LIGHT, DARK }

data class AppPreferences(
    val theme: ThemeSetting = ThemeSetting.SYSTEM,
    val useDynamicColors: Boolean = false,
    val currencyCode: String = "USD",
    val seeded: Boolean = false,
)
