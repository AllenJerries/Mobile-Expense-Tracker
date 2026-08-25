package com.jerries.expense.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

/**
 * Modifier that applies a staggered fade-in + slide-up animation.
 * Use with LazyColumn items for a premium staggered entry effect.
 *
 * @param index The item's position in the list (0-based).
 * @param delayPerItemMs Delay between each item's animation start.
 */
fun Modifier.staggeredListItemEntry(
    index: Int,
    delayPerItemMs: Int = 40,
): Modifier = composed {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(index * delayPerItemMs.toLong())
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        )
    }

    graphicsLayer {
        alpha = animatable.value
        translationY = (1f - animatable.value) * size.height * 0.3f
    }
}

/**
 * Modifier that provides press-scale feedback on touch.
 * The composable squeezes to [pressScale] on press and springs back on release.
 *
 * @param pressScale Scale factor when pressed (default 0.97f for subtle squeeze).
 */
fun Modifier.scaleOnPress(
    pressScale: Float = 0.97f,
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = "pressScale",
    )

    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitFirstDown(requireUnconsumed = false)
                isPressed = true
                waitForUpOrCancellation()
                isPressed = false
            }
        }
    }

    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
