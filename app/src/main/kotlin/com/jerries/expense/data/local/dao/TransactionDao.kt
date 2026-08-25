package com.jerries.expense.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.jerries.expense.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

data class AccountMovementProjection(
    val accountId: String,
    val netMovementMinor: Long,
)

data class DailyTotalProjection(
    @ColumnInfo(name = "date_epoch_day") val dateEpochDay: Long,
    @ColumnInfo(name = "expense_minor") val expenseMinor: Long,
    @ColumnInfo(name = "income_minor") val incomeMinor: Long,
)

data class SpendingByCategoryProjection(
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "total_minor") val totalMinor: Long,
    @ColumnInfo(name = "color_argb") val colorArgb: Long,
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE is_deleted = 0 ORDER BY date_epoch_day DESC, created_at_epoch_millis DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions
        WHERE is_deleted = 0
        ORDER BY date_epoch_day DESC, created_at_epoch_millis DESC
        LIMIT :limit
        """,
    )
    fun observeRecent(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): TransactionEntity?

    @Query(
        """
        SELECT * FROM transactions
        WHERE date_epoch_day >= :startEpochDay AND date_epoch_day <= :endEpochDay
        AND is_deleted = 0
        ORDER BY date_epoch_day DESC, created_at_epoch_millis DESC
        """,
    )
    fun observeByDateRange(startEpochDay: Long, endEpochDay: Long): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions
        WHERE date_epoch_day >= :startEpochDay AND date_epoch_day <= :endEpochDay
        AND is_deleted = 0
        ORDER BY date_epoch_day DESC, created_at_epoch_millis DESC
        """,
    )
    suspend fun getByDateRange(startEpochDay: Long, endEpochDay: Long): List<TransactionEntity>

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
        WHERE is_deleted = 0
        """,
    )
    fun observeNetMovementSum(): Flow<Long>

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
        WHERE is_deleted = 0
        GROUP BY account_id
        """,
    )
    fun observeNetMovementByAccount(): Flow<List<AccountMovementProjection>>

    @Query(
        """
        SELECT account_id AS accountId, COALESCE(SUM(
            CASE
                WHEN type = 'INCOME' THEN amount_minor
                WHEN type = 'EXPENSE' THEN -amount_minor
                WHEN type = 'TRANSFER' THEN -amount_minor
                ELSE 0
            END
        ), 0) AS netMovementMinor
        FROM transactions
        WHERE is_deleted = 0
        GROUP BY account_id
        """,
    )
    fun observeMovementByAccountWithTransfers(): Flow<List<AccountMovementProjection>>

    @Query(
        """
        SELECT destination_account_id AS accountId, COALESCE(SUM(amount_minor), 0) AS netMovementMinor
        FROM transactions
        WHERE type = 'TRANSFER' AND destination_account_id IS NOT NULL AND is_deleted = 0
        GROUP BY destination_account_id
        """,
    )
    fun observeTransferInflows(): Flow<List<AccountMovementProjection>>

    @Query("SELECT COUNT(*) FROM transactions WHERE is_deleted = 0")
    suspend fun count(): Int

    @Query(
        """
        SELECT date_epoch_day,
            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount_minor ELSE 0 END), 0) AS expense_minor,
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount_minor ELSE 0 END), 0) AS income_minor
        FROM transactions
        WHERE date_epoch_day >= :startEpochDay AND date_epoch_day <= :endEpochDay AND is_deleted = 0
        GROUP BY date_epoch_day
        ORDER BY date_epoch_day ASC
        """,
    )
    fun observeDailyTotals(startEpochDay: Long, endEpochDay: Long): Flow<List<DailyTotalProjection>>

    @Query(
        """
        SELECT COALESCE(SUM(amount_minor), 0)
        FROM transactions
        WHERE category_id = :categoryId AND type = 'EXPENSE'
        AND date_epoch_day >= :startEpochDay AND date_epoch_day <= :endEpochDay
        AND is_deleted = 0
        """,
    )
    fun observeSpendingForBudget(
        categoryId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(amount_minor), 0)
        FROM transactions
        WHERE account_id = :accountId AND type = 'EXPENSE'
        AND date_epoch_day >= :startEpochDay AND date_epoch_day <= :endEpochDay
        AND is_deleted = 0
        """,
    )
    fun observeSpendingForBudgetByAccount(
        accountId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<Long>

    @Query(
        """
        SELECT t.category_id AS category_id,
            c.name AS category_name,
            COALESCE(SUM(t.amount_minor), 0) AS total_minor,
            COALESCE(c.color_argb, 0) AS color_argb
        FROM transactions t
        LEFT JOIN categories c ON t.category_id = c.id
        WHERE t.type = :type
        AND t.date_epoch_day >= :startEpochDay AND t.date_epoch_day <= :endEpochDay
        AND t.is_deleted = 0
        GROUP BY t.category_id
        ORDER BY total_minor DESC
        """,
    )
    fun observeByCategory(
        type: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<List<SpendingByCategoryProjection>>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount_minor ELSE 0 END), 0)
        FROM transactions
        WHERE date_epoch_day >= :startEpochDay AND date_epoch_day <= :endEpochDay AND is_deleted = 0
        """,
    )
    fun observeIncomeTotal(startEpochDay: Long, endEpochDay: Long): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount_minor ELSE 0 END), 0)
        FROM transactions
        WHERE date_epoch_day >= :startEpochDay AND date_epoch_day <= :endEpochDay AND is_deleted = 0
        """,
    )
    fun observeExpenseTotal(startEpochDay: Long, endEpochDay: Long): Flow<Long>

    @Transaction
    suspend fun insertTransaction(transaction: TransactionEntity) {
        upsert(transaction)
    }

    @Transaction
    suspend fun insertTransfer(
        sourceTransaction: TransactionEntity,
        destinationTransaction: TransactionEntity,
    ) {
        upsert(sourceTransaction)
        upsert(destinationTransaction)
    }

    @Transaction
    suspend fun deleteTransactionSoft(id: String) {
        val existing = getById(id) ?: return
        upsert(
            existing.copy(
                isDeleted = true,
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }

    @Transaction
    suspend fun updateTransaction(transaction: TransactionEntity) {
        val existing = getById(transaction.id) ?: return
        upsert(
            transaction.copy(
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }

    @Upsert
    suspend fun upsert(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}
