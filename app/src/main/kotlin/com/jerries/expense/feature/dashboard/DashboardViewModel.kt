package com.jerries.expense.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.core.designsystem.component.TransactionRowModel
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.usecase.BudgetSpending
import com.jerries.expense.domain.usecase.ObserveBudgetSpendingUseCase
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase
import com.jerries.expense.domain.usecase.ObserveExpensesByCategoryUseCase
import com.jerries.expense.domain.usecase.ObserveMonthlyTotalsUseCase
import com.jerries.expense.domain.usecase.ObserveRecentTransactionsUseCase
import com.jerries.expense.domain.usecase.ObserveTotalBalanceUseCase
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth

data class CategorySpending(
    val name: String,
    val amountMinor: Long,
    val percentage: Float,
)

data class DashboardUiState(
    val isLoading: Boolean = true,
    val totalBalanceMinor: Long = 0L,
    val currencyCode: String = "USD",
    val todayEpochDay: Long = 0L,
    val currentMonth: YearMonth = YearMonth.now(),
    val incomeThisMonth: Long = 0L,
    val expensesThisMonth: Long = 0L,
    val savingsThisMonth: Long = 0L,
    val incomeLastMonth: Long = 0L,
    val expensesLastMonth: Long = 0L,
    val topCategories: List<CategorySpending> = emptyList(),
    val budgetSpendings: List<BudgetSpending> = emptyList(),
    val recentTransactions: List<TransactionRowModel> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && recentTransactions.isEmpty()

    val incomeChangePercent: Float
        get() = if (incomeLastMonth > 0) {
            ((incomeThisMonth - incomeLastMonth).toFloat() / incomeLastMonth.toFloat())
        } else {
            if (incomeThisMonth > 0) 1f else 0f
        }

    val expenseChangePercent: Float
        get() = if (expensesLastMonth > 0) {
            ((expensesThisMonth - expensesLastMonth).toFloat() / expensesLastMonth.toFloat())
        } else {
            if (expensesThisMonth > 0) 1f else 0f
        }
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeTotalBalance: ObserveTotalBalanceUseCase,
    observeRecentTransactions: ObserveRecentTransactionsUseCase,
    observeCategories: ObserveCategoriesUseCase,
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val observeMonthlyTotals: ObserveMonthlyTotalsUseCase,
    observeBudgetSpending: ObserveBudgetSpendingUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        val today = timeProvider.today()
        val currentMonth = YearMonth.from(today)

        viewModelScope.launch {
            combine(
                observeTotalBalance(),
                observeRecentTransactions(RECENT_LIMIT),
                observeCategories(),
                observeUserPreferences(),
            ) { balance, transactions, categories, prefs ->
                val byId = categories.associateBy(Category::id)
                DashboardUiState(
                    isLoading = false,
                    totalBalanceMinor = balance,
                    currencyCode = prefs.currencyCode,
                    todayEpochDay = today.toEpochDay(),
                    currentMonth = currentMonth,
                    recentTransactions = transactions.map { t -> t.toRowModel(byId) },
                )
            }.collect { baseState ->
                _uiState.value = baseState
                loadMonthData(baseState.currentMonth)
            }
        }

        viewModelScope.launch {
            _selectedMonth.collect { month ->
                loadMonthData(month)
            }
        }
    }

    private suspend fun loadMonthData(month: YearMonth) {
        val prevMonth = month.minusMonths(1)

        // Current month income/expenses
        var incomeThisMonth = 0L
        var expensesThisMonth = 0L
        observeMonthlyTotals(month).collect { pair ->
            incomeThisMonth = pair.first
            expensesThisMonth = pair.second
        }

        // Previous month income/expenses
        var incomeLastMonth = 0L
        var expensesLastMonth = 0L
        observeMonthlyTotals(prevMonth).collect { pair ->
            incomeLastMonth = pair.first
            expensesLastMonth = pair.second
        }

        _uiState.update {
            it.copy(
                incomeThisMonth = incomeThisMonth,
                expensesThisMonth = expensesThisMonth,
                savingsThisMonth = incomeThisMonth - expensesThisMonth,
                incomeLastMonth = incomeLastMonth,
                expensesLastMonth = expensesLastMonth,
            )
        }
    }

    fun onMonthChange(month: YearMonth) {
        _selectedMonth.value = month
    }

    fun onPreviousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
    }

    fun onNextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
    }

    private fun categoriesById(categories: List<Category>) =
        categories.associateBy(Category::id)

    private fun Transaction.toRowModel(
        categoriesById: Map<String, Category>,
    ): TransactionRowModel {
        val category = categoryId?.let(categoriesById::get)
        val displayTitle = title?.takeIf { it.isNotBlank() }
            ?: note?.takeIf { it.isNotBlank() }
            ?: category?.name
            ?: type.name.replaceFirstChar { it.uppercase() }
        return TransactionRowModel(
            id = id,
            title = displayTitle,
            categoryName = category?.name,
            dateEpochDay = dateEpochDay,
            amountMinor = amountMinor,
            isIncome = isIncome,
            icon = JeIcons.category(category?.iconKey),
        )
    }

    companion object {
        private const val RECENT_LIMIT = 5
    }
}
