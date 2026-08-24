package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/** Toggles Material You dynamic colors. */
class SetDynamicColorsUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(enabled: Boolean) =
        userPreferencesRepository.setDynamicColors(enabled)
}
