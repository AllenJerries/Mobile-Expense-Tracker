package com.jerries.expense.feature.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.EmptyContent
import com.jerries.expense.core.designsystem.component.GlassCard
import com.jerries.expense.core.designsystem.component.GlassTopBar
import com.jerries.expense.core.designsystem.component.LoadingContent
import com.jerries.expense.core.designsystem.component.TransactionRow
import com.jerries.expense.core.designsystem.component.glassConfig
import com.jerries.expense.core.designsystem.component.staggeredListItemEntry
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.domain.model.TransactionType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionsScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val config = glassConfig()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val deletedMessage = stringResource(R.string.transaction_deleted)
    val undoLabel = stringResource(R.string.undo)

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        GlassTopBar(
            title = {
                Text(
                    text = stringResource(R.string.nav_transactions),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            actions = {
                IconButton(onClick = { viewModel.onToggleFilters() }) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = stringResource(R.string.label_filters),
                        tint = if (state.hasActiveFilters) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            },
        )

        // Search bar with glass style
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.screenPadding, vertical = spacing.small),
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Search transactions" },
                placeholder = { Text(stringResource(R.string.search_transactions_hint)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(config.cornerRadius),
            )
        }

        // Filter panel
        AnimatedVisibility(
            visible = state.showFilters,
            modifier = Modifier.animateContentSize(),
        ) {
            FilterPanel(
                state = state,
                onTypeFilterChange = viewModel::onTypeFilterChange,
                onCategoryFilterChange = viewModel::onCategoryFilterChange,
                onAccountFilterChange = viewModel::onAccountFilterChange,
                onPaymentMethodFilterChange = viewModel::onPaymentMethodFilterChange,
                onSortOrderChange = viewModel::onSortOrderChange,
                onClearFilters = viewModel::onClearFilters,
            )
        }

        // Active filter chips
        if (state.hasActiveFilters && !state.showFilters) {
            ActiveFilterChips(
                state = state,
                onTypeFilterChange = viewModel::onTypeFilterChange,
                onCategoryFilterChange = viewModel::onCategoryFilterChange,
                onAccountFilterChange = viewModel::onAccountFilterChange,
                onPaymentMethodFilterChange = viewModel::onPaymentMethodFilterChange,
                onClearFilters = viewModel::onClearFilters,
            )
        }

        when {
            state.isLoading -> LoadingContent()

            state.isEmpty -> EmptyContent(
                icon = JeIcons.Fallback,
                title = stringResource(R.string.empty_transactions_title),
                message = stringResource(R.string.empty_transactions_message),
            )

            state.isFilteredEmpty -> EmptyContent(
                icon = JeIcons.Fallback,
                title = stringResource(R.string.empty_search_title),
                message = stringResource(R.string.empty_search_message),
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = spacing.large),
                verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
            ) {
                itemsIndexed(state.filteredTransactions, key = { _, item -> item.id }) { index, row ->
                    TransactionRow(
                        model = row,
                        currencyCode = state.currencyCode,
                        todayEpochDay = state.todayEpochDay,
                        modifier = Modifier
                            .clickable {
                                onNavigateToDetail(row.id)
                            }
                            .staggeredListItemEntry(index),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterPanel(
    state: TransactionsUiState,
    onTypeFilterChange: (TransactionType?) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onAccountFilterChange: (String?) -> Unit,
    onPaymentMethodFilterChange: (String?) -> Unit,
    onSortOrderChange: (TransactionSortOrder) -> Unit,
    onClearFilters: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding, vertical = spacing.small),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        // Sort
        Text(
            text = stringResource(R.string.sort_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        ) {
            TransactionSortOrder.entries.forEach { order ->
                val label = when (order) {
                    TransactionSortOrder.NEWEST -> stringResource(R.string.sort_newest)
                    TransactionSortOrder.OLDEST -> stringResource(R.string.sort_oldest)
                    TransactionSortOrder.HIGHEST -> stringResource(R.string.sort_highest)
                    TransactionSortOrder.LOWEST -> stringResource(R.string.sort_lowest)
                }
                FilterChip(
                    selected = state.sortOrder == order,
                    onClick = { onSortOrderChange(order) },
                    label = { Text(label) },
                )
            }
        }

        // Type filter
        Text(
            text = stringResource(R.string.filter_type),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        ) {
            AnimatedFilterChip(
                label = stringResource(R.string.filter_all),
                selected = state.typeFilter == null,
                onClick = { onTypeFilterChange(null) },
            )
            TransactionType.entries.forEach { type ->
                AnimatedFilterChip(
                    label = when (type) {
                        TransactionType.EXPENSE -> stringResource(R.string.transaction_type_expense)
                        TransactionType.INCOME -> stringResource(R.string.transaction_type_income)
                        TransactionType.TRANSFER -> stringResource(R.string.transaction_type_transfer)
                    },
                    selected = state.typeFilter == type,
                    onClick = { onTypeFilterChange(type) },
                )
            }
        }

        // Account filter
        if (state.accounts.isNotEmpty()) {
            Text(
                text = stringResource(R.string.filter_account),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
            ) {
                FilterChip(
                    selected = state.accountFilter == null,
                    onClick = { onAccountFilterChange(null) },
                    label = { Text(stringResource(R.string.filter_all)) },
                )
                state.accounts.forEach { account ->
                    FilterChip(
                        selected = state.accountFilter == account.id,
                        onClick = { onAccountFilterChange(account.id) },
                        label = { Text(account.name) },
                    )
                }
            }
        }

        // Clear button
        if (state.hasActiveFilters) {
            androidx.compose.material3.TextButton(
                onClick = onClearFilters,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.reset))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveFilterChips(
    state: TransactionsUiState,
    onTypeFilterChange: (TransactionType?) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onAccountFilterChange: (String?) -> Unit,
    onPaymentMethodFilterChange: (String?) -> Unit,
    onClearFilters: () -> Unit,
) {
    val spacing = LocalSpacing.current
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenPadding, vertical = spacing.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
    ) {
        if (state.typeFilter != null) {
            FilterChip(
                selected = true,
                onClick = { onTypeFilterChange(null) },
                label = {
                    Text(
                        when (state.typeFilter) {
                            TransactionType.EXPENSE -> stringResource(R.string.transaction_type_expense)
                            TransactionType.INCOME -> stringResource(R.string.transaction_type_income)
                            TransactionType.TRANSFER -> stringResource(R.string.transaction_type_transfer)
                        }
                    )
                },
            )
        }
        if (state.accountFilter != null) {
            val accountName = state.accounts.firstOrNull { it.id == state.accountFilter }?.name ?: ""
            FilterChip(
                selected = true,
                onClick = { onAccountFilterChange(null) },
                label = { Text(accountName) },
            )
        }
        FilterChip(
            selected = false,
            onClick = onClearFilters,
            label = { Text(stringResource(R.string.reset)) },
        )
    }
}

@Composable
private fun AnimatedFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val animatedBgColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(durationMillis = 200),
        label = "chipBg",
    )
    val animatedContentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 200),
        label = "chipContent",
    )
    val animatedBorderAlpha by animateFloatAsState(
        targetValue = if (selected) 0.8f else 0.2f,
        animationSpec = tween(durationMillis = 200),
        label = "chipBorder",
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = animatedBgColor,
        border = BorderStroke(
            width = 1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = animatedBorderAlpha),
        ),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = animatedContentColor,
        )
    }
}
