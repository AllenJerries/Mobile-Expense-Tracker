package com.jerries.expense.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.jerries.expense.`data`.local.entity.CategoryEntity
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
public class CategoryDao_Impl(
  __db: RoomDatabase,
) : CategoryDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfCategoryEntity: EntityUpsertAdapter<CategoryEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfCategoryEntity = EntityUpsertAdapter<CategoryEntity>(object :
        EntityInsertAdapter<CategoryEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `categories` (`id`,`name`,`kind`,`icon_key`,`color_argb`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CategoryEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.kind)
        val _tmpIconKey: String? = entity.iconKey
        if (_tmpIconKey == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpIconKey)
        }
        statement.bindLong(5, entity.colorArgb)
      }
    }, object : EntityDeleteOrUpdateAdapter<CategoryEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `categories` SET `id` = ?,`name` = ?,`kind` = ?,`icon_key` = ?,`color_argb` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: CategoryEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.kind)
        val _tmpIconKey: String? = entity.iconKey
        if (_tmpIconKey == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpIconKey)
        }
        statement.bindLong(5, entity.colorArgb)
        statement.bindText(6, entity.id)
      }
    })
  }

  public override suspend fun upsert(category: CategoryEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfCategoryEntity.upsert(_connection, category)
  }

  public override fun observeAll(): Flow<List<CategoryEntity>> {
    val _sql: String = "SELECT * FROM categories ORDER BY name"
    return createFlow(__db, false, arrayOf("categories")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfIconKey: Int = getColumnIndexOrThrow(_stmt, "icon_key")
        val _columnIndexOfColorArgb: Int = getColumnIndexOrThrow(_stmt, "color_argb")
        val _result: MutableList<CategoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CategoryEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpKind: String
          _tmpKind = _stmt.getText(_columnIndexOfKind)
          val _tmpIconKey: String?
          if (_stmt.isNull(_columnIndexOfIconKey)) {
            _tmpIconKey = null
          } else {
            _tmpIconKey = _stmt.getText(_columnIndexOfIconKey)
          }
          val _tmpColorArgb: Long
          _tmpColorArgb = _stmt.getLong(_columnIndexOfColorArgb)
          _item = CategoryEntity(_tmpId,_tmpName,_tmpKind,_tmpIconKey,_tmpColorArgb)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByKind(kind: String): Flow<List<CategoryEntity>> {
    val _sql: String = "SELECT * FROM categories WHERE kind = ? ORDER BY name"
    return createFlow(__db, false, arrayOf("categories")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, kind)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfIconKey: Int = getColumnIndexOrThrow(_stmt, "icon_key")
        val _columnIndexOfColorArgb: Int = getColumnIndexOrThrow(_stmt, "color_argb")
        val _result: MutableList<CategoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CategoryEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpKind: String
          _tmpKind = _stmt.getText(_columnIndexOfKind)
          val _tmpIconKey: String?
          if (_stmt.isNull(_columnIndexOfIconKey)) {
            _tmpIconKey = null
          } else {
            _tmpIconKey = _stmt.getText(_columnIndexOfIconKey)
          }
          val _tmpColorArgb: Long
          _tmpColorArgb = _stmt.getLong(_columnIndexOfColorArgb)
          _item = CategoryEntity(_tmpId,_tmpName,_tmpKind,_tmpIconKey,_tmpColorArgb)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: String): CategoryEntity? {
    val _sql: String = "SELECT * FROM categories WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfIconKey: Int = getColumnIndexOrThrow(_stmt, "icon_key")
        val _columnIndexOfColorArgb: Int = getColumnIndexOrThrow(_stmt, "color_argb")
        val _result: CategoryEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpKind: String
          _tmpKind = _stmt.getText(_columnIndexOfKind)
          val _tmpIconKey: String?
          if (_stmt.isNull(_columnIndexOfIconKey)) {
            _tmpIconKey = null
          } else {
            _tmpIconKey = _stmt.getText(_columnIndexOfIconKey)
          }
          val _tmpColorArgb: Long
          _tmpColorArgb = _stmt.getLong(_columnIndexOfColorArgb)
          _result = CategoryEntity(_tmpId,_tmpName,_tmpKind,_tmpIconKey,_tmpColorArgb)
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
    val _sql: String = "DELETE FROM categories WHERE id = ?"
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
