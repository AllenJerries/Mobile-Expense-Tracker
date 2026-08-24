package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/** Persists the display currency (ISO-4217 code). */
class SetCurrencyCodeUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(code: String) = userPreferencesRepository.setCurrencyCode(code)
}
