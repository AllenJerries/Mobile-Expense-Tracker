package com.jerries.expense.domain.usecase

import com.jerries.expense.data.local.dao.DailyTotalProjection
import com.jerries.expense.data.local.dao.TransactionDao
import com.jerries.expense.domain.model.SpendingTrend
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveSpendingTrendsUseCase @Inject constructor(
    private val transactionDao: TransactionDao,
) {
    operator fun invoke(
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<List<SpendingTrend>> =
        transactionDao.observeDailyTotals(startEpochDay, endEpochDay).map { dailyTotals ->
            var cumulative = 0L
            dailyTotals.map { daily ->
                cumulative += daily.expenseMinor
                SpendingTrend(
                    dateEpochDay = daily.dateEpochDay,
                    amountMinor = cumulative,
                )
            }
        }
}
