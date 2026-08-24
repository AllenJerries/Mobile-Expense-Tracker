package com.jerries.expense.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.model.ThemeSetting
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import com.jerries.expense.domain.usecase.SetCurrencyCodeUseCase
import com.jerries.expense.domain.usecase.SetDynamicColorsUseCase
import com.jerries.expense.domain.usecase.SetThemeSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = true,
    val preferences: AppPreferences = AppPreferences(),
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val setThemeSetting: SetThemeSettingUseCase,
    private val setDynamicColors: SetDynamicColorsUseCase,
    private val setCurrencyCode: SetCurrencyCodeUseCase,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = observeUserPreferences()
        .map { prefs -> SettingsUiState(isLoading = false, preferences = prefs) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingsUiState(),
        )

    fun onThemeChange(theme: ThemeSetting) {
        viewModelScope.launch { setThemeSetting(theme) }
    }

    fun onDynamicColorsChange(enabled: Boolean) {
        viewModelScope.launch { setDynamicColors(enabled) }
    }

    fun onCurrencyChange(code: String) {
        viewModelScope.launch { setCurrencyCode(code) }
    }
}
