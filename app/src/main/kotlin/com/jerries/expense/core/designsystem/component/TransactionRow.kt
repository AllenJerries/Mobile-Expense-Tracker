package com.jerries.expense.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.core.util.DateFormatter
import java.time.LocalDate

/** Immutable display model for [TransactionRow]. */
data class TransactionRowModel(
    val id: String,
    val title: String,
    val categoryName: String?,
    val dateEpochDay: Long,
    val amountMinor: Long,
    val isIncome: Boolean,
    val icon: ImageVector,
)

/**
 * Standard list row for transactions: leading category icon, title with a
 * secondary line, and a trailing color-coded amount.
 */
@Composable
fun TransactionRow(
    model: TransactionRowModel,
    currencyCode: String,
    todayEpochDay: Long,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding, vertical = spacing.small),
        horizontalArrangement = Arrangement.spacedBy(spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryIcon(
            icon = model.icon,
            contentDescription = null,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        ) {
            Text(
                text = model.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val subtitle = listOfNotNull(
                model.categoryName,
                DateFormatter.formatRelative(
                    LocalDate.ofEpochDay(model.dateEpochDay),
                    LocalDate.ofEpochDay(todayEpochDay),
                ),
            ).joinToString(" · ")
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        AmountText(
            amountMinor = model.amountMinor,
            currencyCode = currencyCode,
            tint = if (model.isIncome) AmountTint.INCOME else AmountTint.EXPENSE,
        )
    }
}
