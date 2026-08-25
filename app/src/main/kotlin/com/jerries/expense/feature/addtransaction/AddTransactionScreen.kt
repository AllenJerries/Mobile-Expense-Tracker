package com.jerries.expense.feature.addtransaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.JeTextField
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.domain.model.TransactionType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val MILLIS_PER_DAY = 86_400_000L

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddTransactionViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AddTransactionEvent.Saved -> onNavigateUp()
                is AddTransactionEvent.SaveFailed -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_transaction_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
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
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = state.type == TransactionType.EXPENSE,
                    onClick = { viewModel.onTypeChange(TransactionType.EXPENSE) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                ) {
                    Text(stringResource(R.string.transaction_type_expense))
                }
                SegmentedButton(
                    selected = state.type == TransactionType.INCOME,
                    onClick = { viewModel.onTypeChange(TransactionType.INCOME) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                ) {
                    Text(stringResource(R.string.transaction_type_income))
                }
                SegmentedButton(
                    selected = state.type == TransactionType.TRANSFER,
                    onClick = { viewModel.onTypeChange(TransactionType.TRANSFER) },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                ) {
                    Text(stringResource(R.string.transaction_type_transfer))
                }
            }

            AmountInput(
                value = state.amountInput,
                onValueChange = viewModel::onAmountChange,
                currencyCode = state.currencyCode,
                isError = state.amountError,
            )

            JeTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = stringResource(R.string.title_label),
                supportingText = stringResource(R.string.title_hint),
            )

            DropdownField(
                label = stringResource(R.string.account_label),
                options = state.accounts.map { it.name },
                selected = state.accounts.firstOrNull { it.id == state.accountId }?.name,
                isError = state.accountError,
                onSelect = { name ->
                    state.accounts.firstOrNull { it.name == name }
                        ?.let { viewModel.onAccountChange(it.id) }
                },
            )

            AnimatedVisibility(visible = state.type == TransactionType.TRANSFER) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                    DropdownField(
                        label = stringResource(R.string.destination_account_label),
                        options = state.accounts
                            .filter { it.id != state.accountId }
                            .map { it.name },
                        selected = state.accounts
                            .firstOrNull { it.id == state.destinationAccountId }?.name,
                        isError = state.destinationAccountError,
                        onSelect = { name ->
                            state.accounts.firstOrNull { it.name == name }
                                ?.let { viewModel.onDestinationAccountChange(it.id) }
                        },
                    )
                }
            }

            AnimatedVisibility(visible = state.type != TransactionType.TRANSFER) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                    DropdownField(
                        label = stringResource(R.string.category_label),
                        options = state.filteredCategories.map { it.name },
                        selected = state.filteredCategories
                            .firstOrNull { it.id == state.categoryId }?.name,
                        isError = state.categoryError,
                        onSelect = { name ->
                            state.filteredCategories.firstOrNull { it.name == name }
                                ?.let { viewModel.onCategoryChange(it.id) }
                        },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                DateField(
                    epochDay = state.dateEpochDay,
                    onDateSelected = viewModel::onDateChange,
                    modifier = Modifier.weight(1f),
                )
                TimeField(
                    hour = state.timeHour,
                    minute = state.timeMinute,
                    onTimeSelected = viewModel::onTimeChange,
                    modifier = Modifier.weight(1f),
                )
            }

            PaymentMethodSelector(
                selected = state.paymentMethod,
                methods = state.paymentMethods,
                onSelect = viewModel::onPaymentMethodChange,
            )

            JeTextField(
                value = state.note,
                onValueChange = viewModel::onNoteChange,
                label = stringResource(R.string.note_label),
                supportingText = stringResource(R.string.note_hint),
                singleLine = false,
            )

            ReceiptAttachment(
                hasAttachment = state.attachmentUri != null,
                onToggle = {
                    viewModel.onAttachmentChange(
                        if (state.attachmentUri != null) null else "content://placeholder"
                    )
                },
            )

            Button(
                onClick = viewModel::save,
                enabled = state.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.medium),
                shape = MaterialTheme.shapes.medium,
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@Composable
private fun AmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    currencyCode: String,
    isError: Boolean,
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Amount input" },
            label = { Text(stringResource(R.string.amount_label)) },
            prefix = {
                Text(
                    text = currencyCode,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            },
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = isError,
            supportingText = if (isError) {
                { Text(stringResource(R.string.error_invalid_amount)) }
            } else {
                null
            },
            shape = MaterialTheme.shapes.large,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    options: List<String>,
    selected: String?,
    isError: Boolean,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it && options.isNotEmpty() },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            isError = isError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            shape = MaterialTheme.shapes.medium,
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    epochDay: Long,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPicker by remember { mutableStateOf(false) }
    val dateText = remember(epochDay) {
        LocalDate.ofEpochDay(epochDay).format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.date_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPicker = true },
            shape = MaterialTheme.shapes.medium,
            enabled = false,
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showPicker = true },
        )
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = epochDay * MILLIS_PER_DAY,
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(millis / MILLIS_PER_DAY)
                        }
                        showPicker = false
                    },
                ) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeField(
    hour: Int,
    minute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPicker by remember { mutableStateOf(false) }
    val timeText = remember(hour, minute) {
        String.format("%02d:%02d", hour, minute)
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = timeText,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.time_label)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = stringResource(R.string.time_label),
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPicker = true },
            shape = MaterialTheme.shapes.medium,
            enabled = false,
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showPicker = true },
        )
    }

    if (showPicker) {
        val state = rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = true,
        )
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeSelected(state.hour, state.minute)
                    showPicker = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                TimePicker(state = state)
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PaymentMethodSelector(
    selected: String?,
    methods: List<String>,
    onSelect: (String?) -> Unit,
) {
    val spacing = LocalSpacing.current
    Column {
        Text(
            text = stringResource(R.string.payment_method_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.small))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        ) {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text(stringResource(R.string.none)) },
            )
            methods.forEach { method ->
                FilterChip(
                    selected = selected == method,
                    onClick = { onSelect(method) },
                    label = { Text(method) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ReceiptAttachment(
    hasAttachment: Boolean,
    onToggle: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = stringResource(R.string.receipt_label),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(spacing.medium))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.receipt_label),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = if (hasAttachment) "Receipt attached" else stringResource(R.string.add_receipt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
