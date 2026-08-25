package com.jerries.expense.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jerries.expense.core.designsystem.theme.GlassColors

// ── Glass configuration ──────────────────────────────────────────────

@Immutable
data class GlassConfig(
    val surfaceColor: Color,
    val surfaceElevatedColor: Color,
    val surfaceHighColor: Color,
    val border: Color,
    val borderSubtle: Color,
    val scrim: Color,
    val navBarColor: Color,
    val topBarColor: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val cornerRadius: Dp,
    val cornerRadiusLarge: Dp,
    val borderWidth: Dp,
    val borderWidthSubtle: Dp,
    val elevation: Dp,
    val elevationHigh: Dp,
)

private val LightGlassConfig = GlassConfig(
    surfaceColor = GlassColors.LightSurface,
    surfaceElevatedColor = GlassColors.LightSurfaceElevated,
    surfaceHighColor = GlassColors.LightSurfaceHigh,
    border = GlassColors.LightBorder,
    borderSubtle = GlassColors.LightBorderSubtle,
    scrim = GlassColors.LightScrim,
    navBarColor = GlassColors.LightNavBar,
    topBarColor = GlassColors.LightTopBar,
    gradientStart = GlassColors.LightGradientStart,
    gradientEnd = GlassColors.LightGradientEnd,
    cornerRadius = 16.dp,
    cornerRadiusLarge = 24.dp,
    borderWidth = 1.dp,
    borderWidthSubtle = 0.5.dp,
    elevation = 2.dp,
    elevationHigh = 6.dp,
)

private val DarkGlassConfig = GlassConfig(
    surfaceColor = GlassColors.DarkSurface,
    surfaceElevatedColor = GlassColors.DarkSurfaceElevated,
    surfaceHighColor = GlassColors.DarkSurfaceHigh,
    border = GlassColors.DarkBorder,
    borderSubtle = GlassColors.DarkBorderSubtle,
    scrim = GlassColors.DarkScrim,
    navBarColor = GlassColors.DarkNavBar,
    topBarColor = GlassColors.DarkTopBar,
    gradientStart = GlassColors.DarkGradientStart,
    gradientEnd = GlassColors.DarkGradientEnd,
    cornerRadius = 16.dp,
    cornerRadiusLarge = 24.dp,
    borderWidth = 1.dp,
    borderWidthSubtle = 0.5.dp,
    elevation = 2.dp,
    elevationHigh = 6.dp,
)

val LocalGlassConfig = staticCompositionLocalOf { LightGlassConfig }

// ── Theme helper ─────────────────────────────────────────────────────

@Composable
fun glassConfig(): GlassConfig = LocalGlassConfig.current

@Composable
fun ProvideGlassConfig(isDark: Boolean, content: @Composable () -> Unit) {
    val config = if (isDark) DarkGlassConfig else LightGlassConfig
    CompositionLocalProvider(LocalGlassConfig provides config) {
        content()
    }
}

// ── GlassCard ────────────────────────────────────────────────────────

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(glassConfig().cornerRadius),
    elevated: Boolean = false,
    borderAlpha: Float = 1f,
    content: @Composable ColumnScope.() -> Unit,
) {
    val config = glassConfig()
    val bgColor = if (elevated) config.surfaceElevatedColor else config.surfaceColor
    val borderColor = config.border.copy(alpha = config.border.alpha * borderAlpha)
    val elev = if (elevated) config.elevationHigh else config.elevation

    Surface(
        modifier = modifier,
        shape = shape,
        color = bgColor,
        border = BorderStroke(config.borderWidth, borderColor),
        shadowElevation = elev,
    ) {
        Column(content = content)
    }
}

// ── GlassSurface ─────────────────────────────────────────────────────

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(glassConfig().cornerRadius),
    color: Color = glassConfig().surfaceColor,
    content: @Composable () -> Unit,
) {
    val config = glassConfig()
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        border = BorderStroke(config.borderWidthSubtle, config.borderSubtle),
        content = content,
    )
}

// ── GlassTopBar ──────────────────────────────────────────────────────

@Composable
fun GlassTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val config = glassConfig()
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = config.topBarColor,
        border = BorderStroke(config.borderWidthSubtle, config.borderSubtle),
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (navigationIcon != null) {
                navigationIcon()
                Spacer(Modifier.width(8.dp))
            }
            Box(Modifier.weight(1f)) {
                title()
            }
            Row(
                horizontalArrangement = Arrangement.End,
                content = actions,
            )
        }
    }
}

// ── GlassBottomBar ───────────────────────────────────────────────────

@Composable
fun GlassBottomBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val config = glassConfig()
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = config.navBarColor,
        border = BorderStroke(config.borderWidthSubtle, config.borderSubtle),
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            content = content,
        )
    }
}

// ── GlassNavItem ─────────────────────────────────────────────────────

@Composable
fun GlassNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
) {
    val config = glassConfig()
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        } else {
            Color.Transparent
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "navItemBg",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "navItemContent",
    )

    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = if (selected) BorderStroke(config.borderWidthSubtle, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)) else null,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box { icon() }
            Spacer(Modifier.height(4.dp))
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Box {
                    label()
                }
            }
        }
    }
}
