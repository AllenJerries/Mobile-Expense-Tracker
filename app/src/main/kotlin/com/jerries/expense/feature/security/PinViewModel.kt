package com.jerries.expense.feature.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.security.AuthState
import com.jerries.expense.core.security.PinHasher
import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PinUiState(
    val authState: AuthState = AuthState.UNLOCKED,
    val pin: String = "",
    val confirmPin: String = "",
    val isConfirmStep: Boolean = false,
    val error: String? = null,
    val attempts: Int = 0,
    val isLockedOut: Boolean = false,
)

@HiltViewModel
class PinViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinUiState())
    val uiState: StateFlow<PinUiState> = _uiState.asStateFlow()

    val preferences: StateFlow<AppPreferences> = userPreferencesRepository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AppPreferences(),
        )

    init {
        viewModelScope.launch {
            val prefs = userPreferencesRepository.preferences.first()
            val state = when {
                !prefs.pinEnabled -> AuthState.UNLOCKED
                prefs.pinHash == null -> AuthState.SETUP
                else -> AuthState.LOCKED
            }
            _uiState.update { it.copy(authState = state) }
        }
    }

    fun onPinDigitEntered(digit: String) {
        val current = _uiState.value
        if (current.isLockedOut) return
        if (current.pin.length >= PIN_LENGTH) return

        val newPin = current.pin + digit
        _uiState.update { it.copy(pin = newPin, error = null) }

        if (newPin.length == PIN_LENGTH) {
            if (current.authState == AuthState.SETUP && !current.isConfirmStep) {
                _uiState.update {
                    it.copy(
                        confirmPin = newPin,
                        pin = "",
                        isConfirmStep = true,
                    )
                }
            } else if (current.authState == AuthState.SETUP && current.isConfirmStep) {
                verifySetupPin(newPin)
            } else if (current.authState == AuthState.LOCKED) {
                verifyPin(newPin)
            }
        }
    }

    fun onPinBackspace() {
        val current = _uiState.value
        if (current.pin.isNotEmpty()) {
            _uiState.update { it.copy(pin = it.pin.dropLast(1), error = null) }
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(error = null) }
    }

    fun unlock() {
        _uiState.update { it.copy(authState = AuthState.UNLOCKED) }
    }

    fun resetForLock() {
        _uiState.update {
            PinUiState(authState = AuthState.LOCKED)
        }
    }

    private fun verifyPin(pin: String) {
        viewModelScope.launch {
            val prefs = userPreferencesRepository.preferences.first()
            val storedHash = prefs.pinHash
            if (storedHash != null && PinHasher.verify(pin, storedHash)) {
                _uiState.update {
                    it.copy(authState = AuthState.UNLOCKED, pin = "", error = null, attempts = 0)
                }
            } else {
                val newAttempts = _uiState.value.attempts + 1
                _uiState.update {
                    it.copy(
                        pin = "",
                        error = "Wrong PIN",
                        attempts = newAttempts,
                        isLockedOut = newAttempts >= MAX_ATTEMPTS,
                    )
                }
            }
        }
    }

    private fun verifySetupPin(pin: String) {
        viewModelScope.launch {
            val confirmPin = _uiState.value.confirmPin
            if (pin == confirmPin) {
                val hash = PinHasher.hash(pin)
                userPreferencesRepository.setPinHash(hash)
                userPreferencesRepository.setPinEnabled(true)
                _uiState.update {
                    it.copy(
                        authState = AuthState.UNLOCKED,
                        pin = "",
                        confirmPin = "",
                        isConfirmStep = false,
                        error = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        pin = "",
                        confirmPin = "",
                        isConfirmStep = false,
                        error = "PINs don't match",
                    )
                }
            }
        }
    }

    suspend fun disablePin() {
        userPreferencesRepository.setPinEnabled(false)
        userPreferencesRepository.setPinHash(null)
        _uiState.update { PinUiState(authState = AuthState.UNLOCKED) }
    }

    companion object {
        const val PIN_LENGTH = 4
        private const val MAX_ATTEMPTS = 5
    }
}
