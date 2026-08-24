package com.jerries.expense.feature.goals

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.EmptyContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.empty_goals_title)) }) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        EmptyContent(
            icon = Icons.Filled.Flag,
            title = stringResource(R.string.empty_goals_title),
            message = stringResource(R.string.empty_goals_message),
            modifier = Modifier.padding(padding),
        )
    }
}
