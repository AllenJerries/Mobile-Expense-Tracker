package com.jerries.expense.domain.usecase

import com.jerries.expense.data.local.dao.DailyTotalProjection
import com.jerries.expense.data.local.dao.TransactionDao
import com.jerries.expense.data.repository.toDomain
import com.jerries.expense.domain.model.DailyTotal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveDailyTotalsUseCase @Inject constructor(
    private val transactionDao: TransactionDao,
) {
    operator fun invoke(
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<List<DailyTotal>> =
        transactionDao.observeDailyTotals(startEpochDay, endEpochDay)
            .map { list -> list.map(DailyTotalProjection::toDomain) }
}
