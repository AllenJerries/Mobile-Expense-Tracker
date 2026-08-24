package com.jerries.expense.core.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Formats monetary values expressed in minor units (cents) using the given
 * ISO-4217 currency code. Instances are created per call, which keeps them
 * thread-safe without synchronization.
 */
object CurrencyFormatter {

    fun formatMinorUnits(amountMinor: Long, currencyCode: String): String {
        val major = BigDecimal(amountMinor).movePointLeft(2)
        return format(major.toDouble(), currencyCode)
    }

    fun format(amountMajor: Double, currencyCode: String): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        runCatching {
            format.currency = Currency.getInstance(currencyCode)
        }.onFailure {
            format.currency = Currency.getInstance("USD")
        }
        return format.format(amountMajor)
    }
}
