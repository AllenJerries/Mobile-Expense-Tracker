package com.jerries.expense.core.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** Human-friendly date formatting helpers shared by all features. */
object DateFormatter {

    private val mediumDate: DateTimeFormatter =
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    fun format(date: LocalDate): String = date.format(mediumDate)

    /** Returns "Today", "Yesterday" or a localized medium date. */
    fun formatRelative(date: LocalDate, today: LocalDate): String = when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> format(date)
    }
}
