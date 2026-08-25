package com.jerries.expense.data.repository

import com.jerries.expense.domain.repository.AccountRepository
import com.jerries.expense.domain.repository.BudgetRepository
import com.jerries.expense.domain.repository.CategoryRepository
import com.jerries.expense.domain.repository.RecurringTransactionRepository
import com.jerries.expense.domain.repository.SavingsGoalRepository
import com.jerries.expense.domain.repository.TransactionRepository
import com.jerries.expense.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: OfflineFirstTransactionRepository,
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        impl: OfflineFirstAccountRepository,
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: OfflineFirstCategoryRepository,
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(impl: OfflineFirstBudgetRepository): BudgetRepository

    @Binds
    @Singleton
    abstract fun bindSavingsGoalRepository(
        impl: OfflineFirstSavingsGoalRepository,
    ): SavingsGoalRepository

    @Binds
    @Singleton
    abstract fun bindRecurringTransactionRepository(
        impl: OfflineFirstRecurringTransactionRepository,
    ): RecurringTransactionRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserDataPreferencesRepository,
    ): UserPreferencesRepository
}
