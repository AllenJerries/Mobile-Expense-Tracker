package com.jerries.expense.domain.usecase

import com.jerries.expense.data.local.dao.SpendingByCategoryProjection
import com.jerries.expense.data.local.dao.TransactionDao
import com.jerries.expense.data.repository.toDomain
import com.jerries.expense.domain.model.SpendingByCategory
import com.jerries.expense.domain.model.TransactionType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveExpensesByCategoryUseCase @Inject constructor(
    private val transactionDao: TransactionDao,
) {
    operator fun invoke(
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<List<SpendingByCategory>> =
        transactionDao.observeByCategory(
            type = TransactionType.EXPENSE.name,
            startEpochDay = startEpochDay,
            endEpochDay = endEpochDay,
        ).map { list -> list.map(SpendingByCategoryProjection::toDomain) }
}
