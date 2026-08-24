package com.jerries.expense.feature.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BudgetsUiState(
    val isLoading: Boolean = true,
    val currencyCode: String = "USD",
)

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    observeUserPreferences: ObserveUserPreferencesUseCase,
) : ViewModel() {

    val uiState: StateFlow<BudgetsUiState> = observeUserPreferences()
        .map { prefs -> BudgetsUiState(isLoading = false, currencyCode = prefs.currencyCode) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = BudgetsUiState(),
        )
}
