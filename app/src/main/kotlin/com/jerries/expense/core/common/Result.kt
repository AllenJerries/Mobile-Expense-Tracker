package com.jerries.expense.core.common

/**
 * A lightweight functional result type used across layers to surface success and
 * failure states without relying on exceptions for control flow.
 */
sealed interface Result<out T> {

    data class Success<T>(val data: T) : Result<T>

    data class Failure(val error: AppError) : Result<Nothing>
}

/** Domain-level error taxonomy shared by all layers. */
sealed interface AppError {

    data object NotFound : AppError

    data class Validation(val message: String) : AppError

    data class Storage(val cause: Throwable? = null) : AppError

    data class Unexpected(val cause: Throwable? = null) : AppError
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Failure -> this
}

inline fun <T> Result<T>.onSuccess(block: (T) -> Unit): Result<T> {
    if (this is Result.Success) block(data)
    return this
}

inline fun <T> Result<T>.onFailure(block: (AppError) -> Unit): Result<T> {
    if (this is Result.Failure) block(error)
    return this
}

fun <T> Result<T>.getOrNull(): T? = (this as? Result.Success)?.data

/**
 * Runs [block] catching unexpected exceptions and converting them into a [Result].
 * Cancellation is always rethrown so coroutine scopes shut down correctly.
 */
inline fun <T> runCatchingResult(block: () -> T): Result<T> = try {
    Result.Success(block())
} catch (cancellation: kotlinx.coroutines.CancellationException) {
    throw cancellation
} catch (throwable: Throwable) {
    Result.Failure(AppError.Unexpected(throwable))
}
