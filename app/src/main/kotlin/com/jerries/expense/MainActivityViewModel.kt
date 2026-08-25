package com.jerries.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.security.AuthState
import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.repository.UserPreferencesRepository
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoading: Boolean = true,
    val preferences: AppPreferences = AppPreferences(),
    val authState: AuthState = AuthState.UNLOCKED,
)

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val observeUserPreferences: ObserveUserPreferencesUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private var backgroundTimestamp: Long = 0L

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        viewModelScope.launch {
            observeUserPreferences().collect { prefs ->
                val needsLock = prefs.pinEnabled && prefs.pinHash != null
                val currentAuth = _uiState.value.authState
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        preferences = prefs,
                        authState = if (needsLock && currentAuth != AuthState.UNLOCKED) {
                            AuthState.LOCKED
                        } else if (!needsLock) {
                            AuthState.UNLOCKED
                        } else {
                            currentAuth
                        },
                    )
                }
            }
        }
    }

    fun onAppForeground() {
        if (backgroundTimestamp == 0L) return
        val elapsed = System.currentTimeMillis() - backgroundTimestamp
        backgroundTimestamp = 0L
        val prefs = _uiState.value.preferences
        if (prefs.pinEnabled && prefs.pinHash != null) {
            val timeoutMinutes = prefs.autoLockTimeoutMinutes
            if (timeoutMinutes == 0) return
            val timeoutMillis = timeoutMinutes * 60_000L
            if (elapsed >= timeoutMillis) {
                lock()
            }
        }
    }

    fun onAppBackground() {
        backgroundTimestamp = System.currentTimeMillis()
    }

    fun unlock() {
        _uiState.update { it.copy(authState = AuthState.UNLOCKED) }
    }

    fun lock() {
        viewModelScope.launch {
            val prefs = userPreferencesRepository.preferences.first()
            if (prefs.pinEnabled && prefs.pinHash != null) {
                _uiState.update { it.copy(authState = AuthState.LOCKED) }
            }
        }
    }
}
