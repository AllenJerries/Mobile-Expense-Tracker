package com.jerries.expense.feature.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.security.PinHasher
import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SecurityUiState(
    val isLoading: Boolean = true,
    val preferences: AppPreferences = AppPreferences(),
)

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<SecurityUiState> = userPreferencesRepository.preferences
        .map { prefs -> SecurityUiState(isLoading = false, preferences = prefs) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SecurityUiState(),
        )

    fun onPinEnabledChange(enabled: Boolean) {
        viewModelScope.launch {
            if (!enabled) {
                userPreferencesRepository.setPinEnabled(false)
                userPreferencesRepository.setPinHash(null)
                userPreferencesRepository.setBiometricEnabled(false)
            }
        }
    }

    fun onBiometricEnabledChange(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setBiometricEnabled(enabled)
        }
    }

    fun onAutoLockTimeoutChange(minutes: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setAutoLockTimeout(minutes)
        }
    }

    fun onHideSensitiveInfoChange(hide: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setHideSensitiveInfo(hide)
        }
    }
}
