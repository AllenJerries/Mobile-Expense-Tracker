package com.jerries.expense.feature.settings

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.common.IoDispatcher
import com.jerries.expense.domain.model.AppPreferences
import com.jerries.expense.domain.model.FirstDayOfWeek
import com.jerries.expense.domain.model.ThemeSetting
import com.jerries.expense.domain.repository.TransactionRepository
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase
import com.jerries.expense.domain.usecase.SetCurrencyCodeUseCase
import com.jerries.expense.domain.usecase.SetDynamicColorsUseCase
import com.jerries.expense.domain.usecase.SetThemeSettingUseCase
import com.jerries.expense.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SettingsUiState(
    val isLoading: Boolean = true,
    val preferences: AppPreferences = AppPreferences(),
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val setThemeSetting: SetThemeSettingUseCase,
    private val setDynamicColors: SetDynamicColorsUseCase,
    private val setCurrencyCode: SetCurrencyCodeUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val transactionRepository: TransactionRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = observeUserPreferences()
        .map { prefs -> SettingsUiState(isLoading = false, preferences = prefs) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingsUiState(),
        )

    fun onThemeChange(theme: ThemeSetting) {
        viewModelScope.launch { setThemeSetting(theme) }
    }

    fun onDynamicColorsChange(enabled: Boolean) {
        viewModelScope.launch { setDynamicColors(enabled) }
    }

    fun onCurrencyChange(code: String) {
        viewModelScope.launch { setCurrencyCode(code) }
    }

    fun onFirstDayOfWeekChange(day: FirstDayOfWeek) {
        viewModelScope.launch { userPreferencesRepository.setFirstDayOfWeek(day) }
    }

    fun onNotificationBudgetWarningsChange(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setNotificationBudgetWarnings(enabled) }
    }

    fun onNotificationRecurringChange(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setNotificationRecurringReminders(enabled) }
    }

    fun onNotificationSavingsChange(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setNotificationSavingsReminders(enabled) }
    }

    suspend fun exportAllTransactionsCsv(context: Context) {
        withContext(ioDispatcher) {
            val transactions = transactionRepository.observeAll().first()
            val prefs = userPreferencesRepository.preferences.first()
            val currencyCode = prefs.currencyCode

            val sb = StringBuilder()
            sb.appendLine("Date,Type,Title,Category,Account,Amount")
            transactions.forEach { tx ->
                val date = java.time.LocalDate.ofEpochDay(tx.dateEpochDay).toString()
                val type = tx.type.name
                val title = tx.title?.replace(",", ";")?.replace("\"", "\"\"") ?: ""
                val category = tx.categoryId ?: ""
                val account = tx.accountId
                val amount = com.jerries.expense.core.util.CurrencyFormatter.formatMinorUnits(
                    tx.amountMinor,
                    currencyCode,
                )
                sb.appendLine("$date,$type,\"$title\",$category,$account,$amount")
            }

            val file = File(context.cacheDir, "transactions_export.csv")
            file.writeText(sb.toString())
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            withContext(kotlinx.coroutines.Dispatchers.Main) {
                context.startActivity(Intent.createChooser(intent, null))
            }
        }
    }
}
