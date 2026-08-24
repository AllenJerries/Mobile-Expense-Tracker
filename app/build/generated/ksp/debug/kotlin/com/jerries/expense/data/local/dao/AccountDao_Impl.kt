package com.jerries.expense.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.jerries.expense.`data`.local.entity.AccountEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AccountDao_Impl(
  __db: RoomDatabase,
) : AccountDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfAccountEntity: EntityUpsertAdapter<AccountEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfAccountEntity = EntityUpsertAdapter<AccountEntity>(object :
        EntityInsertAdapter<AccountEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `accounts` (`id`,`name`,`type`,`initial_balance_minor`,`currency_code`,`color_argb`,`archived`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AccountEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.type)
        statement.bindLong(4, entity.initialBalanceMinor)
        statement.bindText(5, entity.currencyCode)
        statement.bindLong(6, entity.colorArgb)
        val _tmp: Int = if (entity.archived) 1 else 0
        statement.bindLong(7, _tmp.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<AccountEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `accounts` SET `id` = ?,`name` = ?,`type` = ?,`initial_balance_minor` = ?,`currency_code` = ?,`color_argb` = ?,`archived` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: AccountEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.type)
        statement.bindLong(4, entity.initialBalanceMinor)
        statement.bindText(5, entity.currencyCode)
        statement.bindLong(6, entity.colorArgb)
        val _tmp: Int = if (entity.archived) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindText(8, entity.id)
      }
    })
  }

  public override suspend fun upsert(account: AccountEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfAccountEntity.upsert(_connection, account)
  }

  public override fun observeAll(): Flow<List<AccountEntity>> {
    val _sql: String = "SELECT * FROM accounts WHERE archived = 0 ORDER BY name"
    return createFlow(__db, false, arrayOf("accounts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfInitialBalanceMinor: Int = getColumnIndexOrThrow(_stmt,
            "initial_balance_minor")
        val _columnIndexOfCurrencyCode: Int = getColumnIndexOrThrow(_stmt, "currency_code")
        val _columnIndexOfColorArgb: Int = getColumnIndexOrThrow(_stmt, "color_argb")
        val _columnIndexOfArchived: Int = getColumnIndexOrThrow(_stmt, "archived")
        val _result: MutableList<AccountEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AccountEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpInitialBalanceMinor: Long
          _tmpInitialBalanceMinor = _stmt.getLong(_columnIndexOfInitialBalanceMinor)
          val _tmpCurrencyCode: String
          _tmpCurrencyCode = _stmt.getText(_columnIndexOfCurrencyCode)
          val _tmpColorArgb: Long
          _tmpColorArgb = _stmt.getLong(_columnIndexOfColorArgb)
          val _tmpArchived: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArchived).toInt()
          _tmpArchived = _tmp != 0
          _item =
              AccountEntity(_tmpId,_tmpName,_tmpType,_tmpInitialBalanceMinor,_tmpCurrencyCode,_tmpColorArgb,_tmpArchived)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllIncludingArchived(): Flow<List<AccountEntity>> {
    val _sql: String = "SELECT * FROM accounts ORDER BY name"
    return createFlow(__db, false, arrayOf("accounts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfInitialBalanceMinor: Int = getColumnIndexOrThrow(_stmt,
            "initial_balance_minor")
        val _columnIndexOfCurrencyCode: Int = getColumnIndexOrThrow(_stmt, "currency_code")
        val _columnIndexOfColorArgb: Int = getColumnIndexOrThrow(_stmt, "color_argb")
        val _columnIndexOfArchived: Int = getColumnIndexOrThrow(_stmt, "archived")
        val _result: MutableList<AccountEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AccountEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpInitialBalanceMinor: Long
          _tmpInitialBalanceMinor = _stmt.getLong(_columnIndexOfInitialBalanceMinor)
          val _tmpCurrencyCode: String
          _tmpCurrencyCode = _stmt.getText(_columnIndexOfCurrencyCode)
          val _tmpColorArgb: Long
          _tmpColorArgb = _stmt.getLong(_columnIndexOfColorArgb)
          val _tmpArchived: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArchived).toInt()
          _tmpArchived = _tmp != 0
          _item =
              AccountEntity(_tmpId,_tmpName,_tmpType,_tmpInitialBalanceMinor,_tmpCurrencyCode,_tmpColorArgb,_tmpArchived)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: String): AccountEntity? {
    val _sql: String = "SELECT * FROM accounts WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfInitialBalanceMinor: Int = getColumnIndexOrThrow(_stmt,
            "initial_balance_minor")
        val _columnIndexOfCurrencyCode: Int = getColumnIndexOrThrow(_stmt, "currency_code")
        val _columnIndexOfColorArgb: Int = getColumnIndexOrThrow(_stmt, "color_argb")
        val _columnIndexOfArchived: Int = getColumnIndexOrThrow(_stmt, "archived")
        val _result: AccountEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpInitialBalanceMinor: Long
          _tmpInitialBalanceMinor = _stmt.getLong(_columnIndexOfInitialBalanceMinor)
          val _tmpCurrencyCode: String
          _tmpCurrencyCode = _stmt.getText(_columnIndexOfCurrencyCode)
          val _tmpColorArgb: Long
          _tmpColorArgb = _stmt.getLong(_columnIndexOfColorArgb)
          val _tmpArchived: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArchived).toInt()
          _tmpArchived = _tmp != 0
          _result =
              AccountEntity(_tmpId,_tmpName,_tmpType,_tmpInitialBalanceMinor,_tmpCurrencyCode,_tmpColorArgb,_tmpArchived)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeInitialBalanceSum(): Flow<Long> {
    val _sql: String =
        "SELECT COALESCE(SUM(initial_balance_minor), 0) FROM accounts WHERE archived = 0"
    return createFlow(__db, false, arrayOf("accounts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Long
        if (_stmt.step()) {
          val _tmp: Long
          _tmp = _stmt.getLong(0)
          _result = _tmp
        } else {
          _result = 0L
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM accounts"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM accounts WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
