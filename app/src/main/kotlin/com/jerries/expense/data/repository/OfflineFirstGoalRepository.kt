package com.jerries.expense.data.repository

import com.jerries.expense.data.local.dao.GoalDao
import com.jerries.expense.domain.model.Goal
import com.jerries.expense.domain.repository.GoalRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class OfflineFirstGoalRepository @Inject constructor(
    private val goalDao: GoalDao,
) : GoalRepository {

    override fun observeAll(): Flow<List<Goal>> =
        goalDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun upsert(goal: Goal) {
        goalDao.upsert(goal.toEntity())
    }

    override suspend fun deleteById(id: String) {
        goalDao.deleteById(id)
    }
}
