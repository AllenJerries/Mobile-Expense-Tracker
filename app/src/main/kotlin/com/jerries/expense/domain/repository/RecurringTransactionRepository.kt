package com.jerries.expense.domain.repository

import com.jerries.expense.domain.model.RecurringTransaction
import kotlinx.coroutines.flow.Flow

interface RecurringTransactionRepository {
    fun observeAll(): Flow<List<RecurringTransaction>>

    fun observeActive(): Flow<List<RecurringTransaction>>

    suspend fun getById(id: String): RecurringTransaction?

    suspend fun getDueOccurrences(todayEpochDay: Long): List<RecurringTransaction>

    suspend fun upsert(recurring: RecurringTransaction)

    suspend fun updateNextOccurrence(id: String, nextOccurrenceEpochDay: Long)

    suspend fun deleteById(id: String)

    suspend fun deactivate(id: String)
}
