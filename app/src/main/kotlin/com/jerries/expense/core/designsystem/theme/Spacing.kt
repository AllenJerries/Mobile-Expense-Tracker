package com.jerries.expense.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Application-wide spacing scale. Access from composables via
 * `JerriesSpacing` (see [LocalSpacing] provided in JerriesExpenseTheme).
 */
@Immutable
data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 48.dp,
    val screenPadding: Dp = 16.dp,
    val cardPadding: Dp = 16.dp,
    val listItemSpacing: Dp = 12.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
