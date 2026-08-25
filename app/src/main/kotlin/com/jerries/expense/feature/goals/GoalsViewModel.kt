package com.jerries.expense.feature.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.IdGenerator
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.core.ui.UiEvent
import com.jerries.expense.core.ui.UiEventChannel
import com.jerries.expense.core.util.toMinorUnitsOrNull
import com.jerries.expense.domain.model.SavingsGoal
import com.jerries.expense.domain.repository.SavingsGoalRepository
import com.jerries.expense.domain.usecase.ObserveSavingsGoalsUseCase
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import com.jerries.expense.domain.usecase.UpdateSavingsGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface GoalEvent : UiEvent {
    data object Saved : GoalEvent
    data object Deleted : GoalEvent
    data class Error(val message: String) : GoalEvent
}

data class GoalFormState(
    val name: String = "",
    val targetInput: String = "",
    val deadlineEpochDay: Long? = null,
    val icon: String = "savings",
    val isEditing: Boolean = false,
    val editingId: String = "",
)

data class ContributionState(
    val goalId: String = "",
    val goalName: String = "",
    val amountInput: String = "",
    val isWithdraw: Boolean = false,
)

data class GoalsUiState(
    val isLoading: Boolean = true,
    val currencyCode: String = "USD",
    val goals: List<SavingsGoal> = emptyList(),
    val showForm: Boolean = false,
    val form: GoalFormState = GoalFormState(),
    val showContribution: Boolean = false,
    val contribution: ContributionState = ContributionState(),
) {
    val isEmpty: Boolean get() = !isLoading && goals.isEmpty()
    val activeGoals: List<SavingsGoal> get() = goals.filter { !it.completed }
    val completedGoals: List<SavingsGoal> get() = goals.filter { it.completed }
}

@HiltViewModel
class GoalsViewModel @Inject constructor(
    observeSavingsGoals: ObserveSavingsGoalsUseCase,
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val updateSavingsGoal: UpdateSavingsGoalUseCase,
    private val goalRepository: SavingsGoalRepository,
    private val timeProvider: TimeProvider,
    private val idGenerator: IdGenerator,
) : ViewModel() {

    private val eventChannel = UiEventChannel()
    val events = eventChannel.events

    private val _formState = MutableStateFlow(GoalFormState())
    private val _contributionState = MutableStateFlow(ContributionState())
    private val _uiState = MutableStateFlow(GoalsUiState())

    val uiState: StateFlow<GoalsUiState> = combine(
        observeSavingsGoals(),
        observeUserPreferences(),
        _formState,
        _contributionState,
    ) { goals, prefs, form, contribution ->
        GoalsUiState(
            isLoading = false,
            currencyCode = prefs.currencyCode,
            goals = goals,
            showForm = _uiState.value.showForm,
            form = form,
            showContribution = _uiState.value.showContribution,
            contribution = contribution,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = GoalsUiState(),
    )

    fun onShowForm(show: Boolean) {
        if (!show) _formState.value = GoalFormState()
        _uiState.update { it.copy(showForm = show) }
    }

    fun onFormNameChange(v: String) = _formState.update { it.copy(name = v) }
    fun onFormTargetChange(v: String) = _formState.update { it.copy(targetInput = v.filter { c -> c.isDigit() || c == '.' }) }
    fun onFormDeadlineChange(v: Long?) = _formState.update { it.copy(deadlineEpochDay = v) }
    fun onFormIconChange(v: String) = _formState.update { it.copy(icon = v) }

    fun onEditGoal(goal: SavingsGoal) {
        _formState.value = GoalFormState(
            name = goal.name,
            targetInput = (goal.targetMinor / 100.0).toString(),
            deadlineEpochDay = goal.deadlineEpochDay,
            icon = goal.icon ?: "savings",
            isEditing = true,
            editingId = goal.id,
        )
        _uiState.update { it.copy(showForm = true) }
    }

    fun onSaveGoal() {
        val form = _formState.value
        val targetMinor = form.targetInput.toMinorUnitsOrNull()
        if (targetMinor == null || targetMinor <= 0) {
            eventChannel.send(GoalEvent.Error("Enter a valid target amount"))
            return
        }
        viewModelScope.launch {
            val goal = SavingsGoal(
                id = if (form.isEditing) form.editingId else idGenerator.newId(),
                name = form.name.ifBlank { "Savings Goal" },
                targetMinor = targetMinor,
                savedMinor = if (form.isEditing) {
                    goalRepository.getById(form.editingId)?.savedMinor ?: 0L
                } else 0L,
                deadlineEpochDay = form.deadlineEpochDay,
                icon = form.icon,
                createdAtEpochMillis = timeProvider.nowMillis(),
                completed = false,
            )
            goalRepository.upsert(goal)
            _formState.value = GoalFormState()
            _uiState.update { it.copy(showForm = false) }
            eventChannel.send(GoalEvent.Saved)
        }
    }

    fun onDeleteGoal(id: String) {
        viewModelScope.launch {
            goalRepository.deleteById(id)
            eventChannel.send(GoalEvent.Deleted)
        }
    }

    fun onShowContribution(goalId: String, goalName: String, isWithdraw: Boolean = false) {
        _contributionState.value = ContributionState(goalId = goalId, goalName = goalName, isWithdraw = isWithdraw)
        _uiState.update { it.copy(showContribution = true) }
    }

    fun onContributionAmountChange(v: String) = _contributionState.update {
        it.copy(amountInput = v.filter { c -> c.isDigit() || c == '.' })
    }

    fun onContributionDismiss() {
        _contributionState.value = ContributionState()
        _uiState.update { it.copy(showContribution = false) }
    }

    fun onConfirmContribution() {
        val state = _contributionState.value
        val amount = state.amountInput.toMinorUnitsOrNull()
        if (amount == null || amount <= 0) {
            eventChannel.send(GoalEvent.Error("Enter a valid amount"))
            return
        }
        viewModelScope.launch {
            if (state.isWithdraw) {
                val goal = goalRepository.getById(state.goalId) ?: return@launch
                val newSaved = (goal.savedMinor - amount).coerceAtLeast(0)
                goalRepository.upsert(goal.copy(savedMinor = newSaved, completed = newSaved >= goal.targetMinor))
            } else {
                updateSavingsGoal.addAmount(state.goalId, amount)
            }
            onContributionDismiss()
        }
    }
}
