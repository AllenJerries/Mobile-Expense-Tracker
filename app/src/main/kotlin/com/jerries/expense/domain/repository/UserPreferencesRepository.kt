package com.jerries.expense.domain.repository

import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.model.FirstDayOfWeek
import com.jerries.expense.domain.model.ThemeSetting
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferences: Flow<AppPreferences>

    suspend fun setTheme(theme: ThemeSetting)
    suspend fun setDynamicColors(enabled: Boolean)
    suspend fun setCurrencyCode(code: String)
    suspend fun markSeeded()
    suspend fun setFirstDayOfWeek(day: FirstDayOfWeek)
    suspend fun setNotificationBudgetWarnings(enabled: Boolean)
    suspend fun setNotificationRecurringReminders(enabled: Boolean)
    suspend fun setNotificationSavingsReminders(enabled: Boolean)
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun setPinEnabled(enabled: Boolean)
    suspend fun setPinHash(hash: String?)
    suspend fun setAutoLockTimeout(minutes: Int)
    suspend fun setHideSensitiveInfo(hide: Boolean)
}
