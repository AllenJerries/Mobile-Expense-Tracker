package com.jerries.expense.feature.settings

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.domain.model.FirstDayOfWeek
import com.jerries.expense.domain.model.ThemeSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private val SUPPORTED_CURRENCIES = listOf("USD", "EUR", "GBP", "PHP", "JPY", "AUD", "CAD", "INR", "NGN", "KES")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onOpenAccounts: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenBackup: () -> Unit,
    onOpenSecurity: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            // ── Appearance ──
            Text(
                text = stringResource(R.string.settings_appearance),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = spacing.medium),
            )

            ThemeSetting.entries.forEach { theme ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onThemeChange(theme) }
                        .padding(vertical = spacing.extraSmall),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = state.preferences.theme == theme,
                        onClick = { viewModel.onThemeChange(theme) },
                    )
                    Text(
                        text = when (theme) {
                            ThemeSetting.SYSTEM -> stringResource(R.string.settings_theme_system)
                            ThemeSetting.LIGHT -> stringResource(R.string.settings_theme_light)
                            ThemeSetting.DARK -> stringResource(R.string.settings_theme_dark)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_dynamic_colors)) },
                supportingContent = {
                    Text(stringResource(R.string.settings_dynamic_colors_description))
                },
                trailingContent = {
                    Switch(
                        checked = state.preferences.useDynamicColors,
                        onCheckedChange = viewModel::onDynamicColorsChange,
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))

            // ── General ──
            Text(
                text = stringResource(R.string.settings_general),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )

            CurrencyPicker(
                selected = state.preferences.currencyCode,
                onSelect = viewModel::onCurrencyChange,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))

            // ── First day of week ──
            Text(
                text = stringResource(R.string.settings_first_day_of_week),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )

            FirstDayOfWeekOption(
                label = stringResource(R.string.first_day_sunday),
                selected = state.preferences.firstDayOfWeek == FirstDayOfWeek.SUNDAY,
                onClick = { viewModel.onFirstDayOfWeekChange(FirstDayOfWeek.SUNDAY) },
            )
            FirstDayOfWeekOption(
                label = stringResource(R.string.first_day_monday),
                selected = state.preferences.firstDayOfWeek == FirstDayOfWeek.MONDAY,
                onClick = { viewModel.onFirstDayOfWeekChange(FirstDayOfWeek.MONDAY) },
            )
            FirstDayOfWeekOption(
                label = stringResource(R.string.first_day_saturday),
                selected = state.preferences.firstDayOfWeek == FirstDayOfWeek.SATURDAY,
                onClick = { viewModel.onFirstDayOfWeekChange(FirstDayOfWeek.SATURDAY) },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))

            // ── Notifications ──
            Text(
                text = stringResource(R.string.settings_notifications),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_notification_budget_warnings)) },
                supportingContent = {
                    Text(stringResource(R.string.settings_notification_budget_warnings_desc))
                },
                trailingContent = {
                    Switch(
                        checked = state.preferences.notificationBudgetWarnings,
                        onCheckedChange = viewModel::onNotificationBudgetWarningsChange,
                    )
                },
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_notification_recurring)) },
                supportingContent = {
                    Text(stringResource(R.string.settings_notification_recurring_desc))
                },
                trailingContent = {
                    Switch(
                        checked = state.preferences.notificationRecurringReminders,
                        onCheckedChange = viewModel::onNotificationRecurringChange,
                    )
                },
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_notification_savings)) },
                supportingContent = {
                    Text(stringResource(R.string.settings_notification_savings_desc))
                },
                trailingContent = {
                    Switch(
                        checked = state.preferences.notificationSavingsReminders,
                        onCheckedChange = viewModel::onNotificationSavingsChange,
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))

            // ── Data ──
            Text(
                text = stringResource(R.string.settings_data),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )

            NavigationRow(label = stringResource(R.string.accounts_title), onClick = onOpenAccounts)
            NavigationRow(label = stringResource(R.string.categories_title), onClick = onOpenCategories)

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_export)) },
                supportingContent = { Text(stringResource(R.string.settings_export_csv)) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch {
                            try {
                                viewModel.exportAllTransactionsCsv(context)
                                snackbarHostState.showSnackbar(context.getString(R.string.settings_exported))
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(e.message ?: "Export failed")
                            }
                        }
                    },
            )

            NavigationRow(label = stringResource(R.string.backup_title), onClick = onOpenBackup)

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))

            // ── Security ──
            Text(
                text = stringResource(R.string.settings_security),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )

            NavigationRow(label = stringResource(R.string.security_title), onClick = onOpenSecurity)

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))

            // ── About ──
            Text(
                text = stringResource(R.string.settings_about),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.settings_about_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.settings_version),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))

            Text(
                text = stringResource(R.string.settings_privacy),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.settings_privacy_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = spacing.large),
            )
        }
    }
}

@Composable
private fun CurrencyPicker(
    selected: String,
    onSelect: (String) -> Unit,
) {
    Column {
        SUPPORTED_CURRENCIES.forEach { currency ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(currency) }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = selected == currency, onClick = { onSelect(currency) })
                Text(text = currency, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun FirstDayOfWeekOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun NavigationRow(
    label: String,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    )
}
