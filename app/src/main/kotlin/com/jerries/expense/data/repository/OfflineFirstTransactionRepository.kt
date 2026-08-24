package com.jerries.expense.data.repository

import com.jerries.expense.data.local.dao.TransactionDao
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.repository.TransactionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Offline-first transaction repository. Room is the single source of truth;
 * UI observes reactive queries and every write goes straight to local storage.
 */
@Singleton
class OfflineFirstTransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
) : TransactionRepository {

    override fun observeAll(): Flow<List<Transaction>> =
        transactionDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeRecent(limit: Int): Flow<List<Transaction>> =
        transactionDao.observeRecent(limit).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Transaction? =
        transactionDao.getById(id)?.toDomain()

    override suspend fun add(transaction: Transaction) {
        transactionDao.upsert(transaction.toEntity())
    }

    override suspend fun deleteById(id: String) {
        transactionDao.deleteById(id)
    }
}
