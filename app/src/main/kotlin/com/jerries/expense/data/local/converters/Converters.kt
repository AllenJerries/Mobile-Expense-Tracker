package com.jerries.expense.data.local.converters

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * Room type converters. Dates are stored as ISO-8601 strings for readability;
 * timestamps remain raw epoch values inside entities.
 */
class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toEpochDay()?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? =
        value?.toLongOrNull()?.let(LocalDate::ofEpochDay)
}
