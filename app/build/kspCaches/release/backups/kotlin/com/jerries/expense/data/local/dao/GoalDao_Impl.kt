package com.jerries.expense.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.jerries.expense.`data`.local.entity.GoalEntity
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
public class GoalDao_Impl(
  __db: RoomDatabase,
) : GoalDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfGoalEntity: EntityUpsertAdapter<GoalEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfGoalEntity = EntityUpsertAdapter<GoalEntity>(object :
        EntityInsertAdapter<GoalEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `goals` (`id`,`name`,`target_minor`,`saved_minor`,`deadline_epoch_day`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: GoalEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.targetMinor)
        statement.bindLong(4, entity.savedMinor)
        val _tmpDeadlineEpochDay: Long? = entity.deadlineEpochDay
        if (_tmpDeadlineEpochDay == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpDeadlineEpochDay)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<GoalEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `goals` SET `id` = ?,`name` = ?,`target_minor` = ?,`saved_minor` = ?,`deadline_epoch_day` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: GoalEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.targetMinor)
        statement.bindLong(4, entity.savedMinor)
        val _tmpDeadlineEpochDay: Long? = entity.deadlineEpochDay
        if (_tmpDeadlineEpochDay == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpDeadlineEpochDay)
        }
        statement.bindText(6, entity.id)
      }
    })
  }

  public override suspend fun upsert(goal: GoalEntity): Unit = performSuspending(__db, false, true)
      { _connection ->
    __upsertAdapterOfGoalEntity.upsert(_connection, goal)
  }

  public override fun observeAll(): Flow<List<GoalEntity>> {
    val _sql: String = "SELECT * FROM goals ORDER BY deadline_epoch_day IS NULL, deadline_epoch_day"
    return createFlow(__db, false, arrayOf("goals")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfTargetMinor: Int = getColumnIndexOrThrow(_stmt, "target_minor")
        val _columnIndexOfSavedMinor: Int = getColumnIndexOrThrow(_stmt, "saved_minor")
        val _columnIndexOfDeadlineEpochDay: Int = getColumnIndexOrThrow(_stmt, "deadline_epoch_day")
        val _result: MutableList<GoalEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GoalEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpTargetMinor: Long
          _tmpTargetMinor = _stmt.getLong(_columnIndexOfTargetMinor)
          val _tmpSavedMinor: Long
          _tmpSavedMinor = _stmt.getLong(_columnIndexOfSavedMinor)
          val _tmpDeadlineEpochDay: Long?
          if (_stmt.isNull(_columnIndexOfDeadlineEpochDay)) {
            _tmpDeadlineEpochDay = null
          } else {
            _tmpDeadlineEpochDay = _stmt.getLong(_columnIndexOfDeadlineEpochDay)
          }
          _item = GoalEntity(_tmpId,_tmpName,_tmpTargetMinor,_tmpSavedMinor,_tmpDeadlineEpochDay)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: String): GoalEntity? {
    val _sql: String = "SELECT * FROM goals WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfTargetMinor: Int = getColumnIndexOrThrow(_stmt, "target_minor")
        val _columnIndexOfSavedMinor: Int = getColumnIndexOrThrow(_stmt, "saved_minor")
        val _columnIndexOfDeadlineEpochDay: Int = getColumnIndexOrThrow(_stmt, "deadline_epoch_day")
        val _result: GoalEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpTargetMinor: Long
          _tmpTargetMinor = _stmt.getLong(_columnIndexOfTargetMinor)
          val _tmpSavedMinor: Long
          _tmpSavedMinor = _stmt.getLong(_columnIndexOfSavedMinor)
          val _tmpDeadlineEpochDay: Long?
          if (_stmt.isNull(_columnIndexOfDeadlineEpochDay)) {
            _tmpDeadlineEpochDay = null
          } else {
            _tmpDeadlineEpochDay = _stmt.getLong(_columnIndexOfDeadlineEpochDay)
          }
          _result = GoalEntity(_tmpId,_tmpName,_tmpTargetMinor,_tmpSavedMinor,_tmpDeadlineEpochDay)
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
    val _sql: String = "DELETE FROM goals WHERE id = ?"
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
