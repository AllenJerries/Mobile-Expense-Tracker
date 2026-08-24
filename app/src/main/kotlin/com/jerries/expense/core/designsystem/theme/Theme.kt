package com.jerries.expense.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

/**
 * Root theme for JERRIES EXPENSE. Supports the in-app light/dark/system setting,
 * optional Material You dynamic color on Android 12+, and exposes the shared
 * [Spacing] scale through [LocalSpacing].
 */
@Composable
fun JerriesExpenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = JerriesTypography,
            shapes = JerriesShapes,
            content = content,
        )
    }
}

/** Semantic amount colors that adapt to light/dark themes. */
object AmountColors {
    @Composable
    fun income() = if (isSystemInDarkTheme()) IncomeGreenDark else IncomeGreen

    @Composable
    fun expense() = if (isSystemInDarkTheme()) ExpenseRedDark else ExpenseRed
}
