package com.jerries.expense.feature.budgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.jerries.expense.core.designsystem.icon.JeIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(
    modifier: Modifier = Modifier,
    viewModel: BudgetsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_budgets)) }) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (!state.isLoading) {
            EmptyContent(
                icon = JeIcons.account(com.jerries.expense.domain.model.AccountType.SAVINGS),
                title = stringResource(R.string.empty_budgets_title),
                message = stringResource(R.string.empty_budgets_message),
                modifier = Modifier.padding(padding),
            )
        }
    }
}
