package com.jerries.expense.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.model.ThemeSetting
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "jerries_expense_prefs",
)

/** Single source of truth for user-controlled settings, backed by DataStore. */
@Singleton
class UserPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val CURRENCY = stringPreferencesKey("currency_code")
        val SEEDED = booleanPreferencesKey("seeded")
    }

    val preferences: Flow<AppPreferences> = context.userDataStore.data.map { prefs ->
        AppPreferences(
            theme = prefs[Keys.THEME]
                ?.let { runCatching { ThemeSetting.valueOf(it) }.getOrNull() }
                ?: ThemeSetting.SYSTEM,
            useDynamicColors = prefs[Keys.DYNAMIC_COLORS] ?: false,
            currencyCode = prefs[Keys.CURRENCY] ?: "USD",
            seeded = prefs[Keys.SEEDED] ?: false,
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
}
