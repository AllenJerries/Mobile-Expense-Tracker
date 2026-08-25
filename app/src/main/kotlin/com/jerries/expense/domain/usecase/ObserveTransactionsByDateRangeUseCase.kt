package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTransactionsByDateRangeUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(startEpochDay: Long, endEpochDay: Long): Flow<List<Transaction>> =
        transactionRepository.observeByDateRange(startEpochDay, endEpochDay)
}
