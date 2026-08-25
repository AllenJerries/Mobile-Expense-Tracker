package com.jerries.expense.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.jerries.expense.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets ORDER BY start_epoch_day DESC")
    fun observeAll(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getById(id: String): BudgetEntity?

    @Query(
        """
        SELECT * FROM budgets
        WHERE (category_id = :categoryId OR :categoryId IS NULL)
        AND start_epoch_day <= :epochDay AND end_epoch_day >= :epochDay
        """,
    )
    fun observeActive(epochDay: Long, categoryId: String? = null): Flow<List<BudgetEntity>>

    @Upsert
    suspend fun upsert(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM budgets")
    suspend fun count(): Int
}
