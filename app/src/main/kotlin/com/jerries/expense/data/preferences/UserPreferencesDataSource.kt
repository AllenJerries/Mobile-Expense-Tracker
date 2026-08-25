package com.jerries.expense.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.model.FirstDayOfWeek
import com.jerries.expense.domain.model.ThemeSetting
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "jerries_expense_prefs",
)

@Singleton
class UserPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val CURRENCY = stringPreferencesKey("currency_code")
        val SEEDED = booleanPreferencesKey("seeded")
        val FIRST_DAY_OF_WEEK = stringPreferencesKey("first_day_of_week")
        val NOTIF_BUDGET = booleanPreferencesKey("notif_budget_warnings")
        val NOTIF_RECURRING = booleanPreferencesKey("notif_recurring_reminders")
        val NOTIF_SAVINGS = booleanPreferencesKey("notif_savings_reminders")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val PIN_ENABLED = booleanPreferencesKey("pin_enabled")
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val AUTO_LOCK_MINUTES = intPreferencesKey("auto_lock_minutes")
        val HIDE_SENSITIVE = booleanPreferencesKey("hide_sensitive_info")
    }

    val preferences: Flow<AppPreferences> = context.userDataStore.data.map { prefs ->
        AppPreferences(
            theme = prefs[Keys.THEME]
                ?.let { runCatching { ThemeSetting.valueOf(it) }.getOrNull() }
                ?: ThemeSetting.SYSTEM,
            useDynamicColors = prefs[Keys.DYNAMIC_COLORS] ?: false,
            currencyCode = prefs[Keys.CURRENCY] ?: "USD",
            seeded = prefs[Keys.SEEDED] ?: false,
            firstDayOfWeek = prefs[Keys.FIRST_DAY_OF_WEEK]
                ?.let { runCatching { FirstDayOfWeek.valueOf(it) }.getOrNull() }
                ?: FirstDayOfWeek.MONDAY,
            notificationBudgetWarnings = prefs[Keys.NOTIF_BUDGET] ?: true,
            notificationRecurringReminders = prefs[Keys.NOTIF_RECURRING] ?: true,
            notificationSavingsReminders = prefs[Keys.NOTIF_SAVINGS] ?: true,
            biometricEnabled = prefs[Keys.BIOMETRIC_ENABLED] ?: false,
            pinEnabled = prefs[Keys.PIN_ENABLED] ?: false,
            pinHash = prefs[Keys.PIN_HASH],
            autoLockTimeoutMinutes = prefs[Keys.AUTO_LOCK_MINUTES] ?: 5,
            hideSensitiveInfo = prefs[Keys.HIDE_SENSITIVE] ?: false,
        )
    }

    suspend fun setTheme(theme: ThemeSetting) {
        context.userDataStore.edit { it[Keys.THEME] = theme.name }
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        context.userDataStore.edit { it[Keys.DYNAMIC_COLORS] = enabled }
    }

    suspend fun setCurrencyCode(code: String) {
        context.userDataStore.edit { it[Keys.CURRENCY] = code }
    }

    suspend fun markSeeded() {
        context.userDataStore.edit { it[Keys.SEEDED] = true }
    }

    suspend fun setFirstDayOfWeek(day: FirstDayOfWeek) {
        context.userDataStore.edit { it[Keys.FIRST_DAY_OF_WEEK] = day.name }
    }

    suspend fun setNotificationBudgetWarnings(enabled: Boolean) {
        context.userDataStore.edit { it[Keys.NOTIF_BUDGET] = enabled }
    }

    suspend fun setNotificationRecurringReminders(enabled: Boolean) {
        context.userDataStore.edit { it[Keys.NOTIF_RECURRING] = enabled }
    }

    suspend fun setNotificationSavingsReminders(enabled: Boolean) {
        context.userDataStore.edit { it[Keys.NOTIF_SAVINGS] = enabled }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.userDataStore.edit { it[Keys.BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun setPinEnabled(enabled: Boolean) {
        context.userDataStore.edit { it[Keys.PIN_ENABLED] = enabled }
    }

    suspend fun setPinHash(hash: String?) {
        context.userDataStore.edit {
            if (hash != null) it[Keys.PIN_HASH] = hash else it.remove(Keys.PIN_HASH)
        }
    }

    suspend fun setAutoLockTimeout(minutes: Int) {
        context.userDataStore.edit { it[Keys.AUTO_LOCK_MINUTES] = minutes }
    }

    suspend fun setHideSensitiveInfo(hide: Boolean) {
        context.userDataStore.edit { it[Keys.HIDE_SENSITIVE] = hide }
    }
}
