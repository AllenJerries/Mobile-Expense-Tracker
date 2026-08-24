package com.jerries.expense.data.repository

import com.jerries.expense.data.preferences.UserPreferencesDataSource
import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.model.ThemeSetting
import com.jerries.expense.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class UserDataPreferencesRepository @Inject constructor(
    private val dataSource: UserPreferencesDataSource,
) : UserPreferencesRepository {

    override val preferences: Flow<AppPreferences> = dataSource.preferences

    override suspend fun setTheme(theme: ThemeSetting) = dataSource.setTheme(theme)

    override suspend fun setDynamicColors(enabled: Boolean) =
        dataSource.setDynamicColors(enabled)

    override suspend fun setCurrencyCode(code: String) = dataSource.setCurrencyCode(code)

    override suspend fun markSeeded() = dataSource.markSeeded()
}
