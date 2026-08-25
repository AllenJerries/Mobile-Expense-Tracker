package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.model.SavingsGoal
import com.jerries.expense.domain.repository.SavingsGoalRepository
import javax.inject.Inject

class UpdateSavingsGoalUseCase @Inject constructor(
    private val savingsGoalRepository: SavingsGoalRepository,
) {
    suspend operator fun invoke(goal: SavingsGoal) {
        savingsGoalRepository.upsert(goal)
    }

    suspend fun markCompleted(id: String) {
        val existing = savingsGoalRepository.getById(id) ?: return
        savingsGoalRepository.upsert(existing.copy(completed = true))
    }

    suspend fun addAmount(id: String, amountMinor: Long) {
        val existing = savingsGoalRepository.getById(id) ?: return
        val newSaved = (existing.savedMinor + amountMinor).coerceAtMost(existing.targetMinor)
        savingsGoalRepository.upsert(
            existing.copy(
                savedMinor = newSaved,
                completed = newSaved >= existing.targetMinor,
            ),
        )
    }
}
