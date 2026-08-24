package com.jerries.expense.domain.repository

import com.jerries.expense.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observeAll(): Flow<List<Budget>>

    suspend fun upsert(budget: Budget)

    suspend fun deleteById(id: String)
}
