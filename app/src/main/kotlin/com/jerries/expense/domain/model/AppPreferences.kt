package com.jerries.expense.domain.model

/** User-controlled appearance and locale preferences. */
enum class ThemeSetting { SYSTEM, LIGHT, DARK }

data class AppPreferences(
    val theme: ThemeSetting = ThemeSetting.SYSTEM,
    val useDynamicColors: Boolean = false,
    val currencyCode: String = "USD",
    val seeded: Boolean = false,
)
