package com.jerries.expense.feature.transactiondetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.AmountText
import com.jerries.expense.core.designsystem.component.AmountTint
import com.jerries.expense.core.designsystem.component.CategoryIcon
import com.jerries.expense.core.designsystem.component.GlassCard
import com.jerries.expense.core.designsystem.component.GlassTopBar
import com.jerries.expense.core.designsystem.component.glassConfig
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                TransactionDetailEvent.Deleted -> onNavigateUp()
                TransactionDetailEvent.NotFound -> onNavigateUp()
                TransactionDetailEvent.NavigateToEdit -> {
                    onNavigateToEdit(state.transaction?.id ?: return@collect)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.confirm_delete_title)) },
            text = { Text(stringResource(R.string.confirm_delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteTransaction()
                }) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        GlassTopBar(
            title = {
                Text(
                    text = stringResource(R.string.transaction_detail_title),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.navigateToEdit() }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.edit),
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            },
        )

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            state.isEmpty || state.transaction == null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.transaction_not_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                Spacer(modifier = Modifier.height(spacing.small))

                // Header with icon and amount
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevated = true,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.cardPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                    ) {
                        CategoryIcon(
                            icon = JeIcons.category(null),
                            contentDescription = state.typeText,
                            modifier = Modifier.size(56.dp),
                        )
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        AmountText(
                            amountMinor = state.transaction!!.amountMinor,
                            currencyCode = state.currencyCode,
                            tint = when (state.type) {
                                TransactionType.INCOME -> AmountTint.INCOME
                                TransactionType.EXPENSE -> AmountTint.EXPENSE
                                TransactionType.TRANSFER -> AmountTint.NEUTRAL
                            },
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }
                }

                // Details in glass card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                    ) {
                        DetailRow(
                            label = stringResource(R.string.transaction_detail_type),
                            value = state.typeText,
                        )
                        DetailRow(
                            label = stringResource(R.string.transaction_detail_account),
                            value = state.accountName,
                        )
                        if (state.type == TransactionType.TRANSFER) {
                            DetailRow(
                                label = stringResource(R.string.transaction_detail_destination),
                                value = state.destinationAccountName,
                            )
                        } else {
                            DetailRow(
                                label = stringResource(R.string.transaction_detail_category),
                                value = state.categoryName,
                            )
                        }
                        DetailRow(
                            label = stringResource(R.string.transaction_detail_date),
                            value = state.dateText,
                        )
                        DetailRow(
                            label = stringResource(R.string.transaction_detail_time),
                            value = state.timeText,
                        )
                        if (state.paymentMethod.isNotBlank() && state.paymentMethod != "—") {
                            DetailRow(
                                label = stringResource(R.string.transaction_detail_payment_method),
                                value = state.paymentMethod,
                            )
                        }
                        if (state.note.isNotBlank()) {
                            DetailRow(
                                label = stringResource(R.string.transaction_detail_note),
                                value = state.note,
                            )
                        }
                        if (state.hasAttachment) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = spacing.extraSmall),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CameraAlt,
                                    contentDescription = stringResource(R.string.transaction_detail_receipt),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(modifier = Modifier.width(spacing.small))
                                Text(
                                    text = stringResource(R.string.transaction_detail_receipt),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                // Timestamps in glass card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                    ) {
                        DetailRow(
                            label = stringResource(R.string.transaction_detail_created),
                            value = state.createdText,
                        )
                        DetailRow(
                            label = stringResource(R.string.transaction_detail_updated),
                            value = state.updatedText,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.large))
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.extraSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}
