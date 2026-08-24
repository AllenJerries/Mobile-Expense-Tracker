package com.jerries.expense.feature.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.model.AccountBalance
import com.jerries.expense.domain.usecase.ObserveAccountBalancesUseCase
import com.jerries.expense.domain.usecase.ObserveAccountsUseCase
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AccountRow(
    val id: String,
    val name: String,
    val balanceMinor: Long,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

data class AccountsUiState(
    val isLoading: Boolean = true,
    val currencyCode: String = "USD",
    val accounts: List<AccountRow> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && accounts.isEmpty()
}

@HiltViewModel
class AccountsViewModel @Inject constructor(
    observeAccounts: ObserveAccountsUseCase,
    observeBalancesUseCase: ObserveAccountBalancesUseCase,
    observeUserPreferences: ObserveUserPreferencesUseCase,
) : ViewModel() {

    val uiState: StateFlow<AccountsUiState> =
        combine(
            observeAccounts(),
            observeBalancesUseCase(),
            observeUserPreferences(),
        ) { accounts, balances, prefs ->
            val balanceByAccount = balances.associate { it.accountId to it.balanceMinor }
            AccountsUiState(
                isLoading = false,
                currencyCode = prefs.currencyCode,
                accounts = accounts.map { account ->
                    AccountRow(
                        id = account.id,
                        name = account.name,
                        balanceMinor = balanceByAccount[account.id] ?: 0L,
                        icon = JeIcons.account(account.type),
                    )
                },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AccountsUiState(),
        )
}
