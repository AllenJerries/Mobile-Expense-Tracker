package com.jerries.expense.feature.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.AppError
import com.jerries.expense.core.common.IdGenerator
import com.jerries.expense.core.common.Result
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.core.ui.UiEvent
import com.jerries.expense.core.ui.UiEventChannel
import com.jerries.expense.core.util.toMinorUnitsOrNull
import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.CategoryKind
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.model.TransactionType
import com.jerries.expense.domain.usecase.AddTransactionUseCase
import com.jerries.expense.domain.usecase.ObserveAccountsUseCase
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase
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

/** One-shot events the Add Transaction screen reacts to. */
sealed interface AddTransactionEvent : UiEvent {
    data object Saved : AddTransactionEvent
    data class SaveFailed(val message: String) : AddTransactionEvent
}

data class AddTransactionUiState(
    val amountInput: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val accountId: String? = null,
    val categoryId: String? = null,
    val note: String = "",
    val dateEpochDay: Long = 0L,
    val currencyCode: String = "USD",
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val amountError: Boolean = false,
    val accountError: Boolean = false,
    val categoryError: Boolean = false,
) {
    val canSave: Boolean
        get() = !isLoading && !isSaving && amountInput.isNotBlank()
}

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    observeAccounts: ObserveAccountsUseCase,
    observeCategories: ObserveCategoriesUseCase,
    private val addTransaction: AddTransactionUseCase,
    private val timeProvider: TimeProvider,
    private val idGenerator: IdGenerator,
) : ViewModel() {

    private val eventChannel = UiEventChannel()
    val events = eventChannel.events

    private val _formState = MutableStateFlow(
        AddTransactionUiState(dateEpochDay = timeProvider.today().toEpochDay()),
    )
    private val formState = _formState.asStateFlow()

    val uiState: StateFlow<AddTransactionUiState> =
        combine(formState, observeAccounts(), observeCategories()) { form, accounts, categories ->
            form.copy(
                isLoading = false,
                accounts = accounts,
                currencyCode = accounts.firstOrNull()?.currencyCode ?: form.currencyCode,
                categories = categories.filter {
                    it.kind == form.type.toCategoryKind()
                },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = _formState.value,
        )

    fun onAmountChange(input: String) = _formState.update {
        it.copy(amountInput = filterToAmount(input), amountError = false)
    }

    fun onTypeChange(type: TransactionType) = _formState.update {
        val kind = type.toCategoryKind()
        it.copy(
            type = type,
            categoryId = null,
            categories = it.categories.filter { category -> category.kind == kind },
            categoryError = false,
        )
    }

    fun onAccountChange(accountId: String) = _formState.update {
        it.copy(accountId = accountId, accountError = false)
    }

    fun onCategoryChange(categoryId: String) = _formState.update {
        it.copy(categoryId = categoryId, categoryError = false)
    }

    fun onNoteChange(note: String) = _formState.update { it.copy(note = note) }

    fun onDateChange(epochDay: Long) = _formState.update { it.copy(dateEpochDay = epochDay) }

    fun save() {
        val current = uiState.value
        val amountMinor = current.amountInput.toMinorUnitsOrNull()

        if (amountMinor == null || amountMinor <= 0L) {
            _formState.update { it.copy(amountError = true) }
            return
        }
        val accountId = current.accountId
        if (accountId == null) {
            _formState.update { it.copy(accountError = true) }
            return
        }
        val categoryId = current.categoryId
        if (current.categories.isNotEmpty() && categoryId == null) {
            _formState.update { it.copy(categoryError = true) }
            return
        }

        _formState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val result = addTransaction(
                Transaction(
                    id = idGenerator.newId(),
                    accountId = accountId,
                    categoryId = categoryId,
                    amountMinor = amountMinor,
                    type = current.type,
                    dateEpochDay = current.dateEpochDay,
                    note = current.note.trim().takeIf(String::isNotEmpty),
                    createdAtEpochMillis = timeProvider.nowMillis(),
                ),
            )
            _formState.update { it.copy(isSaving = false) }
            when (result) {
                is Result.Success -> eventChannel.send(AddTransactionEvent.Saved)

                is Result.Failure -> eventChannel.send(
                    AddTransactionEvent.SaveFailed(
                        message = (result.error as? AppError.Validation)?.message ?: GENERIC_ERROR,
                    ),
                )
            }
        }
    }

    private fun filterToAmount(input: String): String =
        input.filterIndexed { index, char ->
            char.isDigit() || (char == '.' && input.indexOf('.') == index)
        }.take(MAX_AMOUNT_LENGTH)

    private fun TransactionType.toCategoryKind(): CategoryKind = when (this) {
        TransactionType.EXPENSE -> CategoryKind.EXPENSE
        TransactionType.INCOME -> CategoryKind.INCOME
        TransactionType.TRANSFER -> CategoryKind.EXPENSE
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
        private const val MAX_AMOUNT_LENGTH = 15
        private const val GENERIC_ERROR = "Could not save transaction"
    }
}
