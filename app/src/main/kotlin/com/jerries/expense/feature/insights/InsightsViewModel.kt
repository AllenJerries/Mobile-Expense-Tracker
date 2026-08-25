package com.jerries.expense.feature.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.DailyTotal
import com.jerries.expense.domain.model.SpendingByCategory
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.usecase.BudgetSpending
import com.jerries.expense.domain.usecase.ObserveBudgetSpendingUseCase
import com.jerries.expense.domain.usecase.ObserveDailyTotalsUseCase
import com.jerries.expense.domain.usecase.ObserveExpensesByCategoryUseCase
import com.jerries.expense.domain.usecase.ObserveMonthlyTotalsUseCase
import com.jerries.expense.domain.usecase.ObserveTransactionsByDateRangeUseCase
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InsightsUiState(
    val isLoading: Boolean = true,
    val insights: List<Insight> = emptyList(),
    val currencyCode: String = "USD",
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val insightEngine: FinancialInsightEngine,
    private val observeDailyTotals: ObserveDailyTotalsUseCase,
    private val observeExpensesByCategory: ObserveExpensesByCategoryUseCase,
    private val observeMonthlyTotals: ObserveMonthlyTotalsUseCase,
    private val observeTransactionsByDateRange: ObserveTransactionsByDateRangeUseCase,
    private val observeBudgetSpending: ObserveBudgetSpendingUseCase,
    private val observeUserPreferences: ObserveUserPreferencesUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        val today = timeProvider.today()
        val currentMonth = YearMonth.from(today)
        val lastMonth = currentMonth.minusMonths(1)

        viewModelScope.launch {
            observeUserPreferences().collect { prefs ->
                _uiState.update { it.copy(currencyCode = prefs.currencyCode) }
            }
        }

        viewModelScope.launch {
            val today = timeProvider.today()
            val currentMonth = YearMonth.from(today)
            val lastMonth = currentMonth.minusMonths(1)
            val currentStart = currentMonth.atDay(1).toEpochDay()
            val currentEnd = currentMonth.atEndOfMonth().toEpochDay().coerceAtMost(today.toEpochDay())
            val lastStart = lastMonth.atDay(1).toEpochDay()
            val lastEnd = lastMonth.atEndOfMonth().toEpochDay()

            combine(
                observeTransactionsByDateRange(currentStart, currentEnd),
                observeTransactionsByDateRange(lastStart, lastEnd),
                observeExpensesByCategory(currentStart, currentEnd),
                observeBudgetSpending(today.toEpochDay()),
                observeDailyTotals(currentStart, currentEnd),
            ) { currentTx: List<Transaction>,
                lastTx: List<Transaction>,
                categoryBreakdown: List<SpendingByCategory>,
                budgetSpendings: List<BudgetSpending>,
                dailyTotals: List<DailyTotal> ->

                val currentExpenses = currentTx.filter { it.isExpense }.sumOf { it.amountMinor }
                val currentIncome = currentTx.filter { it.isIncome }.sumOf { it.amountMinor }
                val lastExpenses = lastTx.filter { it.isExpense }.sumOf { it.amountMinor }
                val lastIncome = lastTx.filter { it.isIncome }.sumOf { it.amountMinor }

                val insightCtx = InsightContext(
                    currentMonth = currentMonth,
                    currentMonthExpenses = currentExpenses,
                    currentMonthIncome = currentIncome,
                    lastMonthExpenses = lastExpenses,
                    lastMonthIncome = lastIncome,
                    expensesByCategory = categoryBreakdown,
                    budgetSpendings = budgetSpendings,
                    dailyTotals = dailyTotals,
                    allTransactions = currentTx,
                    today = today,
                )

                val insights = insightEngine.generateInsights(insightCtx)
                InsightsUiState(isLoading = false, insights = insights)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
