package com.jerries.expense.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.core.designsystem.component.TransactionRowModel
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.repository.TransactionRepository
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val currencyCode: String = "USD",
    val todayEpochDay: Long = 0L,
    val transactions: List<TransactionRowModel> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && transactions.isEmpty()
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    observeCategories: ObserveCategoriesUseCase,
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    val uiState: StateFlow<TransactionsUiState> =
        combine(
            transactionRepository.observeAll(),
            observeCategories(),
            observeUserPreferences(),
        ) { transactions, categories, prefs ->
            val byId = categories.associateBy(Category::id)
            TransactionsUiState(
                isLoading = false,
                currencyCode = prefs.currencyCode,
                todayEpochDay = timeProvider.today().toEpochDay(),
                transactions = transactions.map { it.toRowModel(byId) },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = TransactionsUiState(),
        )

    private fun Transaction.toRowModel(
        categoriesById: Map<String, Category>,
    ): TransactionRowModel {
        val category = categoryId?.let(categoriesById::get)
        return TransactionRowModel(
            id = id,
            title = note?.takeIf { it.isNotBlank() }
                ?: category?.name
                ?: type.name.replaceFirstChar { it.uppercase() },
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
