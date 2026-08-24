package com.jerries.expense.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.jerries.expense.`data`.local.entity.BudgetEntity
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
public class BudgetDao_Impl(
  __db: RoomDatabase,
) : BudgetDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfBudgetEntity: EntityUpsertAdapter<BudgetEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfBudgetEntity = EntityUpsertAdapter<BudgetEntity>(object :
        EntityInsertAdapter<BudgetEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `budgets` (`id`,`category_id`,`limit_minor`,`start_epoch_day`,`end_epoch_day`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BudgetEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.categoryId)
        statement.bindLong(3, entity.limitMinor)
        statement.bindLong(4, entity.startEpochDay)
        statement.bindLong(5, entity.endEpochDay)
      }
    }, object : EntityDeleteOrUpdateAdapter<BudgetEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `budgets` SET `id` = ?,`category_id` = ?,`limit_minor` = ?,`start_epoch_day` = ?,`end_epoch_day` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: BudgetEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.categoryId)
        statement.bindLong(3, entity.limitMinor)
        statement.bindLong(4, entity.startEpochDay)
        statement.bindLong(5, entity.endEpochDay)
        statement.bindText(6, entity.id)
      }
    })
  }

  public override suspend fun upsert(budget: BudgetEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfBudgetEntity.upsert(_connection, budget)
  }

  public override fun observeAll(): Flow<List<BudgetEntity>> {
    val _sql: String = "SELECT * FROM budgets ORDER BY start_epoch_day DESC"
    return createFlow(__db, false, arrayOf("budgets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "category_id")
        val _columnIndexOfLimitMinor: Int = getColumnIndexOrThrow(_stmt, "limit_minor")
        val _columnIndexOfStartEpochDay: Int = getColumnIndexOrThrow(_stmt, "start_epoch_day")
        val _columnIndexOfEndEpochDay: Int = getColumnIndexOrThrow(_stmt, "end_epoch_day")
        val _result: MutableList<BudgetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BudgetEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCategoryId: String
          _tmpCategoryId = _stmt.getText(_columnIndexOfCategoryId)
          val _tmpLimitMinor: Long
          _tmpLimitMinor = _stmt.getLong(_columnIndexOfLimitMinor)
          val _tmpStartEpochDay: Long
          _tmpStartEpochDay = _stmt.getLong(_columnIndexOfStartEpochDay)
          val _tmpEndEpochDay: Long
          _tmpEndEpochDay = _stmt.getLong(_columnIndexOfEndEpochDay)
          _item =
              BudgetEntity(_tmpId,_tmpCategoryId,_tmpLimitMinor,_tmpStartEpochDay,_tmpEndEpochDay)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: String): BudgetEntity? {
    val _sql: String = "SELECT * FROM budgets WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "category_id")
        val _columnIndexOfLimitMinor: Int = getColumnIndexOrThrow(_stmt, "limit_minor")
        val _columnIndexOfStartEpochDay: Int = getColumnIndexOrThrow(_stmt, "start_epoch_day")
        val _columnIndexOfEndEpochDay: Int = getColumnIndexOrThrow(_stmt, "end_epoch_day")
        val _result: BudgetEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCategoryId: String
          _tmpCategoryId = _stmt.getText(_columnIndexOfCategoryId)
          val _tmpLimitMinor: Long
          _tmpLimitMinor = _stmt.getLong(_columnIndexOfLimitMinor)
          val _tmpStartEpochDay: Long
          _tmpStartEpochDay = _stmt.getLong(_columnIndexOfStartEpochDay)
          val _tmpEndEpochDay: Long
          _tmpEndEpochDay = _stmt.getLong(_columnIndexOfEndEpochDay)
          _result =
              BudgetEntity(_tmpId,_tmpCategoryId,_tmpLimitMinor,_tmpStartEpochDay,_tmpEndEpochDay)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM budgets WHERE id = ?"
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
