package com.jerries.expense.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.EmptyContent
import com.jerries.expense.core.designsystem.component.LoadingContent
import com.jerries.expense.core.designsystem.component.SectionHeader
import com.jerries.expense.core.designsystem.component.SummaryCard
import com.jerries.expense.core.designsystem.component.TransactionRow
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.core.designsystem.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
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
                contentPadding = PaddingValues(bottom = spacing.large),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                item(key = "balance") {
                    SummaryCard(
                        label = stringResource(R.string.dashboard_total_balance),
                        amountMinor = state.totalBalanceMinor,
                        currencyCode = state.currencyCode,
                        modifier = Modifier.padding(horizontal = spacing.screenPadding),
                    )
                }
                item(key = "recent-header") {
                    SectionHeader(stringResource(R.string.dashboard_recent_transactions))
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
                        )
                    }
                }
            }
        }
    }
}
