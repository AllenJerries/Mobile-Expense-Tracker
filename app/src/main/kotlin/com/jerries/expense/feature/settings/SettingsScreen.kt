package com.jerries.expense.feature.settings

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.domain.model.ThemeSetting

private val SUPPORTED_CURRENCIES = listOf("USD", "EUR", "GBP", "PHP", "JPY", "AUD", "CAD")

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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title)) }) },
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

            NavigationRow(label = stringResource(R.string.accounts_title), onClick = onOpenAccounts)
            NavigationRow(label = stringResource(R.string.categories_title), onClick = onOpenCategories)
            NavigationRow(label = stringResource(R.string.backup_title), onClick = onOpenBackup)
            NavigationRow(label = stringResource(R.string.security_title), onClick = onOpenSecurity)

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))

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
