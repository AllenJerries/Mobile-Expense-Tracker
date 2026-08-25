package com.jerries.expense.data.repository

import com.jerries.expense.data.local.dao.SavingsGoalDao
import com.jerries.expense.domain.model.SavingsGoal
import com.jerries.expense.domain.repository.SavingsGoalRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class OfflineFirstSavingsGoalRepository @Inject constructor(
    private val savingsGoalDao: SavingsGoalDao,
) : SavingsGoalRepository {

    override fun observeAll(): Flow<List<SavingsGoal>> =
        savingsGoalDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeActive(): Flow<List<SavingsGoal>> =
        savingsGoalDao.observeActive().map { list -> list.map { it.toDomain() } }

    override fun observeCompleted(): Flow<List<SavingsGoal>> =
        savingsGoalDao.observeCompleted().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): SavingsGoal? =
        savingsGoalDao.getById(id)?.toDomain()

    override suspend fun upsert(goal: SavingsGoal) {
        savingsGoalDao.upsert(goal.toEntity())
    }

    override suspend fun deleteById(id: String) {
        savingsGoalDao.deleteById(id)
    }
}
