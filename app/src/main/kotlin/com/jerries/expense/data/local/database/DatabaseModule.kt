package com.jerries.expense.data.local.database

import android.content.Context
import androidx.room.Room
import com.jerries.expense.data.local.dao.AccountDao
import com.jerries.expense.data.local.dao.BudgetDao
import com.jerries.expense.data.local.dao.CategoryDao
import com.jerries.expense.data.local.dao.RecurringTransactionDao
import com.jerries.expense.data.local.dao.SavingsGoalDao
import com.jerries.expense.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ExpenseDatabase =
        Room.databaseBuilder(context, ExpenseDatabase::class.java, ExpenseDatabase.DATABASE_NAME)
            .addMigrations(DatabaseMigrations.MIGRATION_1_2)
            .build()

    @Provides
    fun provideAccountDao(database: ExpenseDatabase): AccountDao = database.accountDao()

    @Provides
    fun provideCategoryDao(database: ExpenseDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideTransactionDao(database: ExpenseDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideBudgetDao(database: ExpenseDatabase): BudgetDao = database.budgetDao()

    @Provides
    fun provideSavingsGoalDao(database: ExpenseDatabase): SavingsGoalDao = database.savingsGoalDao()

    @Provides
    fun provideRecurringTransactionDao(database: ExpenseDatabase): RecurringTransactionDao =
        database.recurringTransactionDao()
}
