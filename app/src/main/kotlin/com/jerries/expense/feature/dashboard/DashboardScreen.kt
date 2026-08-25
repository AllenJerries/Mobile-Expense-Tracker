package com.jerries.expense.feature.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.AmountText
import com.jerries.expense.core.designsystem.component.AmountTint
import com.jerries.expense.core.designsystem.component.EmptyContent
import com.jerries.expense.core.designsystem.component.JeElevatedCard
import com.jerries.expense.core.designsystem.component.LoadingContent
import com.jerries.expense.core.designsystem.component.SectionHeader
import com.jerries.expense.core.designsystem.component.TransactionRow
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.core.util.CurrencyFormatter
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToAddIncome: () -> Unit,
    onNavigateToTransfer: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToTransactionDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.dashboard_greeting)) })
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        when {
            state.isLoading -> LoadingContent(Modifier.padding(padding))

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = spacing.huge),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                // Total Balance
                item(key = "balance") {
                    BalanceCard(
                        balanceMinor = state.totalBalanceMinor,
                        currencyCode = state.currencyCode,
                    )
                }

                // Month Selector
                item(key = "month-selector") {
                    MonthSelector(
                        month = selectedMonth,
                        onPrevious = viewModel::onPreviousMonth,
                        onNext = viewModel::onNextMonth,
                    )
                }

                // Month Summary: Income, Expenses, Savings
                item(key = "month-summary") {
                    MonthSummaryRow(
                        incomeMinor = state.incomeThisMonth,
                        expenseMinor = state.expensesThisMonth,
                        savingsMinor = state.savingsThisMonth,
                        currencyCode = state.currencyCode,
                    )
                }

                // Comparison with previous month
                if (state.incomeLastMonth > 0 || state.expensesLastMonth > 0) {
                    item(key = "comparison") {
                        MonthComparisonCard(
                            incomeChange = state.incomeChangePercent,
                            expenseChange = state.expenseChangePercent,
                        )
                    }
                }

                // Quick Actions
                item(key = "quick-actions") {
                    QuickActions(
                        onAddExpense = onNavigateToAddExpense,
                        onAddIncome = onNavigateToAddIncome,
                        onTransfer = onNavigateToTransfer,
                        onAddBudget = onNavigateToBudgets,
                    )
                }

                // Budget Usage
                if (state.budgetSpendings.isNotEmpty()) {
                    item(key = "budget-header") {
                        SectionHeader(stringResource(R.string.dashboard_budget_usage))
                    }
                    items(state.budgetSpendings.take(3), key = { it.budget.id }) { spending ->
                        BudgetUsageRow(
                            label = spending.budget.categoryId ?: "Overall",
                            spentMinor = spending.spentMinor,
                            limitMinor = spending.limitMinor,
                            percentage = spending.percentage.toFloat(),
                            currencyCode = state.currencyCode,
                        )
                    }
                }

                // Top Spending Categories
                if (state.topCategories.isNotEmpty()) {
                    item(key = "top-categories-header") {
                        SectionHeader(stringResource(R.string.dashboard_top_categories))
                    }
                    items(state.topCategories.take(5), key = { it.name }) { category ->
                        TopCategoryRow(
                            name = category.name,
                            amountMinor = category.amountMinor,
                            percentage = category.percentage,
                            currencyCode = state.currencyCode,
                        )
                    }
                }

                // Recent Transactions
                item(key = "recent-header") {
                    SectionHeader(
                        text = stringResource(R.string.dashboard_recent_transactions),
                        action = {
                            FilledTonalButton(
                                onClick = onNavigateToTransactions,
                                contentPadding = PaddingValues(horizontal = spacing.medium, vertical = spacing.extraSmall),
                            ) {
                                Text(
                                    text = stringResource(R.string.dashboard_view_all),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        },
                    )
                }
                if (state.isEmpty) {
                    item(key = "empty") {
                        EmptyContent(
                            icon = JeIcons.Fallback,
                            title = stringResource(R.string.empty_transactions_title),
                            message = stringResource(R.string.empty_transactions_message),
                        )
                    }
                } else {
                    items(state.recentTransactions, key = { it.id }) { row ->
                        TransactionRow(
                            model = row,
                            currencyCode = state.currencyCode,
                            todayEpochDay = state.todayEpochDay,
                            modifier = Modifier.clickable {
                                onNavigateToTransactionDetail(row.id)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(
    balanceMinor: Long,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    JeElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Text(
                text = stringResource(R.string.dashboard_total_balance),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AmountText(
                amountMinor = balanceMinor,
                currencyCode = currencyCode,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@Composable
private fun MonthSelector(
    month: java.time.YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = "Previous month",
            )
        }
        Text(
            text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Next month",
            )
        }
    }
}

@Composable
private fun MonthSummaryRow(
    incomeMinor: Long,
    expenseMinor: Long,
    savingsMinor: Long,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        SummaryMiniCard(
            title = stringResource(R.string.dashboard_income_this_month),
            amountMinor = incomeMinor,
            currencyCode = currencyCode,
            tint = AmountTint.INCOME,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            modifier = Modifier.weight(1f),
        )
        SummaryMiniCard(
            title = stringResource(R.string.dashboard_expenses_this_month),
            amountMinor = expenseMinor,
            currencyCode = currencyCode,
            tint = AmountTint.EXPENSE,
            icon = Icons.AutoMirrored.Filled.TrendingDown,
            modifier = Modifier.weight(1f),
        )
        SummaryMiniCard(
            title = stringResource(R.string.dashboard_savings_this_month),
            amountMinor = savingsMinor,
            currencyCode = currencyCode,
            tint = AmountTint.NEUTRAL,
            icon = Icons.Filled.Savings,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SummaryMiniCard(
    title: String,
    amountMinor: Long,
    currencyCode: String,
    tint: AmountTint,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.small),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            AmountText(
                amountMinor = amountMinor,
                currencyCode = currencyCode,
                tint = tint,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
private fun MonthComparisonCard(
    incomeChange: Float,
    expenseChange: Float,
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        ) {
            Text(
                text = stringResource(R.string.dashboard_vs_last_month),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Income",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val sign = if (incomeChange >= 0) "+" else ""
                    Text(
                        text = "$sign${String.format("%.0f", incomeChange * 100)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (incomeChange >= 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Expenses",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val sign = if (expenseChange >= 0) "+" else ""
                    Text(
                        text = "$sign${String.format("%.0f", expenseChange * 100)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (expenseChange <= 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickActions(
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onTransfer: () -> Unit,
    onAddBudget: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding),
    ) {
        Text(
            text = stringResource(R.string.dashboard_quick_actions),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(spacing.small))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            QuickActionButton(
                text = stringResource(R.string.quick_action_add_expense),
                icon = Icons.AutoMirrored.Filled.TrendingDown,
                onClick = onAddExpense,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            )
            QuickActionButton(
                text = stringResource(R.string.quick_action_add_income),
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                onClick = onAddIncome,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            QuickActionButton(
                text = stringResource(R.string.quick_action_transfer),
                icon = Icons.Filled.AccountBalance,
                onClick = onTransfer,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            QuickActionButton(
                text = stringResource(R.string.quick_action_add_budget),
                icon = Icons.Filled.Savings,
                onClick = onAddBudget,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
) {
    val spacing = LocalSpacing.current
    FilledTonalButton(
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        contentPadding = PaddingValues(horizontal = spacing.medium, vertical = spacing.small),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(spacing.extraSmall))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun BudgetUsageRow(
    label: String,
    spentMinor: Long,
    limitMinor: Long,
    percentage: Float,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    val progress = percentage.coerceIn(0f, 1f)
    val isExceeded = percentage > 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${CurrencyFormatter.formatMinorUnits(spentMinor, currencyCode)} / ${CurrencyFormatter.formatMinorUnits(limitMinor, currencyCode)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = if (isExceeded) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun TopCategoryRow(
    name: String,
    amountMinor: Long,
    percentage: Float,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding, vertical = spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LinearProgressIndicator(
            progress = { percentage.coerceIn(0f, 1f) },
            modifier = Modifier
                .width(4.dp)
                .height(32.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(modifier = Modifier.width(spacing.small))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "${String.format("%.0f", percentage * 100)}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AmountText(
            amountMinor = amountMinor,
            currencyCode = currencyCode,
            tint = AmountTint.EXPENSE,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
