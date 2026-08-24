package com.jerries.expense.domain.repository

import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.model.ThemeSetting
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferences: Flow<AppPreferences>

    suspend fun setTheme(theme: ThemeSetting)

    suspend fun setDynamicColors(enabled: Boolean)

    suspend fun setCurrencyCode(code: String)

    suspend fun markSeeded()
}
