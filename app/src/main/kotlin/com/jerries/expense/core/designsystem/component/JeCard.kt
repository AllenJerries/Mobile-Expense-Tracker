package com.jerries.expense.core.designsystem.component

import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Outlined card used as the default content container. */
@Composable
fun JeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = MaterialTheme.shapes.medium) {
            content()
        }
    } else {
        Card(modifier = modifier, shape = MaterialTheme.shapes.medium) {
            content()
        }
    }
}

/** Elevated card for hero-style content such as balance summaries. */
@Composable
fun JeElevatedCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ElevatedCard(modifier = modifier, shape = MaterialTheme.shapes.large) {
        content()
    }
}
