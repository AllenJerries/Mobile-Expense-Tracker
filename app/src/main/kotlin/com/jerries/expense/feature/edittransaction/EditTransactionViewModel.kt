package com.jerries.expense.feature.edittransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.AppError
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
import com.jerries.expense.domain.repository.TransactionRepository
import com.jerries.expense.domain.usecase.ObserveAccountsUseCase
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase
import com.jerries.expense.domain.usecase.UpdateTransactionUseCase
import com.jerries.expense.core.navigation.EditTransactionRoute
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

sealed interface EditTransactionEvent : UiEvent {
    data object Saved : EditTransactionEvent
    data class SaveFailed(val message: String) : EditTransactionEvent
    data object Loaded : EditTransactionEvent
    data object NotFound : EditTransactionEvent
}

data class EditTransactionUiState(
    val transactionId: String = "",
    val amountInput: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val accountId: String? = null,
    val categoryId: String? = null,
    val destinationAccountId: String? = null,
    val title: String = "",
    val note: String = "",
    val dateEpochDay: Long = 0L,
    val timeHour: Int = 12,
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
class EditTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeAccounts: ObserveAccountsUseCase,
    observeCategories: ObserveCategoriesUseCase,
    private val updateTransaction: UpdateTransactionUseCase,
    private val transactionRepository: TransactionRepository,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    private val transactionId: String = savedStateHandle[EditTransactionRoute::transactionId.name]
        ?: ""

    private val eventChannel = UiEventChannel()
    val events = eventChannel.events

    private val _formState = MutableStateFlow(EditTransactionUiState())
    private val formState = _formState.asStateFlow()

    val uiState: StateFlow<EditTransactionUiState> =
        combine(formState, observeAccounts(), observeCategories()) { form, accounts, categories ->
            form.copy(
                accounts = accounts,
                currencyCode = accounts.firstOrNull { it.id == form.accountId }?.currencyCode
                    ?: accounts.firstOrNull()?.currencyCode
                    ?: form.currencyCode,
                categories = categories,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = _formState.value,
        )

    init {
        loadTransaction()
    }

    private fun loadTransaction() {
        if (transactionId.isBlank()) {
            eventChannel.send(EditTransactionEvent.NotFound)
            return
        }
        viewModelScope.launch {
            val transaction = transactionRepository.getById(transactionId)
            if (transaction == null) {
                eventChannel.send(EditTransactionEvent.NotFound)
                return@launch
            }
            val timeMillis = transaction.createdAtEpochMillis
            val hour = ((timeMillis % MILLIS_PER_DAY) / 3_600_000L).toInt().coerceIn(0, 23)
            val minute = (((timeMillis % MILLIS_PER_DAY) % 3_600_000L) / 60_000L).toInt().coerceIn(0, 59)
            _formState.update {
                it.copy(
                    transactionId = transaction.id,
                    amountInput = formatAmount(transaction.amountMinor),
                    type = transaction.type,
                    accountId = transaction.accountId,
                    categoryId = transaction.categoryId,
                    destinationAccountId = transaction.destinationAccountId,
                    title = transaction.title ?: "",
                    note = transaction.note ?: "",
                    dateEpochDay = transaction.dateEpochDay,
                    timeHour = hour,
                    timeMinute = minute,
                    paymentMethod = transaction.paymentMethod,
                    attachmentUri = transaction.attachmentUri,
                    isLoading = false,
                )
            }
            eventChannel.send(EditTransactionEvent.Loaded)
        }
    }

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
            val result = updateTransaction(
                Transaction(
                    id = current.transactionId,
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
                is Result.Success -> eventChannel.send(EditTransactionEvent.Saved)
                is Result.Failure -> eventChannel.send(
                    EditTransactionEvent.SaveFailed(
                        message = (result.error as? AppError.Validation)?.message ?: GENERIC_ERROR,
                    ),
                )
            }
        }
    }

    private fun formatAmount(amountMinor: Long): String {
        val major = amountMinor / 100.0
        return if (major == major.toLong().toDouble()) {
            major.toLong().toString()
        } else {
            String.format("%.2f", major)
        }
    }

    private fun filterToAmount(input: String): String =
        input.filterIndexed { index, char ->
            char.isDigit() || (char == '.' && input.indexOf('.') == index)
        }.take(MAX_AMOUNT_LENGTH)

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
        private const val MAX_AMOUNT_LENGTH = 15
        private const val GENERIC_ERROR = "Could not update transaction"
        private const val MILLIS_PER_DAY = 86_400_000L
    }
}
