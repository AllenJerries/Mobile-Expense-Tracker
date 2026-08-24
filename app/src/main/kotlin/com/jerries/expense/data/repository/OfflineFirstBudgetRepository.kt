package com.jerries.expense.data.repository

import com.jerries.expense.data.local.dao.BudgetDao
import com.jerries.expense.domain.model.Budget
import com.jerries.expense.domain.repository.BudgetRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class OfflineFirstBudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao,
) : BudgetRepository {

    override fun observeAll(): Flow<List<Budget>> =
        budgetDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun upsert(budget: Budget) {
        budgetDao.upsert(budget.toEntity())
    }

    override suspend fun deleteById(id: String) {
        budgetDao.deleteById(id)
    }
}
