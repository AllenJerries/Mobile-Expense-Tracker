package com.jerries.expense.domain.usecase

import com.jerries.expense.data.local.dao.TransactionDao
import com.jerries.expense.domain.model.DailyTotal
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveMonthlyTotalsUseCase @Inject constructor(
    private val transactionDao: TransactionDao,
) {
    operator fun invoke(
        yearMonth: YearMonth,
    ): Flow<Pair<Long, Long>> {
        val startEpochDay = yearMonth.atDay(1).toEpochDay()
        val endEpochDay = yearMonth.atEndOfMonth().toEpochDay()
        return transactionDao.observeDailyTotals(startEpochDay, endEpochDay).map { dailyTotals ->
            val totalIncome = dailyTotals.sumOf { it.incomeMinor }
            val totalExpense = dailyTotals.sumOf { it.expenseMinor }
            totalIncome to totalExpense
        }
    }

    fun observeForRange(
        startMonth: YearMonth,
        endMonth: YearMonth,
    ): Flow<List<Pair<YearMonth, Pair<Long, Long>>>> {
        val startEpochDay = startMonth.atDay(1).toEpochDay()
        val endEpochDay = endMonth.atEndOfMonth().toEpochDay()
        return transactionDao.observeDailyTotals(startEpochDay, endEpochDay).map { dailyTotals ->
            dailyTotals.groupBy {
                YearMonth.from(LocalDate.ofEpochDay(it.dateEpochDay))
            }.map { (month, totals) ->
                month to (totals.sumOf { it.incomeMinor } to totals.sumOf { it.expenseMinor })
            }.sortedBy { it.first }
        }
    }
}
