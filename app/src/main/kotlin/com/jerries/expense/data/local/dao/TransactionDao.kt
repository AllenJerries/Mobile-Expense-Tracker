package com.jerries.expense.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.jerries.expense.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/** Signed transaction sum for a single account. */
data class AccountMovementProjection(
    val accountId: String,
    val netMovementMinor: Long,
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date_epoch_day DESC, created_at_epoch_millis DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions
        ORDER BY date_epoch_day DESC, created_at_epoch_millis DESC
        LIMIT :limit
        """,
    )
    fun observeRecent(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): TransactionEntity?

    @Upsert
    suspend fun upsert(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun count(): Int

    /** Net signed movement across all accounts (incomes add, expenses subtract). */
    @Query(
        """
        SELECT COALESCE(SUM(
            CASE
                WHEN type = 'INCOME' THEN amount_minor
                WHEN type = 'EXPENSE' THEN -amount_minor
                ELSE 0
            END
        ), 0)
        FROM transactions
        """,
    )
    fun observeNetMovementSum(): Flow<Long>

    /** Net movement grouped per account. */
    @Query(
        """
        SELECT account_id AS accountId, COALESCE(SUM(
            CASE
                WHEN type = 'INCOME' THEN amount_minor
                WHEN type = 'EXPENSE' THEN -amount_minor
                ELSE 0
            END
        ), 0) AS netMovementMinor
        FROM transactions
        GROUP BY account_id
        """,
    )
    fun observeNetMovementByAccount(): Flow<List<AccountMovementProjection>>
}
