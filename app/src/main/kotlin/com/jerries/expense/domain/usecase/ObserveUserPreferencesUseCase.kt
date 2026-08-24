package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Streams the user's app-level preferences. */
class ObserveUserPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<AppPreferences> = userPreferencesRepository.preferences
}
