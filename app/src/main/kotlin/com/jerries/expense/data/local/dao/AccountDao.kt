package com.jerries.expense.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.jerries.expense.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts WHERE archived = 0 ORDER BY name")
    fun observeAll(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts ORDER BY name")
    fun observeAllIncludingArchived(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: String): AccountEntity?

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun observeById(id: String): Flow<AccountEntity?>

    @Query("SELECT COALESCE(SUM(initial_balance_minor), 0) FROM accounts WHERE archived = 0")
    fun observeInitialBalanceSum(): Flow<Long>

    @Upsert
    suspend fun upsert(account: AccountEntity)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun count(): Int

    @Query("SELECT * FROM accounts WHERE archived = 1 ORDER BY name")
    fun observeArchived(): Flow<List<AccountEntity>>

    @Query("DELETE FROM accounts")
    suspend fun deleteAll()
}
