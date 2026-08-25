package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.model.SavingsGoal
import com.jerries.expense.domain.repository.SavingsGoalRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveSavingsGoalsUseCase @Inject constructor(
    private val savingsGoalRepository: SavingsGoalRepository,
) {
    operator fun invoke(onlyActive: Boolean = false): Flow<List<SavingsGoal>> =
        if (onlyActive) savingsGoalRepository.observeActive()
        else savingsGoalRepository.observeAll()
}
