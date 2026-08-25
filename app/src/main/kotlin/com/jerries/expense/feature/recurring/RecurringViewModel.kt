package com.jerries.expense.feature.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.IdGenerator
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.core.ui.UiEvent
import com.jerries.expense.core.ui.UiEventChannel
import com.jerries.expense.core.util.toMinorUnitsOrNull
import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.CategoryKind
import com.jerries.expense.domain.model.RecurrenceFrequency
import com.jerries.expense.domain.model.RecurringTransaction
import com.jerries.expense.domain.model.TransactionType
import com.jerries.expense.domain.repository.RecurringTransactionRepository
import com.jerries.expense.domain.usecase.ObserveAccountsUseCase
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase
import com.jerries.expense.domain.usecase.ObserveRecurringTransactionsUseCase
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

sealed interface RecurringEvent : UiEvent {
    data object Saved : RecurringEvent
    data object Deleted : RecurringEvent
    data class Error(val message: String) : RecurringEvent
}

data class RecurringFormState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amountInput: String = "",
    val accountId: String? = null,
    val categoryId: String? = null,
    val description: String = "",
    val frequency: RecurrenceFrequency = RecurrenceFrequency.MONTHLY,
    val nextOccurrenceEpochDay: Long = 0L,
    val endDateEpochDay: Long? = null,
)

data class RecurringUiState(
    val isLoading: Boolean = true,
    val currencyCode: String = "USD",
    val recurringTransactions: List<RecurringTransaction> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val showForm: Boolean = false,
    val form: RecurringFormState = RecurringFormState(),
    val todayEpochDay: Long = 0L,
) {
    val isEmpty: Boolean get() = !isLoading && recurringTransactions.isEmpty()
    val activeList: List<RecurringTransaction> get() = recurringTransactions.filter { it.active }
    val inactiveList: List<RecurringTransaction> get() = recurringTransactions.filter { !it.active }
}

@HiltViewModel
class RecurringViewModel @Inject constructor(
    observeRecurringTransactions: ObserveRecurringTransactionsUseCase,
    observeAccounts: ObserveAccountsUseCase,
    observeCategories: ObserveCategoriesUseCase,
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val recurringRepository: RecurringTransactionRepository,
    private val timeProvider: TimeProvider,
    private val idGenerator: IdGenerator,
) : ViewModel() {

    private val eventChannel = UiEventChannel()
    val events = eventChannel.events

    private val _formState = MutableStateFlow(RecurringFormState(nextOccurrenceEpochDay = timeProvider.today().toEpochDay()))
    private val _uiState = MutableStateFlow(RecurringUiState())

    val uiState: StateFlow<RecurringUiState> = combine(
        observeRecurringTransactions(),
        observeAccounts(),
        observeCategories(),
        observeUserPreferences(),
        _formState,
    ) { recurring, accounts, categories, prefs, form ->
        RecurringUiState(
            isLoading = false,
            currencyCode = prefs.currencyCode,
            recurringTransactions = recurring,
            accounts = accounts,
            categories = categories,
            showForm = _uiState.value.showForm,
            form = form,
            todayEpochDay = timeProvider.today().toEpochDay(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = RecurringUiState(),
    )

    fun onShowForm(show: Boolean) {
        if (!show) _formState.value = RecurringFormState(nextOccurrenceEpochDay = timeProvider.today().toEpochDay())
        _uiState.update { it.copy(showForm = show) }
    }

    fun onFormTypeChange(v: TransactionType) = _formState.update { it.copy(type = v, categoryId = null) }
    fun onFormAmountChange(v: String) = _formState.update { it.copy(amountInput = v.filter { c -> c.isDigit() || c == '.' }) }
    fun onFormAccountChange(v: String) = _formState.update { it.copy(accountId = v) }
    fun onFormCategoryChange(v: String?) = _formState.update { it.copy(categoryId = v) }
    fun onFormDescriptionChange(v: String) = _formState.update { it.copy(description = v) }
    fun onFormFrequencyChange(v: RecurrenceFrequency) = _formState.update { it.copy(frequency = v) }
    fun onFormNextOccurrenceChange(v: Long) = _formState.update { it.copy(nextOccurrenceEpochDay = v) }

    fun onSaveRecurring() {
        val form = _formState.value
        val amountMinor = form.amountInput.toMinorUnitsOrNull()
        if (amountMinor == null || amountMinor <= 0) {
            eventChannel.send(RecurringEvent.Error("Enter a valid amount"))
            return
        }
        val accountId = form.accountId
        if (accountId == null) {
            eventChannel.send(RecurringEvent.Error("Select an account"))
            return
        }
        viewModelScope.launch {
            val recurring = RecurringTransaction(
                id = idGenerator.newId(),
                type = form.type,
                amountMinor = amountMinor,
                accountId = accountId,
                categoryId = form.categoryId,
                destinationAccountId = null,
                description = form.description.ifBlank { null },
                frequency = form.frequency,
                nextOccurrenceEpochDay = form.nextOccurrenceEpochDay,
                endDateEpochDay = form.endDateEpochDay,
                active = true,
                createdAtEpochMillis = timeProvider.nowMillis(),
                updatedAtEpochMillis = timeProvider.nowMillis(),
            )
            recurringRepository.upsert(recurring)
            _formState.value = RecurringFormState(nextOccurrenceEpochDay = timeProvider.today().toEpochDay())
            _uiState.update { it.copy(showForm = false) }
            eventChannel.send(RecurringEvent.Saved)
        }
    }

    fun onDeleteRecurring(id: String) {
        viewModelScope.launch {
            recurringRepository.deleteById(id)
            eventChannel.send(RecurringEvent.Deleted)
        }
    }

    fun onToggleActive(id: String, active: Boolean) {
        viewModelScope.launch {
            if (active) recurringRepository.deactivate(id) else {
                val item = recurringRepository.getById(id) ?: return@launch
                recurringRepository.upsert(item.copy(active = true, updatedAtEpochMillis = timeProvider.nowMillis()))
            }
        }
    }
}
