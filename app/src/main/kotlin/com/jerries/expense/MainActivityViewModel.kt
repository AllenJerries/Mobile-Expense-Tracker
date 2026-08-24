package com.jerries.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MainUiState(
    val isLoading: Boolean = true,
    val preferences: AppPreferences = AppPreferences(),
)

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    observeUserPreferences: ObserveUserPreferencesUseCase,
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = observeUserPreferences()
        .map { prefs -> MainUiState(isLoading = false, preferences = prefs) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MainUiState(),
        )
}
