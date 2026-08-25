package com.jerries.expense.domain.repository

import com.jerries.expense.domain.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

interface SavingsGoalRepository {
    fun observeAll(): Flow<List<SavingsGoal>>

    fun observeActive(): Flow<List<SavingsGoal>>

    fun observeCompleted(): Flow<List<SavingsGoal>>

    suspend fun getById(id: String): SavingsGoal?

    suspend fun upsert(goal: SavingsGoal)

    suspend fun deleteById(id: String)
}
