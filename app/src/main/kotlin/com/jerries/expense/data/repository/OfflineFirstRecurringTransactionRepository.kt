package com.jerries.expense.data.repository

import com.jerries.expense.data.local.dao.RecurringTransactionDao
import com.jerries.expense.domain.model.RecurringTransaction
import com.jerries.expense.domain.repository.RecurringTransactionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class OfflineFirstRecurringTransactionRepository @Inject constructor(
    private val recurringTransactionDao: RecurringTransactionDao,
) : RecurringTransactionRepository {

    override fun observeAll(): Flow<List<RecurringTransaction>> =
        recurringTransactionDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeActive(): Flow<List<RecurringTransaction>> =
        recurringTransactionDao.observeActive().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): RecurringTransaction? =
        recurringTransactionDao.getById(id)?.toDomain()

    override suspend fun getDueOccurrences(todayEpochDay: Long): List<RecurringTransaction> =
        recurringTransactionDao.getDueOccurrences(todayEpochDay).map { it.toDomain() }

    override suspend fun upsert(recurring: RecurringTransaction) {
        recurringTransactionDao.upsert(recurring.toEntity())
    }

    override suspend fun updateNextOccurrence(id: String, nextOccurrenceEpochDay: Long) {
        val existing = recurringTransactionDao.getById(id) ?: return
        recurringTransactionDao.upsert(
            existing.copy(
                nextOccurrenceEpochDay = nextOccurrenceEpochDay,
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun deleteById(id: String) {
        recurringTransactionDao.deleteById(id)
    }

    override suspend fun deactivate(id: String) {
        val existing = recurringTransactionDao.getById(id) ?: return
        recurringTransactionDao.upsert(
            existing.copy(
                active = false,
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }
}
