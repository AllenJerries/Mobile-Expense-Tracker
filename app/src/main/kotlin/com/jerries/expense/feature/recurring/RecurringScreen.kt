package com.jerries.expense.feature.recurring

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.AmountText
import com.jerries.expense.core.designsystem.component.AmountTint
import com.jerries.expense.core.designsystem.component.EmptyContent
import com.jerries.expense.core.designsystem.component.LoadingContent
import com.jerries.expense.core.designsystem.component.SectionHeader
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.domain.model.CategoryKind
import com.jerries.expense.domain.model.RecurrenceFrequency
import com.jerries.expense.domain.model.TransactionType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringScreen(
    modifier: Modifier = Modifier,
    viewModel: RecurringViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }
    val recurringSavedMessage = stringResource(R.string.recurring_saved)
    val recurringDeletedMessage = stringResource(R.string.recurring_deleted)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RecurringEvent.Error -> snackbarHostState.showSnackbar(event.message)
                RecurringEvent.Saved -> snackbarHostState.showSnackbar(recurringSavedMessage)
                RecurringEvent.Deleted -> snackbarHostState.showSnackbar(recurringDeletedMessage)
            }
        }
    }

    if (state.showForm) {
        RecurringFormDialog(
            form = state.form,
            accounts = state.accounts,
            categories = state.categories.filter { if (state.form.type == TransactionType.EXPENSE) it.kind == CategoryKind.EXPENSE else it.kind == CategoryKind.INCOME },
            onTypeChange = viewModel::onFormTypeChange,
            onAmountChange = viewModel::onFormAmountChange,
            onAccountChange = viewModel::onFormAccountChange,
            onCategoryChange = viewModel::onFormCategoryChange,
            onDescriptionChange = viewModel::onFormDescriptionChange,
            onFrequencyChange = viewModel::onFormFrequencyChange,
            onSave = viewModel::onSaveRecurring,
            onDismiss = { viewModel.onShowForm(false) },
            currencyCode = state.currencyCode,
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.recurring_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onShowForm(true) }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_recurring))
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        when {
            state.isLoading -> LoadingContent(Modifier.padding(padding))
            state.isEmpty -> EmptyContent(
                icon = Icons.Filled.Repeat,
                title = stringResource(R.string.empty_generic_title),
                message = stringResource(R.string.recurring_empty_message),
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                if (state.activeList.isNotEmpty()) {
                    item { SectionHeader(stringResource(R.string.recurring_active)) }
                    items(state.activeList, key = { it.id }) { item ->
                        RecurringCard(
                            item = item,
                            currencyCode = state.currencyCode,
                            onDelete = { viewModel.onDeleteRecurring(item.id) },
                            onToggle = { viewModel.onToggleActive(item.id, false) },
                        )
                    }
                }
                if (state.inactiveList.isNotEmpty()) {
                    item { SectionHeader(stringResource(R.string.recurring_inactive)) }
                    items(state.inactiveList, key = { it.id }) { item ->
                        RecurringCard(
                            item = item,
                            currencyCode = state.currencyCode,
                            onDelete = { viewModel.onDeleteRecurring(item.id) },
                            onToggle = { viewModel.onToggleActive(item.id, true) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecurringCard(
    item: com.jerries.expense.domain.model.RecurringTransaction,
    currencyCode: String,
    onDelete: () -> Unit,
    onToggle: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.screenPadding),
        colors = if (item.active) CardDefaults.cardColors() else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(spacing.cardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.description ?: item.type.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(item.frequency.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val nextDate = LocalDate.ofEpochDay(item.nextOccurrenceEpochDay).format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                Text(stringResource(R.string.next_occurrence_format, nextDate), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                AmountText(amountMinor = item.amountMinor, currencyCode = currencyCode, tint = if (item.type == TransactionType.EXPENSE) AmountTint.EXPENSE else AmountTint.INCOME)
                Row {
                    TextButton(onClick = onToggle) { Text(if (item.active) stringResource(R.string.pause) else stringResource(R.string.resume)) }
                    IconButton(onClick = onDelete, modifier = Modifier.padding(0.dp)) { Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurringFormDialog(
    form: RecurringFormState,
    accounts: List<com.jerries.expense.domain.model.Account>,
    categories: List<com.jerries.expense.domain.model.Category>,
    onTypeChange: (TransactionType) -> Unit,
    onAmountChange: (String) -> Unit,
    onAccountChange: (String) -> Unit,
    onCategoryChange: (String?) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onFrequencyChange: (RecurrenceFrequency) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    var accountExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_recurring)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(selected = form.type == TransactionType.EXPENSE, onClick = { onTypeChange(TransactionType.EXPENSE) }, shape = SegmentedButtonDefaults.itemShape(0, 2)) { Text(stringResource(R.string.transaction_type_expense)) }
                    SegmentedButton(selected = form.type == TransactionType.INCOME, onClick = { onTypeChange(TransactionType.INCOME) }, shape = SegmentedButtonDefaults.itemShape(1, 2)) { Text(stringResource(R.string.transaction_type_income)) }
                }
                OutlinedTextField(value = form.amountInput, onValueChange = onAmountChange, label = { Text(stringResource(R.string.amount_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
                ExposedDropdownMenuBox(expanded = accountExpanded, onExpandedChange = { accountExpanded = it }) {
                    OutlinedTextField(value = accounts.firstOrNull { it.id == form.accountId }?.name ?: "", onValueChange = {}, readOnly = true, label = { Text(stringResource(R.string.account_label)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(accountExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable), shape = MaterialTheme.shapes.medium)
                    ExposedDropdownMenu(expanded = accountExpanded, onDismissRequest = { accountExpanded = false }) {
                        accounts.forEach { acc -> DropdownMenuItem(text = { Text(acc.name) }, onClick = { onAccountChange(acc.id); accountExpanded = false }) }
                    }
                }
                OutlinedTextField(value = form.description, onValueChange = onDescriptionChange, label = { Text(stringResource(R.string.recurring_description_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
                Text(stringResource(R.string.recurring_frequency_label), style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
                    RecurrenceFrequency.entries.take(4).forEach { freq ->
                        FilterChip(selected = form.frequency == freq, onClick = { onFrequencyChange(freq) }, label = { Text(freq.name.lowercase().replaceFirstChar { it.uppercase() }) })
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onSave, enabled = form.amountInput.isNotBlank() && form.accountId != null) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}
