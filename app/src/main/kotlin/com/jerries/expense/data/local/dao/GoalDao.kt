package com.jerries.expense.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.jerries.expense.data.local.entity.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsGoalDao {

    @Query("SELECT * FROM goals ORDER BY deadline_epoch_day IS NULL, deadline_epoch_day")
    fun observeAll(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM goals WHERE completed = 0 ORDER BY deadline_epoch_day IS NULL, deadline_epoch_day")
    fun observeActive(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM goals WHERE completed = 1 ORDER BY deadline_epoch_day DESC")
    fun observeCompleted(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getById(id: String): SavingsGoalEntity?

    @Upsert
    suspend fun upsert(goal: SavingsGoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM goals")
    suspend fun count(): Int

    @Query("DELETE FROM goals")
    suspend fun deleteAll()
}
