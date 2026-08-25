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

sealed interface AddTransactionEvent : UiEvent {
    data object Saved : AddTransactionEvent
    data class SaveFailed(val message: String) : AddTransactionEvent
}

data class AddTransactionUiState(
    val amountInput: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val accountId: String? = null,
    val categoryId: String? = null,
    val destinationAccountId: String? = null,
    val title: String = "",
    val note: String = "",
    val dateEpochDay: Long = 0L,
    val timeHour: Int = 0,
    val timeMinute: Int = 0,
    val paymentMethod: String? = null,
    val attachmentUri: String? = null,
    val currencyCode: String = "USD",
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val amountError: Boolean = false,
    val accountError: Boolean = false,
    val categoryError: Boolean = false,
    val destinationAccountError: Boolean = false,
) {
    val canSave: Boolean
        get() = !isLoading && !isSaving && amountInput.isNotBlank()

    val paymentMethods: List<String>
        get() = listOf("Cash", "Card", "Bank transfer", "UPI", "Check", "Other")

    val filteredCategories: List<Category>
        get() = when (type) {
            TransactionType.EXPENSE -> categories.filter { it.kind == CategoryKind.EXPENSE }
            TransactionType.INCOME -> categories.filter { it.kind == CategoryKind.INCOME }
            TransactionType.TRANSFER -> emptyList()
        }
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

    private val now = timeProvider.today()
    private val currentTime = java.time.LocalTime.now()

    private val _formState = MutableStateFlow(
        AddTransactionUiState(
            dateEpochDay = now.toEpochDay(),
            timeHour = currentTime.hour,
            timeMinute = currentTime.minute,
        ),
    )
    private val formState = _formState.asStateFlow()

    val uiState: StateFlow<AddTransactionUiState> =
        combine(formState, observeAccounts(), observeCategories()) { form, accounts, categories ->
            val autoSelectAccount = if (form.accountId == null && accounts.size == 1) {
                accounts.first().id
            } else {
                form.accountId
            }
            form.copy(
                isLoading = false,
                accounts = accounts,
                accountId = autoSelectAccount,
                currencyCode = accounts.firstOrNull { it.id == autoSelectAccount }?.currencyCode
                    ?: accounts.firstOrNull()?.currencyCode
                    ?: form.currencyCode,
                categories = categories,
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
        it.copy(
            type = type,
            categoryId = null,
            destinationAccountId = null,
            categoryError = false,
            destinationAccountError = false,
        )
    }

    fun onAccountChange(accountId: String) = _formState.update {
        val account = it.accounts.firstOrNull { a -> a.id == accountId }
        it.copy(
            accountId = accountId,
            accountError = false,
            currencyCode = account?.currencyCode ?: it.currencyCode,
        )
    }

    fun onCategoryChange(categoryId: String) = _formState.update {
        it.copy(categoryId = categoryId, categoryError = false)
    }

    fun onDestinationAccountChange(accountId: String) = _formState.update {
        it.copy(destinationAccountId = accountId, destinationAccountError = false)
    }

    fun onTitleChange(title: String) = _formState.update { it.copy(title = title) }

    fun onNoteChange(note: String) = _formState.update { it.copy(note = note) }

    fun onDateChange(epochDay: Long) = _formState.update { it.copy(dateEpochDay = epochDay) }

    fun onTimeChange(hour: Int, minute: Int) = _formState.update {
        it.copy(timeHour = hour, timeMinute = minute)
    }

    fun onPaymentMethodChange(method: String?) = _formState.update {
        it.copy(paymentMethod = method)
    }

    fun onAttachmentChange(uri: String?) = _formState.update {
        it.copy(attachmentUri = uri)
    }

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
        if (current.type == TransactionType.TRANSFER) {
            val destId = current.destinationAccountId
            if (destId == null || destId == accountId) {
                _formState.update { it.copy(destinationAccountError = true) }
                return
            }
        } else {
            val categoryId = current.categoryId
            if (current.filteredCategories.isNotEmpty() && categoryId == null) {
                _formState.update { it.copy(categoryError = true) }
                return
            }
        }

        _formState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val nowMillis = timeProvider.nowMillis()
            val dateMillis = current.dateEpochDay * MILLIS_PER_DAY +
                current.timeHour * 3_600_000L + current.timeMinute * 60_000L
            val result = addTransaction(
                Transaction(
                    id = idGenerator.newId(),
                    accountId = accountId,
                    categoryId = current.categoryId,
                    amountMinor = amountMinor,
                    type = current.type,
                    dateEpochDay = current.dateEpochDay,
                    title = current.title.trim().takeIf(String::isNotEmpty),
                    note = current.note.trim().takeIf(String::isNotEmpty),
                    createdAtEpochMillis = nowMillis,
                    updatedAtEpochMillis = nowMillis,
                    paymentMethod = current.paymentMethod,
                    destinationAccountId = current.destinationAccountId,
                    recurringTransactionId = null,
                    attachmentUri = current.attachmentUri,
                    isDeleted = false,
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

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
        private const val MAX_AMOUNT_LENGTH = 15
        private const val GENERIC_ERROR = "Could not save transaction"
        private const val MILLIS_PER_DAY = 86_400_000L
    }
}
