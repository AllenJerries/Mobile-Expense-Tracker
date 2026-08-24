package com.jerries.expense.core.util

import java.math.BigDecimal
import java.math.RoundingMode

/** Converts a user-entered decimal string into minor units (cents). */
fun String.toMinorUnitsOrNull(): Long? = runCatching {
    BigDecimal(this.trim())
        .setScale(2, RoundingMode.HALF_UP)
        .movePointRight(2)
        .longValueExact()
}.getOrNull()

/** Converts minor units (cents) back into a plain decimal string. */
fun Long.toMajorString(): String =
    BigDecimal(this).movePointLeft(2).toPlainString()
