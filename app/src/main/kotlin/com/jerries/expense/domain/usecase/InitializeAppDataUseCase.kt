package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.model.AccountType
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.CategoryKind
import com.jerries.expense.domain.repository.UserPreferencesRepository
import com.jerries.expense.domain.repository.AccountRepository
import com.jerries.expense.domain.repository.CategoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class InitializeAppDataUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke() {
        val prefs = userPreferencesRepository.preferences.first()
        if (prefs.seeded) return

        DEFAULT_CATEGORIES.forEach { categoryRepository.upsert(it) }
        DEFAULT_ACCOUNTS.forEach { accountRepository.upsert(it) }
        userPreferencesRepository.markSeeded()
    }

    companion object {
        private fun id(seed: Int) = String.format("seed-%03d", seed)

        val DEFAULT_CATEGORIES: List<Category> = listOf(
            Category(id(1), "Groceries", CategoryKind.EXPENSE, "shopping_cart", 0xFF2F5DA8, isDefault = true, isArchived = false),
            Category(id(2), "Dining", CategoryKind.EXPENSE, "restaurant", 0xFFBA1A1A, isDefault = true, isArchived = false),
            Category(id(3), "Transport", CategoryKind.EXPENSE, "directions_bus", 0xFF00696D, isDefault = true, isArchived = false),
            Category(id(4), "Housing", CategoryKind.EXPENSE, "home", 0xFF7A5900, isDefault = true, isArchived = false),
            Category(id(5), "Utilities", CategoryKind.EXPENSE, "bolt", 0xFF5F6368, isDefault = true, isArchived = false),
            Category(id(6), "Entertainment", CategoryKind.EXPENSE, "movie", 0xFF7B1FA2, isDefault = true, isArchived = false),
            Category(id(7), "Health", CategoryKind.EXPENSE, "health_and_safety", 0xFF388E3C, isDefault = true, isArchived = false),
            Category(id(8), "Shopping", CategoryKind.EXPENSE, "storefront", 0xFF0288D1, isDefault = true, isArchived = false),
            Category(id(9), "Salary", CategoryKind.INCOME, "payments", 0xFF1E8E3E, isDefault = true, isArchived = false),
            Category(id(10), "Freelance", CategoryKind.INCOME, "work", 0xFF0B57D0, isDefault = true, isArchived = false),
            Category(id(11), "Gifts", CategoryKind.INCOME, "redeem", 0xFFF9AB00, isDefault = true, isArchived = false),
            Category(id(12), "Other Income", CategoryKind.INCOME, "attach_money", 0xFF5F6368, isDefault = true, isArchived = false),
        )

        val DEFAULT_ACCOUNTS: List<Account> = listOf(
            Account(
                id = id(101),
                name = "Cash Wallet",
                type = AccountType.CASH,
                initialBalanceMinor = 0L,
                currencyCode = "USD",
                colorArgb = 0xFF1E8E3E,
                archived = false,
                createdAtEpochMillis = System.currentTimeMillis(),
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
            Account(
                id = id(102),
                name = "Bank Account",
                type = AccountType.BANK,
                initialBalanceMinor = 0L,
                currencyCode = "USD",
                colorArgb = 0xFF0B57D0,
                archived = false,
                createdAtEpochMillis = System.currentTimeMillis(),
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }
}
