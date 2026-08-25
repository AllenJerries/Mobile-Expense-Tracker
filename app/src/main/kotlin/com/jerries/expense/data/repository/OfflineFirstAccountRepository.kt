package com.jerries.expense.data.repository

import com.jerries.expense.data.local.dao.AccountDao
import com.jerries.expense.data.local.dao.TransactionDao
import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.model.AccountBalance
import com.jerries.expense.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Singleton
class OfflineFirstAccountRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
) : AccountRepository {

    override fun observeAll(): Flow<List<Account>> =
        accountDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeAllIncludingArchived(): Flow<List<Account>> =
        accountDao.observeAllIncludingArchived().map { list -> list.map { it.toDomain() } }

    override fun observeBalances(): Flow<List<AccountBalance>> =
        combine(
            accountDao.observeAll(),
            transactionDao.observeNetMovementByAccount(),
            transactionDao.observeTransferInflows(),
        ) { accounts, movements, inflows ->
            val movementByAccount = movements.associate { it.accountId to it.netMovementMinor }
            val inflowByAccount = inflows.associate { it.accountId to it.netMovementMinor }
            accounts.map { account ->
                AccountBalance(
                    accountId = account.id,
                    balanceMinor = account.initialBalanceMinor +
                        (movementByAccount[account.id] ?: 0L) +
                        (inflowByAccount[account.id] ?: 0L),
                )
            }
        }

    override fun observeTotalBalanceMinor(): Flow<Long> =
        combine(accountDao.observeInitialBalanceSum(), transactionDao.observeNetMovementSum()) {
            initial,
            movement,
            ->
            initial + movement
        }

    override suspend fun getById(id: String): Account? = accountDao.getById(id)?.toDomain()

    override suspend fun upsert(account: Account) {
        accountDao.upsert(account.toEntity())
    }

    override suspend fun deleteById(id: String) {
        accountDao.deleteById(id)
    }
}
