package com.jerries.expense.feature.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.AmountText
import com.jerries.expense.core.designsystem.component.AmountTint
import com.jerries.expense.core.designsystem.component.EmptyContent
import com.jerries.expense.core.designsystem.component.GlassCard
import com.jerries.expense.core.designsystem.component.GlassTopBar
import com.jerries.expense.core.designsystem.component.LoadingContent
import com.jerries.expense.core.designsystem.component.SectionHeader
import com.jerries.expense.core.designsystem.component.glassConfig
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.core.util.CurrencyFormatter
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(title = { Text(stringResource(R.string.analytics_title), style = MaterialTheme.typography.titleLarge) })
            when {
                state.isLoading -> LoadingContent(Modifier.weight(1f))
                state.isEmpty -> EmptyContent(
                    icon = Icons.AutoMirrored.Filled.ShowChart,
                    title = stringResource(R.string.empty_generic_title),
                    message = stringResource(R.string.analytics_empty_message),
                    modifier = Modifier.weight(1f),
                )
                else -> LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = spacing.huge),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                ) {
                    item("month-selector") {
                        MonthSelector(
                            month = state.currentMonth,
                            onPrevious = viewModel::onPreviousMonth,
                            onNext = viewModel::onNextMonth,
                        )
                    }
                    item("overview-cards") {
                        OverviewCards(
                            income = state.incomeThisMonth,
                            expense = state.expensesThisMonth,
                            savingsRate = state.savingsRate,
                            currencyCode = state.currencyCode,
                        )
                    }
                    if (state.monthIncomeValues.isNotEmpty()) {
                        item("income-expense-chart") {
                            SectionHeader(stringResource(R.string.analytics_income_vs_expenses))
                            IncomeExpenseChart(
                                incomeValues = state.monthIncomeValues,
                                expenseValues = state.monthExpenseValues,
                                labels = state.monthLabels,
                                modifier = Modifier.padding(horizontal = spacing.screenPadding),
                            )
                        }
                    }
                    if (state.expensesByCategory.isNotEmpty()) {
                        item("category-header") {
                            SectionHeader(stringResource(R.string.analytics_category_breakdown))
                        }
                        items(state.expensesByCategory, key = { it.name }) { slice ->
                            CategoryBar(
                                name = slice.name,
                                amountMinor = slice.amountMinor,
                                percentage = slice.percentage,
                                colorArgb = slice.colorArgb,
                                currencyCode = state.currencyCode,
                            )
                        }
                    }
                    item("stats-header") {
                        SectionHeader(stringResource(R.string.analytics_overview))
                    }
                    item("stats") {
                        StatsGrid(
                            avgDaily = state.avgDailySpending,
                            highestCategoryName = state.highestCategoryName,
                            highestCategoryAmount = state.highestCategoryAmount,
                            highestTxTitle = state.highestTransactionTitle,
                            highestTxAmount = state.highestTransactionAmount,
                            currencyCode = state.currencyCode,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    month: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.screenPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = stringResource(R.string.previous_month))
        }
        Text(
            text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.ChevronRight, contentDescription = stringResource(R.string.next_month))
        }
    }
}

@Composable
private fun OverviewCards(
    income: Long,
    expense: Long,
    savingsRate: Float,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        GlassCard(
            modifier = Modifier.weight(1f).padding(spacing.small),
        ) {
            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(16.dp))
            Text(stringResource(R.string.label_income), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            AmountText(amountMinor = income, currencyCode = currencyCode, tint = AmountTint.INCOME, style = MaterialTheme.typography.titleSmall)
        }
        GlassCard(
            modifier = Modifier.weight(1f).padding(spacing.small),
        ) {
            Icon(Icons.AutoMirrored.Filled.TrendingDown, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(16.dp))
            Text(stringResource(R.string.label_expenses), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onErrorContainer)
            AmountText(amountMinor = expense, currencyCode = currencyCode, tint = AmountTint.EXPENSE, style = MaterialTheme.typography.titleSmall)
        }
        GlassCard(
            modifier = Modifier.weight(1f).padding(spacing.small),
        ) {
            Text(stringResource(R.string.label_savings), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(
                text = "${(savingsRate * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun IncomeExpenseChart(
    incomeValues: List<Float>,
    expenseValues: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val incomeColor = MaterialTheme.colorScheme.primary
    val expenseColor = MaterialTheme.colorScheme.error

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(incomeValues, expenseValues) {
        modelProducer.runTransaction {
            lineSeries {
                series(incomeValues)
                series(expenseValues)
            }
        }
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.Line(
                            LineCartesianLayer.LineFill.single(Fill(incomeColor.toArgb())),
                        ),
                        LineCartesianLayer.Line(
                            LineCartesianLayer.LineFill.single(Fill(expenseColor.toArgb())),
                        ),
                    ),
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(),
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxWidth().height(200.dp).padding(spacing.small),
        )
    }
}

@Composable
private fun CategoryBar(
    name: String,
    amountMinor: Long,
    percentage: Float,
    colorArgb: Long,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    val barColor = if (colorArgb != 0L) Color(colorArgb.toULong()) else MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.screenPadding, vertical = spacing.extraSmall),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            AmountText(amountMinor = amountMinor, currencyCode = currencyCode, tint = AmountTint.EXPENSE, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(spacing.extraSmall))
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun StatsGrid(
    avgDaily: Long,
    highestCategoryName: String,
    highestCategoryAmount: Long,
    highestTxTitle: String,
    highestTxAmount: Long,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.screenPadding)) {
        Column(
            modifier = Modifier.padding(spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            StatRow(label = stringResource(R.string.analytics_avg_daily), value = CurrencyFormatter.formatMinorUnits(avgDaily, currencyCode))
            HorizontalDivider()
            StatRow(label = stringResource(R.string.analytics_highest_category), value = if (highestCategoryName.isNotBlank()) "$highestCategoryName (${CurrencyFormatter.formatMinorUnits(highestCategoryAmount, currencyCode)})" else "—")
            HorizontalDivider()
            StatRow(label = stringResource(R.string.analytics_highest_transaction), value = if (highestTxTitle.isNotBlank()) "$highestTxTitle (${CurrencyFormatter.formatMinorUnits(highestTxAmount, currencyCode)})" else "—")
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, textAlign = TextAlign.End, modifier = Modifier.width(180.dp))
    }
}
