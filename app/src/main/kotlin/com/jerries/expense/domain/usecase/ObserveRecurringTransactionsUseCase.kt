package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.model.RecurringTransaction
import com.jerries.expense.domain.repository.RecurringTransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveRecurringTransactionsUseCase @Inject constructor(
    private val recurringTransactionRepository: RecurringTransactionRepository,
) {
    operator fun invoke(onlyActive: Boolean = true): Flow<List<RecurringTransaction>> =
        if (onlyActive) recurringTransactionRepository.observeActive()
        else recurringTransactionRepository.observeAll()
}
