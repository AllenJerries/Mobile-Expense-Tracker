package com.jerries.expense.feature.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.IdGenerator
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.core.ui.UiEvent
import com.jerries.expense.core.ui.UiEventChannel
import com.jerries.expense.core.util.toMinorUnitsOrNull
import com.jerries.expense.domain.model.Budget
import com.jerries.expense.domain.model.BudgetPeriod
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.repository.BudgetRepository
import com.jerries.expense.domain.usecase.BudgetSpending
import com.jerries.expense.domain.usecase.ObserveBudgetSpendingUseCase
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface BudgetEvent : UiEvent {
    data object Saved : BudgetEvent
    data object Deleted : BudgetEvent
    data class Error(val message: String) : BudgetEvent
}

data class BudgetFormState(
    val name: String = "",
    val limitInput: String = "",
    val categoryId: String? = null,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val threshold: Double = 0.8,
    val isEditing: Boolean = false,
    val editingId: String = "",
    val isLoading: Boolean = false,
)

data class BudgetsUiState(
    val isLoading: Boolean = true,
    val currencyCode: String = "USD",
    val budgetSpendings: List<BudgetSpending> = emptyList(),
    val categories: List<Category> = emptyList(),
    val showForm: Boolean = false,
    val form: BudgetFormState = BudgetFormState(),
    val todayEpochDay: Long = 0L,
) {
    val isEmpty: Boolean get() = !isLoading && budgetSpendings.isEmpty()
}

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    observeBudgetSpending: ObserveBudgetSpendingUseCase,
    observeCategories: ObserveCategoriesUseCase,
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val budgetRepository: BudgetRepository,
    private val timeProvider: TimeProvider,
    private val idGenerator: IdGenerator,
) : ViewModel() {

    private val eventChannel = UiEventChannel()
    val events = eventChannel.events

    private val _formState = MutableStateFlow(BudgetFormState())
    private val _uiState = MutableStateFlow(BudgetsUiState())

    val uiState: StateFlow<BudgetsUiState> = combine(
        observeBudgetSpending(timeProvider.today().toEpochDay()),
        observeCategories(),
        observeUserPreferences(),
        _formState,
    ) { spendings, categories, prefs, form ->
        BudgetsUiState(
            isLoading = false,
            currencyCode = prefs.currencyCode,
            budgetSpendings = spendings,
            categories = categories,
            showForm = _uiState.value.showForm,
            form = form,
            todayEpochDay = timeProvider.today().toEpochDay(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = BudgetsUiState(),
    )

    fun onShowForm(show: Boolean) {
        if (!show) _formState.value = BudgetFormState()
        _uiState.update { it.copy(showForm = show) }
    }

    fun onFormNameChange(v: String) = _formState.update { it.copy(name = v) }
    fun onFormLimitChange(v: String) = _formState.update { it.copy(limitInput = v.filter { c -> c.isDigit() || c == '.' }) }
    fun onFormCategoryChange(v: String?) = _formState.update { it.copy(categoryId = v) }
    fun onFormPeriodChange(v: BudgetPeriod) = _formState.update { it.copy(period = v) }
    fun onFormThresholdChange(v: Double) = _formState.update { it.copy(threshold = v) }

    fun onEditBudget(budget: Budget) {
        _formState.value = BudgetFormState(
            name = budget.categoryId ?: "Overall",
            limitInput = (budget.limitMinor / 100.0).toString(),
            categoryId = budget.categoryId,
            period = budget.period,
            threshold = budget.alertThreshold,
            isEditing = true,
            editingId = budget.id,
        )
        _uiState.update { it.copy(showForm = true) }
    }

    fun onSaveBudget() {
        val form = _formState.value
        val limitMinor = form.limitInput.toMinorUnitsOrNull()
        if (limitMinor == null || limitMinor <= 0) {
            eventChannel.send(BudgetEvent.Error("Enter a valid limit amount"))
            return
        }
        val today = timeProvider.today()
        val (start, end) = when (form.period) {
            BudgetPeriod.WEEKLY -> today.with(java.time.DayOfWeek.MONDAY) to today.with(java.time.DayOfWeek.SUNDAY)
            BudgetPeriod.MONTHLY -> today.withDayOfMonth(1) to today.with(TemporalAdjusters.lastDayOfMonth())
            BudgetPeriod.YEARLY -> today.withDayOfYear(1) to today.withDayOfYear(today.lengthOfYear())
            BudgetPeriod.CUSTOM -> today to today.plusMonths(1).withDayOfMonth(1).minusDays(1)
        }
        viewModelScope.launch {
            val budget = Budget(
                id = if (form.isEditing) form.editingId else idGenerator.newId(),
                categoryId = form.categoryId,
                accountId = null,
                limitMinor = limitMinor,
                period = form.period,
                startEpochDay = start.toEpochDay(),
                endEpochDay = end.toEpochDay(),
                alertThreshold = form.threshold,
                createdAtEpochMillis = timeProvider.nowMillis(),
            )
            budgetRepository.upsert(budget)
            _formState.value = BudgetFormState()
            _uiState.update { it.copy(showForm = false) }
            eventChannel.send(BudgetEvent.Saved)
        }
    }

    fun onDeleteBudget(id: String) {
        viewModelScope.launch {
            budgetRepository.deleteById(id)
            eventChannel.send(BudgetEvent.Deleted)
        }
    }
}
