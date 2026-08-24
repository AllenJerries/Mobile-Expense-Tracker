package com.jerries.expense.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.jerries.expense.core.designsystem.theme.AmountColors
import com.jerries.expense.core.util.CurrencyFormatter

enum class AmountTint { NEUTRAL, INCOME, EXPENSE }

/**
 * Displays a monetary value in minor units, optionally color-coded by
 * income/expense. Centralizes currency formatting for the whole app.
 */
@Composable
fun AmountText(
    amountMinor: Long,
    currencyCode: String,
    modifier: Modifier = Modifier,
    tint: AmountTint = AmountTint.NEUTRAL,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
) {
    val color = when (tint) {
        AmountTint.NEUTRAL -> MaterialTheme.colorScheme.onSurface
        AmountTint.INCOME -> AmountColors.income()
        AmountTint.EXPENSE -> AmountColors.expense()
    }
    Text(
        text = CurrencyFormatter.formatMinorUnits(amountMinor, currencyCode),
        modifier = modifier,
        style = style,
        fontWeight = FontWeight.SemiBold,
        color = color,
    )
}
