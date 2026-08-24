package com.jerries.expense.feature.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.JeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    modifier: Modifier = Modifier,
) {
    val spacing = com.jerries.expense.core.designsystem.theme.LocalSpacing.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.backup_title)) }) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            JeCard {
                Text(
                    text = stringResource(R.string.backup_description),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(spacing.cardPadding),
                )
            }
            Text(
                text = stringResource(R.string.backup_coming_soon),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
