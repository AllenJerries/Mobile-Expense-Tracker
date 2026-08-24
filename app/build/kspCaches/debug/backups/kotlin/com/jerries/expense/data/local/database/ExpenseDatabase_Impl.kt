package com.jerries.expense.`data`.local.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.jerries.expense.`data`.local.dao.AccountDao
import com.jerries.expense.`data`.local.dao.AccountDao_Impl
import com.jerries.expense.`data`.local.dao.BudgetDao
import com.jerries.expense.`data`.local.dao.BudgetDao_Impl
import com.jerries.expense.`data`.local.dao.CategoryDao
import com.jerries.expense.`data`.local.dao.CategoryDao_Impl
import com.jerries.expense.`data`.local.dao.GoalDao
import com.jerries.expense.`data`.local.dao.GoalDao_Impl
import com.jerries.expense.`data`.local.dao.TransactionDao
import com.jerries.expense.`data`.local.dao.TransactionDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ExpenseDatabase_Impl : ExpenseDatabase() {
  private val _accountDao: Lazy<AccountDao> = lazy {
    AccountDao_Impl(this)
  }

  private val _categoryDao: Lazy<CategoryDao> = lazy {
    CategoryDao_Impl(this)
  }

  private val _transactionDao: Lazy<TransactionDao> = lazy {
    TransactionDao_Impl(this)
  }

  private val _budgetDao: Lazy<BudgetDao> = lazy {
    BudgetDao_Impl(this)
  }

  private val _goalDao: Lazy<GoalDao> = lazy {
    GoalDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "addcf290c41d98b3cf76f77a019c3bec", "618ccb5624b614d4c056233e8cf7b143") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `accounts` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `initial_balance_minor` INTEGER NOT NULL, `currency_code` TEXT NOT NULL, `color_argb` INTEGER NOT NULL, `archived` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_accounts_name` ON `accounts` (`name`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `kind` TEXT NOT NULL, `icon_key` TEXT, `color_argb` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_categories_name_kind` ON `categories` (`name`, `kind`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` TEXT NOT NULL, `account_id` TEXT NOT NULL, `category_id` TEXT, `amount_minor` INTEGER NOT NULL, `type` TEXT NOT NULL, `date_epoch_day` INTEGER NOT NULL, `note` TEXT, `created_at_epoch_millis` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`account_id`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_account_id` ON `transactions` (`account_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_category_id` ON `transactions` (`category_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_date_epoch_day` ON `transactions` (`date_epoch_day`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`id` TEXT NOT NULL, `category_id` TEXT NOT NULL, `limit_minor` INTEGER NOT NULL, `start_epoch_day` INTEGER NOT NULL, `end_epoch_day` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_budgets_category_id` ON `budgets` (`category_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `goals` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `target_minor` INTEGER NOT NULL, `saved_minor` INTEGER NOT NULL, `deadline_epoch_day` INTEGER, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'addcf290c41d98b3cf76f77a019c3bec')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `accounts`")
        connection.execSQL("DROP TABLE IF EXISTS `categories`")
        connection.execSQL("DROP TABLE IF EXISTS `transactions`")
        connection.execSQL("DROP TABLE IF EXISTS `budgets`")
        connection.execSQL("DROP TABLE IF EXISTS `goals`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsAccounts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAccounts.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("initial_balance_minor", TableInfo.Column("initial_balance_minor",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("currency_code", TableInfo.Column("currency_code", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("color_argb", TableInfo.Column("color_argb", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("archived", TableInfo.Column("archived", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAccounts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAccounts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesAccounts.add(TableInfo.Index("index_accounts_name", true, listOf("name"),
            listOf("ASC")))
        val _infoAccounts: TableInfo = TableInfo("accounts", _columnsAccounts, _foreignKeysAccounts,
            _indicesAccounts)
        val _existingAccounts: TableInfo = read(connection, "accounts")
        if (!_infoAccounts.equals(_existingAccounts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |accounts(com.jerries.expense.data.local.entity.AccountEntity).
              | Expected:
              |""".trimMargin() + _infoAccounts + """
              |
              | Found:
              |""".trimMargin() + _existingAccounts)
        }
        val _columnsCategories: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCategories.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("kind", TableInfo.Column("kind", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("icon_key", TableInfo.Column("icon_key", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("color_argb", TableInfo.Column("color_argb", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCategories: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCategories: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCategories.add(TableInfo.Index("index_categories_name_kind", true, listOf("name",
            "kind"), listOf("ASC", "ASC")))
        val _infoCategories: TableInfo = TableInfo("categories", _columnsCategories,
            _foreignKeysCategories, _indicesCategories)
        val _existingCategories: TableInfo = read(connection, "categories")
        if (!_infoCategories.equals(_existingCategories)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |categories(com.jerries.expense.data.local.entity.CategoryEntity).
              | Expected:
              |""".trimMargin() + _infoCategories + """
              |
              | Found:
              |""".trimMargin() + _existingCategories)
        }
        val _columnsTransactions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTransactions.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("account_id", TableInfo.Column("account_id", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("category_id", TableInfo.Column("category_id", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("amount_minor", TableInfo.Column("amount_minor", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("date_epoch_day", TableInfo.Column("date_epoch_day", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("note", TableInfo.Column("note", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("created_at_epoch_millis",
            TableInfo.Column("created_at_epoch_millis", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTransactions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysTransactions.add(TableInfo.ForeignKey("accounts", "RESTRICT", "NO ACTION",
            listOf("account_id"), listOf("id")))
        _foreignKeysTransactions.add(TableInfo.ForeignKey("categories", "SET NULL", "NO ACTION",
            listOf("category_id"), listOf("id")))
        val _indicesTransactions: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTransactions.add(TableInfo.Index("index_transactions_account_id", false,
            listOf("account_id"), listOf("ASC")))
        _indicesTransactions.add(TableInfo.Index("index_transactions_category_id", false,
            listOf("category_id"), listOf("ASC")))
        _indicesTransactions.add(TableInfo.Index("index_transactions_date_epoch_day", false,
            listOf("date_epoch_day"), listOf("ASC")))
        val _infoTransactions: TableInfo = TableInfo("transactions", _columnsTransactions,
            _foreignKeysTransactions, _indicesTransactions)
        val _existingTransactions: TableInfo = read(connection, "transactions")
        if (!_infoTransactions.equals(_existingTransactions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |transactions(com.jerries.expense.data.local.entity.TransactionEntity).
              | Expected:
              |""".trimMargin() + _infoTransactions + """
              |
              | Found:
              |""".trimMargin() + _existingTransactions)
        }
        val _columnsBudgets: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBudgets.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBudgets.put("category_id", TableInfo.Column("category_id", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBudgets.put("limit_minor", TableInfo.Column("limit_minor", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBudgets.put("start_epoch_day", TableInfo.Column("start_epoch_day", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBudgets.put("end_epoch_day", TableInfo.Column("end_epoch_day", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBudgets: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysBudgets.add(TableInfo.ForeignKey("categories", "CASCADE", "NO ACTION",
            listOf("category_id"), listOf("id")))
        val _indicesBudgets: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBudgets.add(TableInfo.Index("index_budgets_category_id", false,
            listOf("category_id"), listOf("ASC")))
        val _infoBudgets: TableInfo = TableInfo("budgets", _columnsBudgets, _foreignKeysBudgets,
            _indicesBudgets)
        val _existingBudgets: TableInfo = read(connection, "budgets")
        if (!_infoBudgets.equals(_existingBudgets)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |budgets(com.jerries.expense.data.local.entity.BudgetEntity).
              | Expected:
              |""".trimMargin() + _infoBudgets + """
              |
              | Found:
              |""".trimMargin() + _existingBudgets)
        }
        val _columnsGoals: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsGoals.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGoals.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGoals.put("target_minor", TableInfo.Column("target_minor", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGoals.put("saved_minor", TableInfo.Column("saved_minor", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGoals.put("deadline_epoch_day", TableInfo.Column("deadline_epoch_day", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysGoals: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesGoals: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoGoals: TableInfo = TableInfo("goals", _columnsGoals, _foreignKeysGoals,
            _indicesGoals)
        val _existingGoals: TableInfo = read(connection, "goals")
        if (!_infoGoals.equals(_existingGoals)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |goals(com.jerries.expense.data.local.entity.GoalEntity).
              | Expected:
              |""".trimMargin() + _infoGoals + """
              |
              | Found:
              |""".trimMargin() + _existingGoals)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "accounts", "categories",
        "transactions", "budgets", "goals")
  }

  public override fun clearAllTables() {
    super.performClear(true, "accounts", "categories", "transactions", "budgets", "goals")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(AccountDao::class, AccountDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CategoryDao::class, CategoryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TransactionDao::class, TransactionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BudgetDao::class, BudgetDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(GoalDao::class, GoalDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun accountDao(): AccountDao = _accountDao.value

  public override fun categoryDao(): CategoryDao = _categoryDao.value

  public override fun transactionDao(): TransactionDao = _transactionDao.value

  public override fun budgetDao(): BudgetDao = _budgetDao.value

  public override fun goalDao(): GoalDao = _goalDao.value
}
