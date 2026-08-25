package com.jerries.expense.domain.usecase

import com.jerries.expense.data.local.dao.TransactionDao
import com.jerries.expense.domain.model.Budget
import com.jerries.expense.domain.repository.BudgetRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class BudgetSpending(
    val budget: Budget,
    val spentMinor: Long,
    val limitMinor: Long,
    val percentage: Double,
    val exceeded: Boolean,
)

class ObserveBudgetSpendingUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionDao: TransactionDao,
) {
    operator fun invoke(epochDay: Long): Flow<List<BudgetSpending>> {
        return budgetRepository.observeActive(epochDay).combine(
            transactionDao.observeAll(),
        ) { budgets, transactions ->
            budgets.map { budget ->
                val spent = transactions
                    .filter { t ->
                        t.isDeleted.not() &&
                            t.type == "EXPENSE" &&
                            t.dateEpochDay >= budget.startEpochDay &&
                            t.dateEpochDay <= budget.endEpochDay &&
                            (budget.categoryId == null || t.categoryId == budget.categoryId) &&
                            (budget.accountId == null || t.accountId == budget.accountId)
                    }
                    .sumOf { it.amountMinor }
                val pct = if (budget.limitMinor > 0) {
                    spent.toDouble() / budget.limitMinor.toDouble()
                } else {
                    0.0
                }
                BudgetSpending(
                    budget = budget,
                    spentMinor = spent,
                    limitMinor = budget.limitMinor,
                    percentage = pct,
                    exceeded = pct > budget.alertThreshold,
                )
            }
        }
    }
}
