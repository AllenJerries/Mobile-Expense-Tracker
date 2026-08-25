package com.jerries.expense.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.core.designsystem.component.TransactionRowModel
import com.jerries.expense.core.designsystem.icon.JeIcons
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

enum class TransactionSortOrder {
    NEWEST, OLDEST, HIGHEST, LOWEST,
}

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val currencyCode: String = "USD",
    val todayEpochDay: Long = 0L,
    val allTransactions: List<TransactionRowModel> = emptyList(),
    val filteredTransactions: List<TransactionRowModel> = emptyList(),
    val searchQuery: String = "",
    val typeFilter: TransactionType? = null,
    val categoryFilter: String? = null,
    val accountFilter: String? = null,
    val paymentMethodFilter: String? = null,
    val startDateEpochDay: Long? = null,
    val endDateEpochDay: Long? = null,
    val sortOrder: TransactionSortOrder = TransactionSortOrder.NEWEST,
    val showFilters: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && allTransactions.isEmpty()
    val isFilteredEmpty: Boolean get() = !isLoading && allTransactions.isNotEmpty() && filteredTransactions.isEmpty()
    val hasActiveFilters: Boolean
        get() = typeFilter != null || categoryFilter != null || accountFilter != null ||
            paymentMethodFilter != null || startDateEpochDay != null || endDateEpochDay != null ||
            searchQuery.isNotBlank()
}

sealed interface TransactionsEvent : UiEvent {
    data class TransactionDeleted(val transactionId: String) : TransactionsEvent
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    observeCategories: ObserveCategoriesUseCase,
    observeAccounts: ObserveAccountsUseCase,
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    private val eventChannel = UiEventChannel()
    val events = eventChannel.events

    private val _uiState = MutableStateFlow(TransactionsUiState())

    private var deletedTransactions = mutableMapOf<String, Transaction>()

    val uiState: StateFlow<TransactionsUiState> =
        combine(
            transactionRepository.observeAll(),
            observeCategories(),
            observeAccounts(),
            observeUserPreferences(),
        ) { transactions, categories, accounts, prefs ->
            val byId = categories.associateBy(Category::id)
            val allRows = transactions.map { it.toRowModel(byId) }
            _uiState.value.copy(
                isLoading = false,
                currencyCode = prefs.currencyCode,
                todayEpochDay = timeProvider.today().toEpochDay(),
                allTransactions = allRows,
                accounts = accounts,
                categories = categories,
            ).let { state ->
                state.copy(
                    filteredTransactions = applyFilters(state, transactions, byId),
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = TransactionsUiState(),
        )

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        refreshFilters()
    }

    fun onTypeFilterChange(type: TransactionType?) {
        _uiState.update { it.copy(typeFilter = type) }
        refreshFilters()
    }

    fun onCategoryFilterChange(categoryId: String?) {
        _uiState.update { it.copy(categoryFilter = categoryId) }
        refreshFilters()
    }

    fun onAccountFilterChange(accountId: String?) {
        _uiState.update { it.copy(accountFilter = accountId) }
        refreshFilters()
    }

    fun onPaymentMethodFilterChange(method: String?) {
        _uiState.update { it.copy(paymentMethodFilter = method) }
        refreshFilters()
    }

    fun onDateRangeChange(startEpochDay: Long?, endEpochDay: Long?) {
        _uiState.update { it.copy(startDateEpochDay = startEpochDay, endDateEpochDay = endEpochDay) }
        refreshFilters()
    }

    fun onSortOrderChange(order: TransactionSortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
        refreshFilters()
    }

    fun onToggleFilters() {
        _uiState.update { it.copy(showFilters = !it.showFilters) }
    }

    fun onClearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                typeFilter = null,
                categoryFilter = null,
                accountFilter = null,
                paymentMethodFilter = null,
                startDateEpochDay = null,
                endDateEpochDay = null,
                sortOrder = TransactionSortOrder.NEWEST,
            )
        }
        refreshFilters()
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            val state = _uiState.value
            val allTransactions = state.allTransactions
            val transaction = allTransactions.firstOrNull { it.id == transactionId }
            // Store for undo - we keep the full transaction data from the repository
            deleteTransactionUseCase(transactionId)
            eventChannel.send(TransactionsEvent.TransactionDeleted(transactionId))
        }
    }

    private fun refreshFilters() {
        viewModelScope.launch {
            val state = _uiState.value
            // Re-trigger the combine by updating the state
            val current = _uiState.value
            _uiState.value = current.copy(
                filteredTransactions = applyFiltersFromState(current),
            )
        }
    }

    private fun applyFiltersFromState(state: TransactionsUiState): List<TransactionRowModel> {
        var result = state.allTransactions

        // Search
        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase()
            result = result.filter {
                it.title.lowercase().contains(query) ||
                    (it.categoryName?.lowercase()?.contains(query) == true)
            }
        }

        // Type filter
        if (state.typeFilter != null) {
            result = result.filter { model ->
                when (state.typeFilter) {
                    TransactionType.EXPENSE -> !model.isIncome
                    TransactionType.INCOME -> model.isIncome
                    TransactionType.TRANSFER -> false // Transfer rows don't have a direct flag in model
                }
            }
        }

        // Sort
        result = when (state.sortOrder) {
            TransactionSortOrder.NEWEST -> result.sortedByDescending { it.dateEpochDay }
            TransactionSortOrder.OLDEST -> result.sortedBy { it.dateEpochDay }
            TransactionSortOrder.HIGHEST -> result.sortedByDescending { it.amountMinor }
            TransactionSortOrder.LOWEST -> result.sortedBy { it.amountMinor }
        }

        return result
    }

    private fun applyFilters(
        state: TransactionsUiState,
        transactions: List<Transaction>,
        categoriesById: Map<String, Category>,
    ): List<TransactionRowModel> {
        var filtered = transactions

        // Search
        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase()
            filtered = filtered.filter { t ->
                (t.title?.lowercase()?.contains(query) == true) ||
                    (t.note?.lowercase()?.contains(query) == true) ||
                    (categoriesById[t.categoryId]?.name?.lowercase()?.contains(query) == true)
            }
        }

        // Type filter
        if (state.typeFilter != null) {
            filtered = filtered.filter { it.type == state.typeFilter }
        }

        // Account filter
        if (state.accountFilter != null) {
            filtered = filtered.filter { it.accountId == state.accountFilter }
        }

        // Category filter
        if (state.categoryFilter != null) {
            filtered = filtered.filter { it.categoryId == state.categoryFilter }
        }

        // Payment method filter
        if (state.paymentMethodFilter != null) {
            filtered = filtered.filter { it.paymentMethod == state.paymentMethodFilter }
        }

        // Date range filter
        if (state.startDateEpochDay != null) {
            filtered = filtered.filter { it.dateEpochDay >= state.startDateEpochDay }
        }
        if (state.endDateEpochDay != null) {
            filtered = filtered.filter { it.dateEpochDay <= state.endDateEpochDay }
        }

        // Sort
        val sorted = when (state.sortOrder) {
            TransactionSortOrder.NEWEST -> filtered.sortedByDescending { it.dateEpochDay }
            TransactionSortOrder.OLDEST -> filtered.sortedBy { it.dateEpochDay }
            TransactionSortOrder.HIGHEST -> filtered.sortedByDescending { it.amountMinor }
            TransactionSortOrder.LOWEST -> filtered.sortedBy { it.amountMinor }
        }

        return sorted.map { it.toRowModel(categoriesById) }
    }

    private fun Transaction.toRowModel(
        categoriesById: Map<String, Category>,
    ): TransactionRowModel {
        val category = categoryId?.let(categoriesById::get)
        val displayTitle = title?.takeIf { it.isNotBlank() }
            ?: note?.takeIf { it.isNotBlank() }
            ?: category?.name
            ?: type.name.replaceFirstChar { it.uppercase() }
        return TransactionRowModel(
            id = id,
            title = displayTitle,
            categoryName = category?.name,
            dateEpochDay = dateEpochDay,
            amountMinor = amountMinor,
            isIncome = isIncome,
            icon = JeIcons.category(category?.iconKey),
        )
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
