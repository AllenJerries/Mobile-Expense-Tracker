package com.jerries.expense.feature.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.model.SavingsGoal
import com.jerries.expense.domain.model.SpendingByCategory
import com.jerries.expense.domain.usecase.BudgetSpending
import com.jerries.expense.domain.usecase.ObserveAccountsUseCase
import com.jerries.expense.domain.usecase.ObserveBudgetSpendingUseCase
import com.jerries.expense.domain.usecase.ObserveExpensesByCategoryUseCase
import com.jerries.expense.domain.usecase.ObserveIncomeByCategoryUseCase
import com.jerries.expense.domain.usecase.ObserveMonthlyTotalsUseCase
import com.jerries.expense.domain.usecase.ObserveSavingsGoalsUseCase
import com.jerries.expense.domain.usecase.ObserveTransactionsByDateRangeUseCase
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import com.jerries.expense.core.util.CurrencyFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ReportType { MONTHLY, YEARLY, INCOME, EXPENSE, CATEGORY, BUDGET, SAVINGS }

data class ReportsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val reportType: ReportType = ReportType.MONTHLY,
    val startEpochDay: Long = 0L,
    val endEpochDay: Long = 0L,
    val currencyCode: String = "USD",
    val income: Long = 0L,
    val expenses: Long = 0L,
    val savings: Long = 0L,
    val categoryBreakdown: List<SpendingByCategory> = emptyList(),
    val incomeCategoryBreakdown: List<SpendingByCategory> = emptyList(),
    val budgetSpendings: List<BudgetSpending> = emptyList(),
    val goals: List<SavingsGoal> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val monthLabel: String = "",
    val currentMonth: YearMonth = YearMonth.now(),
    val currentYear: Int = YearMonth.now().year,
) {
    val hasData: Boolean get() = income > 0L || expenses > 0L || categoryBreakdown.isNotEmpty() || budgetSpendings.isNotEmpty() || goals.isNotEmpty()
}

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val observeTransactionsByDateRange: ObserveTransactionsByDateRangeUseCase,
    private val observeExpensesByCategory: ObserveExpensesByCategoryUseCase,
    private val observeIncomeByCategory: ObserveIncomeByCategoryUseCase,
    private val observeBudgetSpending: ObserveBudgetSpendingUseCase,
    private val observeSavingsGoals: ObserveSavingsGoalsUseCase,
    private val observeAccounts: ObserveAccountsUseCase,
    private val observeMonthlyTotals: ObserveMonthlyTotalsUseCase,
    private val observeUserPreferences: ObserveUserPreferencesUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        val today = timeProvider.today()
        val currentMonth = YearMonth.from(today)
        val startEpochDay = currentMonth.atDay(1).toEpochDay()
        val endEpochDay = currentMonth.atEndOfMonth().toEpochDay()

        _uiState.value = ReportsUiState(
            isLoading = true,
            startEpochDay = startEpochDay,
            endEpochDay = endEpochDay,
            currentMonth = currentMonth,
            currentYear = currentMonth.year,
        )

        viewModelScope.launch {
            observeUserPreferences().collect { prefs ->
                _uiState.update { it.copy(currencyCode = prefs.currencyCode) }
                loadReportData()
            }
        }
    }

    fun onReportTypeChange(type: ReportType) {
        _uiState.update { it.copy(reportType = type) }
        resetDateRangeForType(type)
        loadReportData()
    }

    fun onPreviousMonth() {
        val current = _uiState.value.currentMonth
        val prev = current.minusMonths(1)
        _uiState.update { it.copy(currentMonth = prev) }
        updateDateRangeFromMonth(prev)
        loadReportData()
    }

    fun onNextMonth() {
        val current = _uiState.value.currentMonth
        val next = current.plusMonths(1)
        _uiState.update { it.copy(currentMonth = next) }
        updateDateRangeFromMonth(next)
        loadReportData()
    }

    fun onPreviousYear() {
        val newYear = _uiState.value.currentYear - 1
        _uiState.update { it.copy(currentYear = newYear) }
        updateDateRangeFromYear(newYear)
        loadReportData()
    }

    fun onNextYear() {
        val newYear = _uiState.value.currentYear + 1
        _uiState.update { it.copy(currentYear = newYear) }
        updateDateRangeFromYear(newYear)
        loadReportData()
    }

    private fun resetDateRangeForType(type: ReportType) {
        val today = timeProvider.today()
        when (type) {
            ReportType.MONTHLY -> {
                val month = YearMonth.from(today)
                _uiState.update {
                    it.copy(
                        currentMonth = month,
                        startEpochDay = month.atDay(1).toEpochDay(),
                        endEpochDay = month.atEndOfMonth().toEpochDay(),
                    )
                }
            }
            ReportType.YEARLY -> {
                _uiState.update {
                    it.copy(
                        currentYear = today.year,
                        startEpochDay = YearMonth.of(today.year, 1).atDay(1).toEpochDay(),
                        endEpochDay = YearMonth.of(today.year, 12).atEndOfMonth().toEpochDay(),
                    )
                }
            }
            else -> {
                val month = _uiState.value.currentMonth
                _uiState.update {
                    it.copy(
                        startEpochDay = month.atDay(1).toEpochDay(),
                        endEpochDay = month.atEndOfMonth().toEpochDay(),
                    )
                }
            }
        }
    }

    private fun updateDateRangeFromMonth(month: YearMonth) {
        _uiState.update {
            it.copy(
                startEpochDay = month.atDay(1).toEpochDay(),
                endEpochDay = month.atEndOfMonth().toEpochDay(),
            )
        }
    }

    private fun updateDateRangeFromYear(year: Int) {
        _uiState.update {
            it.copy(
                startEpochDay = YearMonth.of(year, 1).atDay(1).toEpochDay(),
                endEpochDay = YearMonth.of(year, 12).atEndOfMonth().toEpochDay(),
            )
        }
    }

    private fun loadReportData() {
        val state = _uiState.value
        val start = state.startEpochDay
        val end = state.endEpochDay

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val transactions = observeTransactionsByDateRange(start, end).first()
                val income = transactions.filter { it.isIncome }.sumOf { it.amountMinor }
                val expenses = transactions.filter { it.isExpense }.sumOf { it.amountMinor }

                val expenseCategories = observeExpensesByCategory(start, end).first()
                val incomeCategories = observeIncomeByCategory(start, end).first()
                val budgetSpendings = observeBudgetSpending(start).first()
                val goals = observeSavingsGoals().first()
                val accounts = observeAccounts().first()

                val monthLabel = when (state.reportType) {
                    ReportType.YEARLY -> state.currentYear.toString()
                    else -> {
                        val startFormatter = DateTimeFormatter.ofPattern("MMM d")
                        val endFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
                        val startDate = LocalDate.ofEpochDay(start)
                        val endDate = LocalDate.ofEpochDay(end)
                        "${startDate.format(startFormatter)} – ${endDate.format(endFormatter)}"
                    }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        income = income,
                        expenses = expenses,
                        savings = income - expenses,
                        categoryBreakdown = expenseCategories,
                        incomeCategoryBreakdown = incomeCategories,
                        budgetSpendings = budgetSpendings,
                        goals = goals,
                        accounts = accounts,
                        monthLabel = monthLabel,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    fun generateCsv(): String {
        val state = _uiState.value
        val currencyCode = state.currencyCode
        val sb = StringBuilder()

        sb.appendLine("Report Type: ${state.reportType.name}")
        sb.appendLine("Period: ${state.monthLabel}")
        sb.appendLine("Generated: ${java.time.Instant.ofEpochMilli(System.currentTimeMillis())}")
        sb.appendLine()

        when (state.reportType) {
            ReportType.MONTHLY, ReportType.YEARLY -> {
                sb.appendLine("Summary")
                sb.appendLine("Type,Amount")
                sb.appendLine("Income,${CurrencyFormatter.formatMinorUnits(state.income, currencyCode)}")
                sb.appendLine("Expenses,${CurrencyFormatter.formatMinorUnits(state.expenses, currencyCode)}")
                sb.appendLine("Savings,${CurrencyFormatter.formatMinorUnits(state.savings, currencyCode)}")
                sb.appendLine()

                if (state.categoryBreakdown.isNotEmpty()) {
                    sb.appendLine("Expense Category Breakdown")
                    sb.appendLine("Category,Amount")
                    state.categoryBreakdown.forEach { cat ->
                        sb.appendLine("${cat.categoryName},${CurrencyFormatter.formatMinorUnits(cat.totalMinor, currencyCode)}")
                    }
                    sb.appendLine()
                }

                if (state.accounts.isNotEmpty()) {
                    sb.appendLine("Accounts")
                    sb.appendLine("Name,Balance")
                    state.accounts.forEach { acc ->
                        sb.appendLine("${acc.name},${CurrencyFormatter.formatMinorUnits(acc.initialBalanceMinor, acc.currencyCode)}")
                    }
                }
            }

            ReportType.INCOME -> {
                sb.appendLine("Income by Category")
                sb.appendLine("Category,Amount")
                state.incomeCategoryBreakdown.forEach { cat ->
                    sb.appendLine("${cat.categoryName},${CurrencyFormatter.formatMinorUnits(cat.totalMinor, currencyCode)}")
                }
                sb.appendLine()
                sb.appendLine("Total Income,${CurrencyFormatter.formatMinorUnits(state.income, currencyCode)}")
            }

            ReportType.EXPENSE -> {
                sb.appendLine("Expenses by Category")
                sb.appendLine("Category,Amount")
                state.categoryBreakdown.forEach { cat ->
                    sb.appendLine("${cat.categoryName},${CurrencyFormatter.formatMinorUnits(cat.totalMinor, currencyCode)}")
                }
                sb.appendLine()
                sb.appendLine("Total Expenses,${CurrencyFormatter.formatMinorUnits(state.expenses, currencyCode)}")
            }

            ReportType.CATEGORY -> {
                sb.appendLine("Expense Category Breakdown")
                sb.appendLine("Category,Amount")
                state.categoryBreakdown.forEach { cat ->
                    sb.appendLine("${cat.categoryName},${CurrencyFormatter.formatMinorUnits(cat.totalMinor, currencyCode)}")
                }
                sb.appendLine()
                if (state.incomeCategoryBreakdown.isNotEmpty()) {
                    sb.appendLine("Income Category Breakdown")
                    sb.appendLine("Category,Amount")
                    state.incomeCategoryBreakdown.forEach { cat ->
                        sb.appendLine("${cat.categoryName},${CurrencyFormatter.formatMinorUnits(cat.totalMinor, currencyCode)}")
                    }
                }
            }

            ReportType.BUDGET -> {
                sb.appendLine("Budget Summary")
                sb.appendLine("Budget,Spent,Limit,Status")
                state.budgetSpendings.forEach { b ->
                    val name = b.budget.categoryId ?: "Overall"
                    val status = if (b.exceeded) "Exceeded" else "On Track"
                    sb.appendLine(
                        "$name,${CurrencyFormatter.formatMinorUnits(b.spentMinor, currencyCode)}," +
                            "${CurrencyFormatter.formatMinorUnits(b.limitMinor, currencyCode)},$status",
                    )
                }
            }

            ReportType.SAVINGS -> {
                sb.appendLine("Savings Goals")
                sb.appendLine("Goal,Target,Saved,Progress")
                state.goals.forEach { g ->
                    val progress = "${(g.progress * 100).toInt()}%"
                    sb.appendLine(
                        "${g.name},${CurrencyFormatter.formatMinorUnits(g.targetMinor, currencyCode)}," +
                            "${CurrencyFormatter.formatMinorUnits(g.savedMinor, currencyCode)},$progress",
                    )
                }
            }
        }

        return sb.toString()
    }

    fun generateJson(): String {
        val state = _uiState.value
        val currencyCode = state.currencyCode

        val sb = StringBuilder()
        sb.appendLine("{")
        sb.appendLine("  \"reportType\": \"${state.reportType.name}\",")
        sb.appendLine("  \"period\": \"${state.monthLabel}\",")
        sb.appendLine("  \"generatedAtEpochMillis\": ${System.currentTimeMillis()},")
        sb.appendLine("  \"currencyCode\": \"$currencyCode\",")
        sb.appendLine("  \"summary\": {")
        sb.appendLine("    \"income\": \"${CurrencyFormatter.formatMinorUnits(state.income, currencyCode)}\",")
        sb.appendLine("    \"incomeMinor\": ${state.income},")
        sb.appendLine("    \"expenses\": \"${CurrencyFormatter.formatMinorUnits(state.expenses, currencyCode)}\",")
        sb.appendLine("    \"expensesMinor\": ${state.expenses},")
        sb.appendLine("    \"savings\": \"${CurrencyFormatter.formatMinorUnits(state.savings, currencyCode)}\",")
        sb.appendLine("    \"savingsMinor\": ${state.savings}")
        sb.appendLine("  },")

        if (state.categoryBreakdown.isNotEmpty()) {
            sb.appendLine("  \"expenseCategoryBreakdown\": [")
            state.categoryBreakdown.forEachIndexed { index, cat ->
                val pct = if (state.expenses > 0) {
                    ((cat.totalMinor.toDouble() / state.expenses.coerceAtLeast(1).toDouble()) * 100).toInt()
                } else 0
                val comma = if (index < state.categoryBreakdown.size - 1) "," else ""
                sb.appendLine("    {\"category\": \"${cat.categoryName}\", \"amount\": \"${CurrencyFormatter.formatMinorUnits(cat.totalMinor, currencyCode)}\", \"amountMinor\": ${cat.totalMinor}, \"percentage\": \"$pct%\"}$comma")
            }
            sb.appendLine("  ],")
        }

        if (state.incomeCategoryBreakdown.isNotEmpty()) {
            sb.appendLine("  \"incomeCategoryBreakdown\": [")
            state.incomeCategoryBreakdown.forEachIndexed { index, cat ->
                val comma = if (index < state.incomeCategoryBreakdown.size - 1) "," else ""
                sb.appendLine("    {\"category\": \"${cat.categoryName}\", \"amount\": \"${CurrencyFormatter.formatMinorUnits(cat.totalMinor, currencyCode)}\", \"amountMinor\": ${cat.totalMinor}}$comma")
            }
            sb.appendLine("  ],")
        }

        if (state.budgetSpendings.isNotEmpty()) {
            sb.appendLine("  \"budgetSummary\": [")
            state.budgetSpendings.forEachIndexed { index, b ->
                val name = b.budget.categoryId ?: "Overall"
                val pct = (b.percentage * 100).toInt()
                val comma = if (index < state.budgetSpendings.size - 1) "," else ""
                sb.appendLine("    {\"budgetName\": \"$name\", \"spent\": \"${CurrencyFormatter.formatMinorUnits(b.spentMinor, currencyCode)}\", \"limit\": \"${CurrencyFormatter.formatMinorUnits(b.limitMinor, currencyCode)}\", \"percentage\": \"$pct%\", \"exceeded\": ${b.exceeded}}$comma")
            }
            sb.appendLine("  ],")
        }

        if (state.goals.isNotEmpty()) {
            sb.appendLine("  \"savingsGoals\": [")
            state.goals.forEachIndexed { index, g ->
                val pct = (g.progress * 100).toInt()
                val comma = if (index < state.goals.size - 1) "," else ""
                sb.appendLine("    {\"name\": \"${g.name}\", \"target\": \"${CurrencyFormatter.formatMinorUnits(g.targetMinor, currencyCode)}\", \"saved\": \"${CurrencyFormatter.formatMinorUnits(g.savedMinor, currencyCode)}\", \"progress\": \"$pct%\", \"completed\": ${g.completed}}$comma")
            }
            sb.appendLine("  ],")
        }

        if (state.accounts.isNotEmpty()) {
            sb.appendLine("  \"accounts\": [")
            state.accounts.forEachIndexed { index, acc ->
                val comma = if (index < state.accounts.size - 1) "," else ""
                sb.appendLine("    {\"name\": \"${acc.name}\", \"balance\": \"${CurrencyFormatter.formatMinorUnits(acc.initialBalanceMinor, acc.currencyCode)}\"}$comma")
            }
            sb.appendLine("  ]")
        } else {
            sb.setLength(sb.length - 1)
            if (sb.endsWith(",")) {
                sb.setLength(sb.length - 1)
            }
        }

        sb.appendLine("}")
        return sb.toString()
    }
}
