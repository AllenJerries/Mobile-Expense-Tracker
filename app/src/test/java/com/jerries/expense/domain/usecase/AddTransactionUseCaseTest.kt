package com.jerries.expense.domain.usecase

import com.jerries.expense.core.common.AppError
import com.jerries.expense.core.common.Result
import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.model.AccountType
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.CategoryKind
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.model.TransactionType
import com.jerries.expense.domain.repository.AccountRepository
import com.jerries.expense.domain.repository.CategoryRepository
import com.jerries.expense.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddTransactionUseCaseTest {

    private lateinit var useCase: AddTransactionUseCase
    private val transactionRepo = FakeTransactionRepository()
    private val accountRepo = FakeAccountRepository()
    private val categoryRepo = FakeCategoryRepository()

    @Before
    fun setup() {
        useCase = AddTransactionUseCase(transactionRepo, accountRepo, categoryRepo)
    }

    @Test
    fun `reject zero amount`() = runTest {
        val result = useCase(makeTransaction(amountMinor = 0))
        assertTrue(result is Result.Failure)
        assertEquals(AddTransactionUseCase.AMOUNT_MESSAGE, (result as Result.Failure).error.let {
            (it as? AppError.Validation)?.message
        })
    }

    @Test
    fun `reject negative amount`() = runTest {
        val result = useCase(makeTransaction(amountMinor = -100))
        assertTrue(result is Result.Failure)
    }

    @Test
    fun `reject unknown account`() = runTest {
        val result = useCase(makeTransaction(accountId = "nonexistent"))
        assertTrue(result is Result.Failure)
    }

    @Test
    fun `reject archived account`() = runTest {
        accountRepo.addAccount(makeAccount(id = "acc1", archived = true))
        val result = useCase(makeTransaction(accountId = "acc1"))
        assertTrue(result is Result.Failure)
    }

    @Test
    fun `reject missing category for expense`() = runTest {
        accountRepo.addAccount(makeAccount(id = "acc1"))
        val result = useCase(makeTransaction(accountId = "acc1", categoryId = null))
        assertTrue(result is Result.Failure)
    }

    @Test
    fun `reject archived category`() = runTest {
        accountRepo.addAccount(makeAccount(id = "acc1"))
        categoryRepo.addCategory(makeCategory(id = "cat1", isArchived = true))
        val result = useCase(makeTransaction(accountId = "acc1", categoryId = "cat1"))
        assertTrue(result is Result.Failure)
    }

    @Test
    fun `reject transfer to same account`() = runTest {
        accountRepo.addAccount(makeAccount(id = "acc1"))
        val result = useCase(
            makeTransferTransaction(
                accountId = "acc1",
                destinationAccountId = "acc1",
            ),
        )
        assertTrue(result is Result.Failure)
    }

    @Test
    fun `reject transfer to nonexistent destination`() = runTest {
        accountRepo.addAccount(makeAccount(id = "acc1"))
        val result = useCase(
            makeTransferTransaction(
                accountId = "acc1",
                destinationAccountId = "nonexistent",
            ),
        )
        assertTrue(result is Result.Failure)
    }

    @Test
    fun `accept valid expense`() = runTest {
        accountRepo.addAccount(makeAccount(id = "acc1"))
        categoryRepo.addCategory(makeCategory(id = "cat1"))
        val result = useCase(makeTransaction(accountId = "acc1", categoryId = "cat1"))
        assertTrue(result is Result.Success)
        assertEquals(1, transactionRepo.transactions.size)
    }

    @Test
    fun `accept valid transfer`() = runTest {
        accountRepo.addAccount(makeAccount(id = "acc1"))
        accountRepo.addAccount(makeAccount(id = "acc2"))
        val result = useCase(
            makeTransferTransaction(
                accountId = "acc1",
                destinationAccountId = "acc2",
            ),
        )
        assertTrue(result is Result.Success)
    }

    private fun makeTransaction(
        id: String = "tx1",
        accountId: String = "acc1",
        categoryId: String? = "cat1",
        amountMinor: Long = 1000,
        type: TransactionType = TransactionType.EXPENSE,
    ) = Transaction(
        id = id,
        accountId = accountId,
        categoryId = categoryId,
        amountMinor = amountMinor,
        type = type,
        dateEpochDay = 20000,
        title = null,
        note = "test",
        createdAtEpochMillis = 1000L,
        updatedAtEpochMillis = 1000L,
        paymentMethod = null,
        destinationAccountId = null,
        recurringTransactionId = null,
        attachmentUri = null,
        isDeleted = false,
    )

    private fun makeTransferTransaction(
        accountId: String,
        destinationAccountId: String,
    ) = Transaction(
        id = "tx1",
        accountId = accountId,
        categoryId = null,
        amountMinor = 1000,
        type = TransactionType.TRANSFER,
        dateEpochDay = 20000,
        title = null,
        note = "transfer",
        createdAtEpochMillis = 1000L,
        updatedAtEpochMillis = 1000L,
        paymentMethod = null,
        destinationAccountId = destinationAccountId,
        recurringTransactionId = null,
        attachmentUri = null,
        isDeleted = false,
    )

    private fun makeAccount(
        id: String,
        archived: Boolean = false,
    ) = Account(
        id = id,
        name = "Account $id",
        type = AccountType.BANK,
        initialBalanceMinor = 0,
        currencyCode = "USD",
        colorArgb = 0,
        archived = archived,
        createdAtEpochMillis = 0L,
        updatedAtEpochMillis = 0L,
    )

    private fun makeCategory(
        id: String,
        isArchived: Boolean = false,
    ) = Category(
        id = id,
        name = "Category $id",
        kind = CategoryKind.EXPENSE,
        iconKey = null,
        colorArgb = 0,
        isDefault = true,
        isArchived = isArchived,
    )
}

class FakeTransactionRepository : TransactionRepository {
    val transactions = mutableListOf<Transaction>()
    override fun observeAll() = flowOf(transactions.toList())
    override fun observeRecent(limit: Int) = flowOf(transactions.take(limit))
    override fun observeByDateRange(startEpochDay: Long, endEpochDay: Long) =
        flowOf(transactions.filter { it.dateEpochDay in startEpochDay..endEpochDay })
    override suspend fun getById(id: String) = transactions.find { it.id == id }
    override suspend fun add(transaction: Transaction) { transactions.add(transaction) }
    override suspend fun addTransfer(sourceTransaction: Transaction, destinationTransaction: Transaction) {
        transactions.add(sourceTransaction)
        transactions.add(destinationTransaction)
    }
    override suspend fun update(transaction: Transaction) {
        val idx = transactions.indexOfFirst { it.id == transaction.id }
        if (idx >= 0) transactions[idx] = transaction
    }
    override suspend fun deleteById(id: String) { transactions.removeAll { it.id == id } }
    override suspend fun softDeleteById(id: String) {
        val idx = transactions.indexOfFirst { it.id == id }
        if (idx >= 0) transactions[idx] = transactions[idx].copy(isDeleted = true)
    }
    override suspend fun getDueRecurringTransactions(todayEpochDay: Long) = emptyList<Transaction>()
    override fun observeSpendingForBudget(categoryId: String, startEpochDay: Long, endEpochDay: Long) = flowOf(0L)
    override fun observeSpendingForBudgetByAccount(accountId: String, startEpochDay: Long, endEpochDay: Long) = flowOf(0L)
}

class FakeAccountRepository : AccountRepository {
    private val accounts = mutableListOf<Account>()
    override fun observeAll() = flowOf(accounts.filter { !it.archived })
    override fun observeAllIncludingArchived() = flowOf(accounts.toList())
    override fun observeBalances() = flowOf(accounts.map { AccountBalance(it.id, it.initialBalanceMinor) })
    override fun observeTotalBalanceMinor() = flowOf(accounts.sumOf { it.initialBalanceMinor })
    override suspend fun getById(id: String) = accounts.find { it.id == id }
    override suspend fun upsert(account: Account) {
        accounts.removeAll { it.id == account.id }
        accounts.add(account)
    }
    override suspend fun deleteById(id: String) { accounts.removeAll { it.id == id } }
    suspend fun addAccount(account: Account) { upsert(account) }
}

class FakeCategoryRepository : CategoryRepository {
    private val categories = mutableListOf<Category>()
    override fun observeAll() = flowOf(categories.filter { !it.isArchived })
    override fun observeByKind(kind: CategoryKind) = flowOf(categories.filter { it.kind == kind && !it.isArchived })
    override suspend fun getById(id: String) = categories.find { it.id == id }
    override suspend fun upsert(category: Category) {
        categories.removeAll { it.id == category.id }
        categories.add(category)
    }
    override suspend fun deleteById(id: String) { categories.removeAll { it.id == id } }
    suspend fun addCategory(category: Category) { upsert(category) }
}

private typealias AccountBalance = com.jerries.expense.domain.model.AccountBalance
