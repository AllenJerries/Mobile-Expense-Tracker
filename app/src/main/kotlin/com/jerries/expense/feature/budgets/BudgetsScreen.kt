package com.jerries.expense.feature.budgets

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.AmountText
import com.jerries.expense.core.designsystem.component.AmountTint
import com.jerries.expense.core.designsystem.component.EmptyContent
import com.jerries.expense.core.designsystem.component.staggeredListItemEntry
import com.jerries.expense.core.designsystem.component.GlassCard
import com.jerries.expense.core.designsystem.component.scaleOnPress
import com.jerries.expense.core.designsystem.component.GlassTopBar
import com.jerries.expense.core.designsystem.component.LoadingContent
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.core.util.CurrencyFormatter
import com.jerries.expense.domain.model.BudgetPeriod

@Composable
fun BudgetsScreen(
    modifier: Modifier = Modifier,
    viewModel: BudgetsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }
    val budgetSavedMessage = stringResource(R.string.budget_saved)
    val budgetDeletedMessage = stringResource(R.string.budget_deleted)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BudgetEvent.Error -> snackbarHostState.showSnackbar(event.message)
                BudgetEvent.Saved -> snackbarHostState.showSnackbar(budgetSavedMessage)
                BudgetEvent.Deleted -> snackbarHostState.showSnackbar(budgetDeletedMessage)
            }
        }
    }

    if (state.showForm) {
        BudgetFormDialog(
            form = state.form,
            categories = state.categories,
            onNameChange = viewModel::onFormNameChange,
            onLimitChange = viewModel::onFormLimitChange,
            onCategoryChange = viewModel::onFormCategoryChange,
            onPeriodChange = viewModel::onFormPeriodChange,
            onThresholdChange = viewModel::onFormThresholdChange,
            onSave = viewModel::onSaveBudget,
            onDismiss = { viewModel.onShowForm(false) },
            currencyCode = state.currencyCode,
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(title = { Text(stringResource(R.string.nav_budgets), style = MaterialTheme.typography.titleLarge) })
            when {
                state.isLoading -> LoadingContent(Modifier.weight(1f))
                state.isEmpty -> EmptyContent(
                    icon = Icons.Filled.Savings,
                    title = stringResource(R.string.empty_budgets_title),
                    message = stringResource(R.string.empty_budgets_message),
                    modifier = Modifier.weight(1f),
                )
                else -> LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.small),
                ) {
                    itemsIndexed(state.budgetSpendings, key = { _, item -> item.budget.id }) { index, spending ->
                        BudgetCard(
                            spending = spending,
                            currencyCode = state.currencyCode,
                            onEdit = { viewModel.onEditBudget(spending.budget) },
                            onDelete = { viewModel.onDeleteBudget(spending.budget.id) },
                            modifier = Modifier.staggeredListItemEntry(index),
                        )
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { viewModel.onShowForm(true) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).scaleOnPress(),
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_budget))
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun BudgetCard(
    spending: com.jerries.expense.domain.usecase.BudgetSpending,
    currencyCode: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val progress = spending.percentage.coerceIn(0.0, 1.5).toFloat()
    val isExceeded = spending.percentage > 1.0
    val isApproaching = spending.percentage >= 0.8 && !isExceeded
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "budgetProgress",
    )

    GlassCard(modifier = modifier.fillMaxWidth().padding(horizontal = spacing.screenPadding)) {
        Column(modifier = Modifier.padding(spacing.cardPadding).animateContentSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = spending.budget.categoryId ?: stringResource(R.string.budget_overall),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.height(spacing.small))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AmountText(amountMinor = spending.spentMinor, currencyCode = currencyCode, tint = AmountTint.EXPENSE, style = MaterialTheme.typography.bodyLarge)
                Text(" / ${CurrencyFormatter.formatMinorUnits(spending.limitMinor, currencyCode)}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(spacing.small))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.extraSmall),
                color = when {
                    isExceeded -> MaterialTheme.colorScheme.error
                    isApproaching -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(Modifier.height(spacing.extraSmall))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val remaining = (spending.limitMinor - spending.spentMinor).coerceAtLeast(0)
                Text(
                    text = if (isExceeded) stringResource(R.string.budget_exceeded) else if (isApproaching) stringResource(R.string.budget_approaching) else stringResource(R.string.budget_healthy),
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        isExceeded -> MaterialTheme.colorScheme.error
                        isApproaching -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    },
                )
                Text(
                    text = stringResource(R.string.label_left, CurrencyFormatter.formatMinorUnits(remaining, currencyCode)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetFormDialog(
    form: com.jerries.expense.feature.budgets.BudgetFormState,
    categories: List<com.jerries.expense.domain.model.Category>,
    onNameChange: (String) -> Unit,
    onLimitChange: (String) -> Unit,
    onCategoryChange: (String?) -> Unit,
    onPeriodChange: (BudgetPeriod) -> Unit,
    onThresholdChange: (Double) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    val expenseCategories = categories.filter { it.kind == com.jerries.expense.domain.model.CategoryKind.EXPENSE }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (form.isEditing) stringResource(R.string.edit_budget) else stringResource(R.string.add_budget)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                OutlinedTextField(
                    value = form.limitInput,
                    onValueChange = onLimitChange,
                    label = { Text(stringResource(R.string.budget_limit_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )
                Text(stringResource(R.string.budget_category_label), style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
                    FilterChip(selected = form.categoryId == null, onClick = { onCategoryChange(null) }, label = { Text(stringResource(R.string.none)) })
                    expenseCategories.take(3).forEach { cat ->
                        FilterChip(selected = form.categoryId == cat.id, onClick = { onCategoryChange(cat.id) }, label = { Text(cat.name) })
                    }
                }
                Text(stringResource(R.string.budget_period_label), style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
                    BudgetPeriod.entries.take(3).forEach { period ->
                        FilterChip(
                            selected = form.period == period,
                            onClick = { onPeriodChange(period) },
                            label = { Text(period.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        )
                    }
                }
                Text("${stringResource(R.string.budget_threshold_label)}: ${(form.threshold * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
                Slider(value = form.threshold.toFloat(), onValueChange = { onThresholdChange(it.toDouble()) }, valueRange = 0.5f..1.0f, steps = 4)
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = form.limitInput.isNotBlank()) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}
