package com.jerries.expense.data.repository

import com.jerries.expense.data.preferences.UserPreferencesDataSource
import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.model.FirstDayOfWeek
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
    override suspend fun setDynamicColors(enabled: Boolean) = dataSource.setDynamicColors(enabled)
    override suspend fun setCurrencyCode(code: String) = dataSource.setCurrencyCode(code)
    override suspend fun markSeeded() = dataSource.markSeeded()
    override suspend fun setFirstDayOfWeek(day: FirstDayOfWeek) = dataSource.setFirstDayOfWeek(day)
    override suspend fun setNotificationBudgetWarnings(enabled: Boolean) = dataSource.setNotificationBudgetWarnings(enabled)
    override suspend fun setNotificationRecurringReminders(enabled: Boolean) = dataSource.setNotificationRecurringReminders(enabled)
    override suspend fun setNotificationSavingsReminders(enabled: Boolean) = dataSource.setNotificationSavingsReminders(enabled)
    override suspend fun setBiometricEnabled(enabled: Boolean) = dataSource.setBiometricEnabled(enabled)
    override suspend fun setPinEnabled(enabled: Boolean) = dataSource.setPinEnabled(enabled)
    override suspend fun setPinHash(hash: String?) = dataSource.setPinHash(hash)
    override suspend fun setAutoLockTimeout(minutes: Int) = dataSource.setAutoLockTimeout(minutes)
    override suspend fun setHideSensitiveInfo(hide: Boolean) = dataSource.setHideSensitiveInfo(hide)
}
