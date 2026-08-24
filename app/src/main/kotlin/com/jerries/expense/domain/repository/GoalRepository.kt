package com.jerries.expense.domain.repository

import com.jerries.expense.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun observeAll(): Flow<List<Goal>>

    suspend fun upsert(goal: Goal)

    suspend fun deleteById(id: String)
}
