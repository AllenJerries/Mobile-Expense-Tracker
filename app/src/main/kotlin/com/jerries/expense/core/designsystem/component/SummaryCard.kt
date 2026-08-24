package com.jerries.expense.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jerries.expense.core.designsystem.theme.LocalSpacing

/**
 * Hero summary card (e.g. total balance on the dashboard): a label above and
 * a large monetary value below, inside an elevated card.
 */
@Composable
fun SummaryCard(
    label: String,
    amountMinor: Long,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    JeElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AmountText(
                amountMinor = amountMinor,
                currencyCode = currencyCode,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}
