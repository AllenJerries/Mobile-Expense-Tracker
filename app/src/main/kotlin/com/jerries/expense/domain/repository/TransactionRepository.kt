package com.jerries.expense.domain.repository

import com.jerries.expense.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeAll(): Flow<List<Transaction>>

    fun observeRecent(limit: Int): Flow<List<Transaction>>

    fun observeByDateRange(startEpochDay: Long, endEpochDay: Long): Flow<List<Transaction>>

    suspend fun getById(id: String): Transaction?

    suspend fun add(transaction: Transaction)

    suspend fun addTransfer(
        sourceTransaction: Transaction,
        destinationTransaction: Transaction,
    )

    suspend fun update(transaction: Transaction)

    suspend fun deleteById(id: String)

    suspend fun softDeleteById(id: String)

    suspend fun getDueRecurringTransactions(todayEpochDay: Long): List<Transaction>
}
