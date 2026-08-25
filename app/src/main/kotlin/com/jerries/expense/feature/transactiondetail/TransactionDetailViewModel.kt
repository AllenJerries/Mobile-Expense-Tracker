package com.jerries.expense.feature.transactiondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.core.navigation.TransactionDetailRoute
import com.jerries.expense.core.ui.UiEvent
import com.jerries.expense.core.ui.UiEventChannel
import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.model.TransactionType
import com.jerries.expense.domain.repository.TransactionRepository
import com.jerries.expense.domain.usecase.DeleteTransactionUseCase
import com.jerries.expense.domain.usecase.ObserveAccountsUseCase
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionDetailItem(
    val label: String,
    val value: String,
)

sealed interface TransactionDetailEvent : UiEvent {
    data object Deleted : TransactionDetailEvent
    data object NotFound : TransactionDetailEvent
    data object NavigateToEdit : TransactionDetailEvent
}

data class TransactionDetailUiState(
    val isLoading: Boolean = true,
    val transaction: Transaction? = null,
    val title: String = "",
    val amountText: String = "",
    val typeText: String = "",
    val accountName: String = "",
    val categoryName: String = "",
    val destinationAccountName: String = "",
    val dateText: String = "",
    val timeText: String = "",
    val paymentMethod: String = "",
    val note: String = "",
    val createdText: String = "",
    val updatedText: String = "",
    val hasAttachment: Boolean = false,
    val type: TransactionType = TransactionType.EXPENSE,
    val currencyCode: String = "USD",
) {
    val isEmpty: Boolean get() = !isLoading && transaction == null
}

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    observeAccounts: ObserveAccountsUseCase,
    observeCategories: ObserveCategoriesUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    private val transactionId: String = savedStateHandle[TransactionDetailRoute::transactionId.name]
        ?: ""

    private val eventChannel = UiEventChannel()
    val events = eventChannel.events

    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState.asStateFlow()

    private var accounts: List<Account> = emptyList()
    private var categories: List<Category> = emptyList()

    init {
        viewModelScope.launch {
            observeAccounts().collect { list -> accounts = list }
        }
        viewModelScope.launch {
            observeCategories().collect { list -> categories = list }
        }
        loadTransaction()
    }

    private fun loadTransaction() {
        if (transactionId.isBlank()) {
            eventChannel.send(TransactionDetailEvent.NotFound)
            return
        }
        viewModelScope.launch {
            val transaction = transactionRepository.getById(transactionId)
            if (transaction == null) {
                eventChannel.send(TransactionDetailEvent.NotFound)
                return@launch
            }
            updateState(transaction)
        }
    }

    private fun updateState(transaction: Transaction) {
        val accountsById = accounts.associateBy(Account::id)
        val categoriesById = categories.associateBy(Category::id)
        val account = accountsById[transaction.accountId]
        val category = transaction.categoryId?.let(categoriesById::get)
        val destAccount = transaction.destinationAccountId?.let(accountsById::get)

        val displayTitle = transaction.title?.takeIf { it.isNotBlank() }
            ?: transaction.note?.takeIf { it.isNotBlank() }
            ?: category?.name
            ?: transaction.type.name.replaceFirstChar { it.uppercase() }

        val timeMillis = transaction.createdAtEpochMillis
        val hour = ((timeMillis % 86_400_000L) / 3_600_000L).toInt().coerceIn(0, 23)
        val minute = (((timeMillis % 86_400_000L) % 3_600_000L) / 60_000L).toInt().coerceIn(0, 59)

        _uiState.update {
            it.copy(
                isLoading = false,
                transaction = transaction,
                title = displayTitle,
                amountText = formatAmount(transaction.amountMinor),
                typeText = transaction.type.name.replaceFirstChar { c -> c.uppercase() },
                accountName = account?.name ?: "Unknown",
                categoryName = category?.name ?: "—",
                destinationAccountName = destAccount?.name ?: "—",
                dateText = java.time.LocalDate.ofEpochDay(transaction.dateEpochDay)
                    .format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                timeText = String.format("%02d:%02d", hour, minute),
                paymentMethod = transaction.paymentMethod ?: "—",
                note = transaction.note ?: "",
                createdText = formatTimestamp(transaction.createdAtEpochMillis),
                updatedText = formatTimestamp(transaction.updatedAtEpochMillis),
                hasAttachment = transaction.attachmentUri != null,
                type = transaction.type,
                currencyCode = account?.currencyCode ?: "USD",
            )
        }
    }

    fun deleteTransaction() {
        val transaction = _uiState.value.transaction ?: return
        viewModelScope.launch {
            deleteTransactionUseCase(transaction.id)
            eventChannel.send(TransactionDetailEvent.Deleted)
        }
    }

    fun navigateToEdit() {
        eventChannel.send(TransactionDetailEvent.NavigateToEdit)
    }

    private fun formatAmount(amountMinor: Long): String {
        val major = amountMinor / 100.0
        return if (major == major.toLong().toDouble()) {
            major.toLong().toString()
        } else {
            String.format("%.2f", major)
        }
    }

    private fun formatTimestamp(millis: Long): String {
        val instant = java.time.Instant.ofEpochMilli(millis)
        val ldt = instant.atZone(timeProvider.zone()).toLocalDateTime()
        return ldt.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    }
}
