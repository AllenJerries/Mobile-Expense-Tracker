package com.jerries.expense.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.jerries.expense.`data`.local.entity.TransactionEntity
import javax.`annotation`.processing.Generated
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
public class TransactionDao_Impl(
  __db: RoomDatabase,
) : TransactionDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfTransactionEntity: EntityUpsertAdapter<TransactionEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfTransactionEntity = EntityUpsertAdapter<TransactionEntity>(object :
        EntityInsertAdapter<TransactionEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `transactions` (`id`,`account_id`,`category_id`,`amount_minor`,`type`,`date_epoch_day`,`note`,`created_at_epoch_millis`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TransactionEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.accountId)
        val _tmpCategoryId: String? = entity.categoryId
        if (_tmpCategoryId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpCategoryId)
        }
        statement.bindLong(4, entity.amountMinor)
        statement.bindText(5, entity.type)
        statement.bindLong(6, entity.dateEpochDay)
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpNote)
        }
        statement.bindLong(8, entity.createdAtEpochMillis)
      }
    }, object : EntityDeleteOrUpdateAdapter<TransactionEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `transactions` SET `id` = ?,`account_id` = ?,`category_id` = ?,`amount_minor` = ?,`type` = ?,`date_epoch_day` = ?,`note` = ?,`created_at_epoch_millis` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: TransactionEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.accountId)
        val _tmpCategoryId: String? = entity.categoryId
        if (_tmpCategoryId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpCategoryId)
        }
        statement.bindLong(4, entity.amountMinor)
        statement.bindText(5, entity.type)
        statement.bindLong(6, entity.dateEpochDay)
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpNote)
        }
        statement.bindLong(8, entity.createdAtEpochMillis)
        statement.bindText(9, entity.id)
      }
    })
  }

  public override suspend fun upsert(transaction: TransactionEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfTransactionEntity.upsert(_connection, transaction)
  }

  public override fun observeAll(): Flow<List<TransactionEntity>> {
    val _sql: String =
        "SELECT * FROM transactions ORDER BY date_epoch_day DESC, created_at_epoch_millis DESC"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAccountId: Int = getColumnIndexOrThrow(_stmt, "account_id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "category_id")
        val _columnIndexOfAmountMinor: Int = getColumnIndexOrThrow(_stmt, "amount_minor")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDateEpochDay: Int = getColumnIndexOrThrow(_stmt, "date_epoch_day")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "created_at_epoch_millis")
        val _result: MutableList<TransactionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAccountId: String
          _tmpAccountId = _stmt.getText(_columnIndexOfAccountId)
          val _tmpCategoryId: String?
          if (_stmt.isNull(_columnIndexOfCategoryId)) {
            _tmpCategoryId = null
          } else {
            _tmpCategoryId = _stmt.getText(_columnIndexOfCategoryId)
          }
          val _tmpAmountMinor: Long
          _tmpAmountMinor = _stmt.getLong(_columnIndexOfAmountMinor)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDateEpochDay: Long
          _tmpDateEpochDay = _stmt.getLong(_columnIndexOfDateEpochDay)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          _item =
              TransactionEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpAmountMinor,_tmpType,_tmpDateEpochDay,_tmpNote,_tmpCreatedAtEpochMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeRecent(limit: Int): Flow<List<TransactionEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM transactions
        |        ORDER BY date_epoch_day DESC, created_at_epoch_millis DESC
        |        LIMIT ?
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAccountId: Int = getColumnIndexOrThrow(_stmt, "account_id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "category_id")
        val _columnIndexOfAmountMinor: Int = getColumnIndexOrThrow(_stmt, "amount_minor")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDateEpochDay: Int = getColumnIndexOrThrow(_stmt, "date_epoch_day")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "created_at_epoch_millis")
        val _result: MutableList<TransactionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAccountId: String
          _tmpAccountId = _stmt.getText(_columnIndexOfAccountId)
          val _tmpCategoryId: String?
          if (_stmt.isNull(_columnIndexOfCategoryId)) {
            _tmpCategoryId = null
          } else {
            _tmpCategoryId = _stmt.getText(_columnIndexOfCategoryId)
          }
          val _tmpAmountMinor: Long
          _tmpAmountMinor = _stmt.getLong(_columnIndexOfAmountMinor)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDateEpochDay: Long
          _tmpDateEpochDay = _stmt.getLong(_columnIndexOfDateEpochDay)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          _item =
              TransactionEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpAmountMinor,_tmpType,_tmpDateEpochDay,_tmpNote,_tmpCreatedAtEpochMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: String): TransactionEntity? {
    val _sql: String = "SELECT * FROM transactions WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAccountId: Int = getColumnIndexOrThrow(_stmt, "account_id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "category_id")
        val _columnIndexOfAmountMinor: Int = getColumnIndexOrThrow(_stmt, "amount_minor")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDateEpochDay: Int = getColumnIndexOrThrow(_stmt, "date_epoch_day")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "created_at_epoch_millis")
        val _result: TransactionEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAccountId: String
          _tmpAccountId = _stmt.getText(_columnIndexOfAccountId)
          val _tmpCategoryId: String?
          if (_stmt.isNull(_columnIndexOfCategoryId)) {
            _tmpCategoryId = null
          } else {
            _tmpCategoryId = _stmt.getText(_columnIndexOfCategoryId)
          }
          val _tmpAmountMinor: Long
          _tmpAmountMinor = _stmt.getLong(_columnIndexOfAmountMinor)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDateEpochDay: Long
          _tmpDateEpochDay = _stmt.getLong(_columnIndexOfDateEpochDay)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          _result =
              TransactionEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpAmountMinor,_tmpType,_tmpDateEpochDay,_tmpNote,_tmpCreatedAtEpochMillis)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM transactions"
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

  public override fun observeNetMovementSum(): Flow<Long> {
    val _sql: String = """
        |
        |        SELECT COALESCE(SUM(
        |            CASE
        |                WHEN type = 'INCOME' THEN amount_minor
        |                WHEN type = 'EXPENSE' THEN -amount_minor
        |                ELSE 0
        |            END
        |        ), 0)
        |        FROM transactions
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
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

  public override fun observeNetMovementByAccount(): Flow<List<AccountMovementProjection>> {
    val _sql: String = """
        |
        |        SELECT account_id AS accountId, COALESCE(SUM(
        |            CASE
        |                WHEN type = 'INCOME' THEN amount_minor
        |                WHEN type = 'EXPENSE' THEN -amount_minor
        |                ELSE 0
        |            END
        |        ), 0) AS netMovementMinor
        |        FROM transactions
        |        GROUP BY account_id
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfAccountId: Int = 0
        val _columnIndexOfNetMovementMinor: Int = 1
        val _result: MutableList<AccountMovementProjection> = mutableListOf()
        while (_stmt.step()) {
          val _item: AccountMovementProjection
          val _tmpAccountId: String
          _tmpAccountId = _stmt.getText(_columnIndexOfAccountId)
          val _tmpNetMovementMinor: Long
          _tmpNetMovementMinor = _stmt.getLong(_columnIndexOfNetMovementMinor)
          _item = AccountMovementProjection(_tmpAccountId,_tmpNetMovementMinor)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM transactions WHERE id = ?"
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
