package com.jerries.expense.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.core.designsystem.component.TransactionRowModel
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase
import com.jerries.expense.domain.usecase.ObserveRecentTransactionsUseCase
import com.jerries.expense.domain.usecase.ObserveTotalBalanceUseCase
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val isLoading: Boolean = true,
    val totalBalanceMinor: Long = 0L,
    val currencyCode: String = "USD",
    val todayEpochDay: Long = 0L,
    val recentTransactions: List<TransactionRowModel> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && recentTransactions.isEmpty()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeTotalBalance: ObserveTotalBalanceUseCase,
    observeRecentTransactions: ObserveRecentTransactionsUseCase,
    observeCategories: ObserveCategoriesUseCase,
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        combine(
            observeTotalBalance(),
            observeRecentTransactions(RECENT_LIMIT),
            observeCategories(),
            observeUserPreferences(),
        ) { balance, transactions, categories, prefs ->
            DashboardUiState(
                isLoading = false,
                totalBalanceMinor = balance,
                currencyCode = prefs.currencyCode,
                todayEpochDay = timeProvider.today().toEpochDay(),
                recentTransactions = transactions.map { it.toRowModel(categoriesById(categories)) },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DashboardUiState(),
        )

    private fun categoriesById(categories: List<Category>) =
        categories.associateBy(Category::id)

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
        private const val RECENT_LIMIT = 10
    }
}
