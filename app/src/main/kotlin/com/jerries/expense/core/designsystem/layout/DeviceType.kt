package com.jerries.expense.core.designsystem.layout

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

/** Logical device classes derived from the window width size class. */
enum class DeviceType {
    COMPACT,
    MEDIUM,
    EXPANDED,
}

/**
 * Maps a [WindowWidthSizeClass] onto the app's logical [DeviceType].
 * Screens use this to switch between single-column and multi-pane layouts.
 */
fun WindowWidthSizeClass.toDeviceType(): DeviceType = when (this) {
    WindowWidthSizeClass.Compact -> DeviceType.COMPACT
    WindowWidthSizeClass.Medium -> DeviceType.MEDIUM
    else -> DeviceType.EXPANDED
}
