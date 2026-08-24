package com.jerries.expense.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jerries.expense.core.designsystem.theme.LocalSpacing

/** Section title with consistent spacing, used to separate screen content. */
@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding, vertical = spacing.small),
    )
}
