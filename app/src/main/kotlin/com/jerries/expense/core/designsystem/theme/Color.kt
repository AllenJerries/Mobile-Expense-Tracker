package com.jerries.expense.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// Brand palette
val JerriesBlue10 = Color(0xFF001A41)
val JerriesBlue20 = Color(0xFF0B2E63)
val JerriesBlue30 = Color(0xFF14458C)
val JerriesBlue40 = Color(0xFF2F5DA8)
val JerriesBlue80 = Color(0xFFADC1EC)
val JerriesBlue90 = Color(0xFFCBD9F5)

val TealAccent40 = Color(0xFF00696D)
val TealAccent80 = Color(0xFF4CDADF)

// Semantic amounts
val IncomeGreen = Color(0xFF1E8E3E)
val IncomeGreenDark = Color(0xFF6DD58C)
val ExpenseRed = Color(0xFFBA1A1A)
val ExpenseRedDark = Color(0xFFFFB4AB)

val LightBackground = Color(0xFFF9F9FF)
val DarkBackground = Color(0xFF101418)

internal val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = JerriesBlue30,
    onPrimary = Color.White,
    primaryContainer = JerriesBlue90,
    onPrimaryContainer = JerriesBlue10,
    secondary = TealAccent40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBDF0F2),
    onSecondaryContainer = Color(0xFF002022),
    tertiary = Color(0xFF7A5900),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE08D),
    onTertiaryContainer = Color(0xFF251A00),
    background = LightBackground,
    onBackground = Color(0xFF191C20),
    surface = LightBackground,
    onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFE0E2EC),
    onSurfaceVariant = Color(0xFF44474E),
    error = ExpenseRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF74777F),
)

internal val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = JerriesBlue80,
    onPrimary = JerriesBlue20,
    primaryContainer = JerriesBlue30,
    onPrimaryContainer = JerriesBlue90,
    secondary = TealAccent80,
    onSecondary = Color(0xFF003739),
    secondaryContainer = Color(0xFF004F52),
    onSecondaryContainer = Color(0xFFBDF0F2),
    tertiary = Color(0xFFF5BE48),
    onTertiary = Color(0xFF412D00),
    tertiaryContainer = Color(0xFF5E4300),
    onTertiaryContainer = Color(0xFFFFE08D),
    background = DarkBackground,
    onBackground = Color(0xFFE1E2E8),
    surface = DarkBackground,
    onSurface = Color(0xFFE1E2E8),
    surfaceVariant = Color(0xFF44474E),
    onSurfaceVariant = Color(0xFFC5C6D0),
    error = ExpenseRedDark,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = ExpenseRedDark,
    outline = Color(0xFF8F909A),
)
