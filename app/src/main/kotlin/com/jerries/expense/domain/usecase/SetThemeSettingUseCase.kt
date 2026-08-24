package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.model.ThemeSetting
import com.jerries.expense.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/** Persists the user's theme selection. */
class SetThemeSettingUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(theme: ThemeSetting) = userPreferencesRepository.setTheme(theme)
}
