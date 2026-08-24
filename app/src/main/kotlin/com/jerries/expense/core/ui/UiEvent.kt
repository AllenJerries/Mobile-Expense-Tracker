package com.jerries.expense.core.ui

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * One-shot navigation / messaging events emitted by ViewModels and consumed
 * exactly once by the UI (e.g. navigate up after a successful save).
 */
interface UiEvent

/**
 * Small helper that owns a [MutableSharedFlow] of [UiEvent]s with
 * replay-free, extra-buffered delivery semantics.
 */
class UiEventChannel {

    private val _events = MutableSharedFlow<UiEvent>(
        replay = 0,
        extraBufferCapacity = 8,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    fun send(event: UiEvent) {
        _events.tryEmit(event)
    }
}
