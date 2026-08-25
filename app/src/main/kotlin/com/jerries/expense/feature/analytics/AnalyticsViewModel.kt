package com.jerries.expense.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.DailyTotal
import com.jerries.expense.domain.model.SpendingByCategory
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.usecase.ObserveAccountsUseCase
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase
import com.jerries.expense.domain.usecase.ObserveDailyTotalsUseCase
import com.jerries.expense.domain.usecase.ObserveExpensesByCategoryUseCase
import com.jerries.expense.domain.usecase.ObserveIncomeByCategoryUseCase
import com.jerries.expense.domain.usecase.ObserveMonthlyTotalsUseCase
import com.jerries.expense.domain.usecase.ObserveTransactionsByDateRangeUseCase
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DayLabel(val label: String, val epochDay: Long)

data class CategorySlice(
    val name: String,
    val amountMinor: Long,
    val colorArgb: Long,
    val percentage: Float,
)

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val currencyCode: String = "USD",
    val todayEpochDay: Long = 0L,
    val currentMonth: YearMonth = YearMonth.now(),
    val dailyTotals: List<DailyTotal> = emptyList(),
    val dayLabels: List<DayLabel> = emptyList(),
    val expensesByCategory: List<CategorySlice> = emptyList(),
    val incomeThisMonth: Long = 0L,
    val expensesThisMonth: Long = 0L,
    val incomeLastMonth: Long = 0L,
    val expensesLastMonth: Long = 0L,
    val avgDailySpending: Long = 0L,
    val highestCategoryName: String = "",
    val highestCategoryAmount: Long = 0L,
    val highestTransactionTitle: String = "",
    val highestTransactionAmount: Long = 0L,
    val savingsRate: Float = 0f,
    val monthIncomeValues: List<Float> = emptyList(),
    val monthExpenseValues: List<Float> = emptyList(),
    val monthLabels: List<String> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && dailyTotals.isEmpty()
}

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val observeDailyTotals: ObserveDailyTotalsUseCase,
    private val observeExpensesByCategory: ObserveExpensesByCategoryUseCase,
    private val observeMonthlyTotals: ObserveMonthlyTotalsUseCase,
    private val observeTransactionsByDateRange: ObserveTransactionsByDateRangeUseCase,
    private val observeCategories: ObserveCategoriesUseCase,
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("d")

    init {
        val today = timeProvider.today()
        val currentMonth = YearMonth.from(today)
        _uiState.value = AnalyticsUiState(
            isLoading = false,
            todayEpochDay = today.toEpochDay(),
            currentMonth = currentMonth,
        )
        loadMonthData(currentMonth)

        viewModelScope.launch {
            observeUserPreferences().collect { prefs ->
                _uiState.update { it.copy(currencyCode = prefs.currencyCode) }
            }
        }
    }

    fun onPreviousMonth() {
        val newMonth = _uiState.value.currentMonth.minusMonths(1)
        _uiState.update { it.copy(currentMonth = newMonth) }
        loadMonthData(newMonth)
    }

    fun onNextMonth() {
        val newMonth = _uiState.value.currentMonth.plusMonths(1)
        _uiState.update { it.copy(currentMonth = newMonth) }
        loadMonthData(newMonth)
    }

    private fun loadMonthData(month: YearMonth) {
        val today = timeProvider.today()
        val startEpochDay = month.atDay(1).toEpochDay()
        val endEpochDay = month.atEndOfMonth().toEpochDay().coerceAtMost(today.toEpochDay())
        val prevMonth = month.minusMonths(1)
        val prevStart = prevMonth.atDay(1).toEpochDay()
        val prevEnd = prevMonth.atEndOfMonth().toEpochDay()

        viewModelScope.launch {
            combine(
                observeDailyTotals(startEpochDay, endEpochDay),
                observeExpensesByCategory(startEpochDay, endEpochDay),
                observeTransactionsByDateRange(startEpochDay, endEpochDay),
                observeMonthlyTotals(month),
                observeMonthlyTotals(prevMonth),
            ) { dailyTotals, categoryBreakdown, transactions, monthTotals, prevTotals ->
                val totalExpenses = transactions.filter { it.isExpense }.sumOf { it.amountMinor }
                val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amountMinor }
                val daysInMonth = month.lengthOfMonth().toLong()
                val daysElapsed = (java.time.temporal.ChronoUnit.DAYS.between(month.atDay(1), today) + 1).coerceAtMost(daysInMonth)
                val avgDaily = if (daysElapsed > 0) totalExpenses / daysElapsed else 0L

                val highestTx = transactions.filter { it.isExpense }.maxByOrNull { it.amountMinor }
                val highestCat = categoryBreakdown.maxByOrNull { it.totalMinor }

                val savingsRate = if (totalIncome > 0) {
                    ((totalIncome - totalExpenses).toFloat() / totalIncome.toFloat()).coerceIn(-1f, 1f)
                } else 0f

                val maxCatAmount = categoryBreakdown.maxOfOrNull { it.totalMinor }?.coerceAtLeast(1) ?: 1L
                val slices = categoryBreakdown.map { cat ->
                    CategorySlice(
                        name = cat.categoryName,
                        amountMinor = cat.totalMinor,
                        colorArgb = cat.colorArgb,
                        percentage = (cat.totalMinor.toFloat() / maxCatAmount.toFloat()).coerceIn(0f, 1f),
                    )
                }

                val labels = dailyTotals.map { day ->
                    val date = LocalDate.ofEpochDay(day.dateEpochDay)
                    DayLabel(label = date.format(formatter), epochDay = day.dateEpochDay)
                }

                val incomeVals = dailyTotals.map { it.incomeMinor.toFloat() }
                val expenseVals = dailyTotals.map { it.expenseMinor.toFloat() }
                val dateLabels = dailyTotals.map {
                    LocalDate.ofEpochDay(it.dateEpochDay).format(formatter)
                }

                AnalyticsUiState(
                    isLoading = false,
                    todayEpochDay = today.toEpochDay(),
                    currentMonth = month,
                    dailyTotals = dailyTotals,
                    dayLabels = labels,
                    expensesByCategory = slices,
                    incomeThisMonth = totalIncome,
                    expensesThisMonth = totalExpenses,
                    incomeLastMonth = monthTotals.first,
                    expensesLastMonth = monthTotals.second,
                    avgDailySpending = avgDaily,
                    highestCategoryName = highestCat?.categoryName ?: "",
                    highestCategoryAmount = highestCat?.totalMinor ?: 0L,
                    highestTransactionTitle = highestTx?.title ?: highestTx?.note ?: "",
                    highestTransactionAmount = highestTx?.amountMinor ?: 0L,
                    savingsRate = savingsRate,
                    monthIncomeValues = incomeVals,
                    monthExpenseValues = expenseVals,
                    monthLabels = dateLabels,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
