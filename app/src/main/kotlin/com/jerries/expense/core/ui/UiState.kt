package com.jerries.expense.core.ui

/**
 * Generic immutable UI state for screens with an asynchronous load.
 * ViewModels expose `StateFlow<UiState<T>>`; composables map each case
 * onto the shared loading / error / empty / content components.
 */
sealed interface UiState<out T> {

    data object Loading : UiState<Nothing>

    data class Empty(val message: String? = null) : UiState<Nothing>

    data class Error(val message: String? = null) : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>
}

fun <T> UiState<T>.dataOrNull(): T? = (this as? UiState.Success)?.data
