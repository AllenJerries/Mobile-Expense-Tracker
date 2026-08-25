package com.jerries.expense.domain.repository

import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.model.AccountBalance
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun observeAll(): Flow<List<Account>>

    fun observeAllIncludingArchived(): Flow<List<Account>>

    fun observeBalances(): Flow<List<AccountBalance>>

    fun observeTotalBalanceMinor(): Flow<Long>

    suspend fun getById(id: String): Account?

    suspend fun upsert(account: Account)

    suspend fun deleteById(id: String)
}
