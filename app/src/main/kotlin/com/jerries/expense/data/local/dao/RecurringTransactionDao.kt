package com.jerries.expense.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.jerries.expense.data.local.entity.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringTransactionDao {

    @Query("SELECT * FROM recurring_transactions ORDER BY next_occurrence_epoch_day ASC")
    fun observeAll(): Flow<List<RecurringTransactionEntity>>

    @Query("SELECT * FROM recurring_transactions WHERE active = 1 ORDER BY next_occurrence_epoch_day ASC")
    fun observeActive(): Flow<List<RecurringTransactionEntity>>

    @Query("SELECT * FROM recurring_transactions WHERE id = :id")
    suspend fun getById(id: String): RecurringTransactionEntity?

    @Query(
        """
        SELECT * FROM recurring_transactions
        WHERE active = 1 AND next_occurrence_epoch_day <= :epochDay
        ORDER BY next_occurrence_epoch_day ASC
        """,
    )
    suspend fun getDueOccurrences(epochDay: Long): List<RecurringTransactionEntity>

    @Query("SELECT * FROM recurring_transactions WHERE active = 1 ORDER BY next_occurrence_epoch_day ASC")
    fun observeDue(): Flow<List<RecurringTransactionEntity>>

    @Upsert
    suspend fun upsert(recurring: RecurringTransactionEntity)

    @Query("DELETE FROM recurring_transactions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM recurring_transactions WHERE active = 1")
    suspend fun countActive(): Int
}
