package com.jerries.expense.feature.reports

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.AmountText
import com.jerries.expense.core.designsystem.component.AmountTint
import com.jerries.expense.core.designsystem.component.EmptyContent
import com.jerries.expense.core.designsystem.component.ErrorContent
import com.jerries.expense.core.designsystem.component.JeElevatedCard
import com.jerries.expense.core.designsystem.component.LoadingContent
import com.jerries.expense.core.designsystem.component.SectionHeader
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.core.util.CurrencyFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showExportMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reports_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                actions = {
                    IconButton(onClick = { showExportMenu = true }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(R.string.reports_export_share),
                        )
                    }
                    DropdownMenu(
                        expanded = showExportMenu,
                        onDismissRequest = { showExportMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.reports_export_csv)) },
                            onClick = {
                                showExportMenu = false
                                scope.launch {
                                    try {
                                        val csv = viewModel.generateCsv()
                                        shareTextFile(context, csv, "expense_report.csv", "text/csv")
                                        snackbarHostState.showSnackbar(context.getString(R.string.reports_exported))
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(e.message ?: context.getString(R.string.error_generic_title))
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Description,
                                    contentDescription = null,
                                )
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.reports_export_json)) },
                            onClick = {
                                showExportMenu = false
                                scope.launch {
                                    try {
                                        val json = viewModel.generateJson()
                                        shareTextFile(context, json, "expense_report.json", "application/json")
                                        snackbarHostState.showSnackbar(context.getString(R.string.reports_exported))
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(e.message ?: context.getString(R.string.error_generic_title))
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.FileDownload,
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            ReportTypeSelector(
                selectedType = state.reportType,
                onTypeSelected = viewModel::onReportTypeChange,
            )

            DateRangeHeader(
                reportType = state.reportType,
                monthLabel = state.monthLabel,
                currentMonth = state.currentMonth,
                currentYear = state.currentYear,
                onPreviousMonth = viewModel::onPreviousMonth,
                onNextMonth = viewModel::onNextMonth,
                onPreviousYear = viewModel::onPreviousYear,
                onNextYear = viewModel::onNextYear,
            )

            when {
                state.isLoading -> LoadingContent()
                state.error != null -> ErrorContent(
                    title = stringResource(R.string.error_generic_title),
                    message = state.error ?: "",
                )
                !state.hasData -> EmptyContent(
                    icon = Icons.Filled.Description,
                    title = stringResource(R.string.empty_generic_title),
                    message = stringResource(R.string.reports_no_data),
                )
                else -> ReportContent(
                    state = state,
                )
            }
        }
    }
}

@Composable
private fun ReportTypeSelector(
    selectedType: ReportType,
    onTypeSelected: (ReportType) -> Unit,
) {
    val spacing = LocalSpacing.current
    val chipLabels = listOf(
        ReportType.MONTHLY to R.string.reports_monthly,
        ReportType.YEARLY to R.string.reports_yearly,
        ReportType.INCOME to R.string.reports_income,
        ReportType.EXPENSE to R.string.reports_expense,
        ReportType.CATEGORY to R.string.reports_category,
        ReportType.BUDGET to R.string.reports_budget,
        ReportType.SAVINGS to R.string.reports_savings,
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = spacing.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        items(chipLabels.size) { index ->
            val (type, labelRes) = chipLabels[index]
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = {
                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        }
    }
}

@Composable
private fun DateRangeHeader(
    reportType: ReportType,
    monthLabel: String,
    currentMonth: java.time.YearMonth,
    currentYear: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onPreviousYear: () -> Unit,
    onNextYear: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding, vertical = spacing.small),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {
            when (reportType) {
                ReportType.YEARLY -> onPreviousYear()
                else -> onPreviousMonth()
            }
        }) {
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = null,
            )
        }

        val dateLabel = if (reportType == ReportType.YEARLY) {
            currentYear.toString()
        } else {
            currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        }
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = spacing.small),
        )

        IconButton(onClick = {
            when (reportType) {
                ReportType.YEARLY -> onNextYear()
                else -> onNextMonth()
            }
        }) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ReportContent(
    state: ReportsUiState,
) {
    val spacing = LocalSpacing.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = spacing.huge),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
    ) {
        when (state.reportType) {
            ReportType.MONTHLY, ReportType.YEARLY -> {
                item(key = "summary") {
                    SummaryCardsRow(
                        income = state.income,
                        expenses = state.expenses,
                        savings = state.savings,
                        currencyCode = state.currencyCode,
                    )
                }
                if (state.categoryBreakdown.isNotEmpty()) {
                    item(key = "cat-header") {
                        SectionHeader(text = stringResource(R.string.reports_category_breakdown))
                    }
                    items(state.categoryBreakdown, key = { it.categoryId }) { cat ->
                        CategoryBreakdownRow(
                            name = cat.categoryName,
                            amountMinor = cat.totalMinor,
                            totalMinor = state.expenses.coerceAtLeast(1),
                            currencyCode = state.currencyCode,
                        )
                    }
                }
                if (state.accounts.isNotEmpty()) {
                    item(key = "accounts-header") {
                        SectionHeader(text = stringResource(R.string.reports_account_summary))
                    }
                    items(state.accounts, key = { it.id }) { account ->
                        AccountRow(
                            name = account.name,
                            balanceMinor = account.initialBalanceMinor,
                            currencyCode = account.currencyCode,
                        )
                    }
                }
            }

            ReportType.INCOME -> {
                item(key = "income-total") {
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
                                text = stringResource(R.string.reports_total_income),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            AmountText(
                                amountMinor = state.income,
                                currencyCode = state.currencyCode,
                                tint = AmountTint.INCOME,
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        }
                    }
                }
                if (state.incomeCategoryBreakdown.isNotEmpty()) {
                    item(key = "cat-header") {
                        SectionHeader(text = stringResource(R.string.reports_category_breakdown))
                    }
                    items(state.incomeCategoryBreakdown, key = { it.categoryId }) { cat ->
                        CategoryBreakdownRow(
                            name = cat.categoryName,
                            amountMinor = cat.totalMinor,
                            totalMinor = state.income.coerceAtLeast(1),
                            currencyCode = state.currencyCode,
                        )
                    }
                }
            }

            ReportType.EXPENSE -> {
                item(key = "expense-total") {
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
                                text = stringResource(R.string.reports_total_expenses),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            AmountText(
                                amountMinor = state.expenses,
                                currencyCode = state.currencyCode,
                                tint = AmountTint.EXPENSE,
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        }
                    }
                }
                if (state.categoryBreakdown.isNotEmpty()) {
                    item(key = "cat-header") {
                        SectionHeader(text = stringResource(R.string.reports_category_breakdown))
                    }
                    items(state.categoryBreakdown, key = { it.categoryId }) { cat ->
                        CategoryBreakdownRow(
                            name = cat.categoryName,
                            amountMinor = cat.totalMinor,
                            totalMinor = state.expenses.coerceAtLeast(1),
                            currencyCode = state.currencyCode,
                        )
                    }
                }
            }

            ReportType.CATEGORY -> {
                if (state.categoryBreakdown.isNotEmpty()) {
                    item(key = "expense-header") {
                        SectionHeader(text = stringResource(R.string.reports_expense))
                    }
                    items(state.categoryBreakdown, key = { "exp-${it.categoryId}" }) { cat ->
                        CategoryBreakdownRow(
                            name = cat.categoryName,
                            amountMinor = cat.totalMinor,
                            totalMinor = state.expenses.coerceAtLeast(1),
                            currencyCode = state.currencyCode,
                        )
                    }
                }
                if (state.incomeCategoryBreakdown.isNotEmpty()) {
                    item(key = "income-header") {
                        SectionHeader(text = stringResource(R.string.reports_income))
                    }
                    items(state.incomeCategoryBreakdown, key = { "inc-${it.categoryId}" }) { cat ->
                        CategoryBreakdownRow(
                            name = cat.categoryName,
                            amountMinor = cat.totalMinor,
                            totalMinor = state.income.coerceAtLeast(1),
                            currencyCode = state.currencyCode,
                        )
                    }
                }
            }

            ReportType.BUDGET -> {
                if (state.budgetSpendings.isNotEmpty()) {
                    items(state.budgetSpendings, key = { it.budget.id }) { spending ->
                        BudgetRow(
                            name = spending.budget.categoryId ?: stringResource(R.string.budget_overall),
                            spentMinor = spending.spentMinor,
                            limitMinor = spending.limitMinor,
                            percentage = spending.percentage.toFloat(),
                            currencyCode = state.currencyCode,
                        )
                    }
                }
            }

            ReportType.SAVINGS -> {
                if (state.goals.isNotEmpty()) {
                    items(state.goals, key = { it.id }) { goal ->
                        GoalRow(
                            name = goal.name,
                            savedMinor = goal.savedMinor,
                            targetMinor = goal.targetMinor,
                            progress = goal.progress.toFloat(),
                            currencyCode = state.currencyCode,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCardsRow(
    income: Long,
    expenses: Long,
    savings: Long,
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
            title = stringResource(R.string.reports_total_income),
            amountMinor = income,
            currencyCode = currencyCode,
            tint = AmountTint.INCOME,
            modifier = Modifier.weight(1f),
        )
        SummaryMiniCard(
            title = stringResource(R.string.reports_total_expenses),
            amountMinor = expenses,
            currencyCode = currencyCode,
            tint = AmountTint.EXPENSE,
            modifier = Modifier.weight(1f),
        )
        SummaryMiniCard(
            title = stringResource(R.string.reports_total_savings),
            amountMinor = savings,
            currencyCode = currencyCode,
            tint = AmountTint.NEUTRAL,
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
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    androidx.compose.material3.Card(
        modifier = modifier,
        colors = androidx.compose.material3.CardDefaults.cardColors(
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
private fun CategoryBreakdownRow(
    name: String,
    amountMinor: Long,
    totalMinor: Long,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    val fraction = if (totalMinor > 0) (amountMinor.toFloat() / totalMinor.toFloat()).coerceIn(0f, 1f) else 0f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding, vertical = spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LinearProgressIndicator(
            progress = { fraction },
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
                text = "${(fraction * 100).toInt()}%",
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

@Composable
private fun AccountRow(
    name: String,
    balanceMinor: Long,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    JeElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.cardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            AmountText(
                amountMinor = balanceMinor,
                currencyCode = currencyCode,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun BudgetRow(
    name: String,
    spentMinor: Long,
    limitMinor: Long,
    percentage: Float,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    val progress = percentage.coerceIn(0f, 1f)
    val isExceeded = percentage > 1f

    JeElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = name,
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
                color = if (isExceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            if (isExceeded) {
                Text(
                    text = stringResource(R.string.budget_exceeded),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun GoalRow(
    name: String,
    savedMinor: Long,
    targetMinor: Long,
    progress: Float,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    val progressClamped = progress.coerceIn(0f, 1f)

    JeElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LinearProgressIndicator(
                progress = { progressClamped },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (progress >= 1f) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = CurrencyFormatter.formatMinorUnits(savedMinor, currencyCode),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.reports_progress) + " " + CurrencyFormatter.formatMinorUnits(targetMinor, currencyCode),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private suspend fun shareTextFile(context: Context, content: String, fileName: String, mimeType: String) {
    withContext(Dispatchers.IO) {
        try {
            val file = java.io.File(context.cacheDir, fileName).apply {
                writeText(content)
            }
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(sendIntent, null))
        } catch (_: Exception) {
        }
    }
}
