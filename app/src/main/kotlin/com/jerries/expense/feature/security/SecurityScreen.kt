package com.jerries.expense.feature.security

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.core.security.BiometricHelper
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SecurityViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    var showTimeoutPicker by remember { mutableStateOf(false) }
    var showDisablePinDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showTimeoutPicker) {
        AutoLockTimeoutPicker(
            selectedMinutes = state.preferences.autoLockTimeoutMinutes,
            onSelect = {
                viewModel.onAutoLockTimeoutChange(it)
                showTimeoutPicker = false
            },
            onDismiss = { showTimeoutPicker = false },
        )
    }

    if (showDisablePinDialog) {
        AlertDialog(
            onDismissRequest = { showDisablePinDialog = false },
            title = { Text("Disable PIN lock?") },
            text = { Text("You will no longer need to enter a PIN to open the app.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onPinEnabledChange(false)
                        showDisablePinDialog = false
                    },
                ) {
                    Text("Disable")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisablePinDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.security_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
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
            ListItem(
                headlineContent = { Text(stringResource(R.string.security_pin_lock)) },
                supportingContent = {
                    Text(stringResource(R.string.security_pin_lock_description))
                },
                trailingContent = {
                    Switch(
                        checked = state.preferences.pinEnabled,
                        onCheckedChange = { enabled ->
                            if (!enabled && state.preferences.pinEnabled) {
                                showDisablePinDialog = true
                            } else {
                                viewModel.onPinEnabledChange(enabled)
                            }
                        },
                    )
                },
            )

            HorizontalDivider()

            ListItem(
                headlineContent = { Text(stringResource(R.string.security_biometric)) },
                supportingContent = {
                    Text(
                        if (BiometricHelper.canAuthenticate(context)) {
                            stringResource(R.string.security_biometric_description)
                        } else {
                            stringResource(R.string.security_biometric_not_available)
                        },
                    )
                },
                trailingContent = {
                    Switch(
                        checked = state.preferences.biometricEnabled,
                        onCheckedChange = viewModel::onBiometricEnabledChange,
                        enabled = BiometricHelper.canAuthenticate(context) &&
                            state.preferences.pinEnabled,
                    )
                },
            )

            HorizontalDivider()

            ListItem(
                headlineContent = { Text(stringResource(R.string.security_auto_lock)) },
                supportingContent = {
                    Text(text = formatTimeoutMinutes(state.preferences.autoLockTimeoutMinutes))
                },
                modifier = Modifier.clickable(enabled = state.preferences.pinEnabled) {
                    showTimeoutPicker = true
                },
            )

            HorizontalDivider()

            ListItem(
                headlineContent = { Text(stringResource(R.string.security_hide_sensitive)) },
                supportingContent = {
                    Text(stringResource(R.string.security_hide_sensitive_description))
                },
                trailingContent = {
                    Switch(
                        checked = state.preferences.hideSensitiveInfo,
                        onCheckedChange = viewModel::onHideSensitiveInfoChange,
                    )
                },
            )
        }
    }
}

@Composable
private fun AutoLockTimeoutPicker(
    selectedMinutes: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = listOf(
        0 to stringResource(R.string.security_timeout_none),
        1 to stringResource(R.string.security_timeout_1),
        5 to stringResource(R.string.security_timeout_5),
        15 to stringResource(R.string.security_timeout_15),
        30 to stringResource(R.string.security_timeout_30),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.security_auto_lock)) },
        text = {
            Column {
                options.forEach { (minutes, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(minutes) }
                            .padding(vertical = 4.dp),
                    ) {
                        RadioButton(
                            selected = selectedMinutes == minutes,
                            onClick = { onSelect(minutes) },
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {},
    )
}

private fun formatTimeoutMinutes(minutes: Int): String = when (minutes) {
    0 -> "Never"
    1 -> "1 minute"
    else -> "$minutes minutes"
}
