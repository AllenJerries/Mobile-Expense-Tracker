package com.jerries.expense.core.common

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Abstraction over time so domain and view-model logic remain testable.
 * Production code always uses the system clock in the device default zone.
 */
interface TimeProvider {
    fun nowMillis(): Long
    fun today(): LocalDate
    fun zone(): ZoneId
}

@Singleton
class SystemTimeProvider @Inject constructor() : TimeProvider {

    override fun nowMillis(): Long = System.currentTimeMillis()

    override fun today(): LocalDate = LocalDate.now()

    override fun zone(): ZoneId = ZoneId.systemDefault()

    fun clock(): Clock = Clock.systemDefaultZone()
}
