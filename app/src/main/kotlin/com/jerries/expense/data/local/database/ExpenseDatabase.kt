package com.jerries.expense.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jerries.expense.data.local.converters.Converters
import com.jerries.expense.data.local.dao.AccountDao
import com.jerries.expense.data.local.dao.BudgetDao
import com.jerries.expense.data.local.dao.CategoryDao
import com.jerries.expense.data.local.dao.GoalDao
import com.jerries.expense.data.local.dao.TransactionDao
import com.jerries.expense.data.local.entity.AccountEntity
import com.jerries.expense.data.local.entity.BudgetEntity
import com.jerries.expense.data.local.entity.CategoryEntity
import com.jerries.expense.data.local.entity.GoalEntity
import com.jerries.expense.data.local.entity.TransactionEntity

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        GoalEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao

    abstract fun budgetDao(): BudgetDao

    abstract fun goalDao(): GoalDao

    companion object {
        const val DATABASE_NAME = "jerries-expense.db"
    }
}
