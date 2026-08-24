package com.jerries.expense.feature.transactions

import androidx.compose.foundation.layout.Arrangement
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
import com.jerries.expense.core.designsystem.component.TransactionRow
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.core.designsystem.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.nav_transactions)) })
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        when {
            state.isLoading -> LoadingContent(Modifier.padding(padding))

            state.isEmpty -> EmptyContent(
                icon = JeIcons.Fallback,
                title = stringResource(R.string.empty_transactions_title),
                message = stringResource(R.string.empty_transactions_message),
                modifier = Modifier.padding(padding),
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = spacing.large),
                verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
            ) {
                items(state.transactions, key = { it.id }) { row ->
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
