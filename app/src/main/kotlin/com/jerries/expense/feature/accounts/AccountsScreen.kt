package com.jerries.expense.feature.accounts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.AmountText
import com.jerries.expense.core.designsystem.component.CategoryIcon
import com.jerries.expense.core.designsystem.component.EmptyContent
import com.jerries.expense.core.designsystem.component.GlassTopBar
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.domain.model.AccountType

@Composable
fun AccountsScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    Column(modifier = modifier.fillMaxSize()) {
        GlassTopBar(title = { Text(stringResource(R.string.accounts_title)) })
        when {
            state.isLoading -> Unit

            state.isEmpty -> EmptyContent(
                icon = JeIcons.account(AccountType.CASH),
                title = stringResource(R.string.empty_accounts_title),
                message = stringResource(R.string.empty_accounts_message),
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = spacing.large),
            ) {
                items(state.accounts, key = { it.id }) { account ->
                    ListItem(
                        headlineContent = { Text(account.name) },
                        leadingContent = {
                            CategoryIcon(icon = account.icon, contentDescription = null)
                        },
                        trailingContent = {
                            AmountText(
                                amountMinor = account.balanceMinor,
                                currencyCode = state.currencyCode,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
