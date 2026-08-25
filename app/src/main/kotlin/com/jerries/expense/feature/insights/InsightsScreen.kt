package com.jerries.expense.feature.insights

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.EmptyContent
import com.jerries.expense.core.designsystem.component.GlassTopBar

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        GlassTopBar(title = { Text(stringResource(R.string.insights_title), style = MaterialTheme.typography.titleLarge) })
        EmptyContent(
            icon = Icons.Filled.Insights,
            title = stringResource(R.string.empty_generic_title),
            message = stringResource(R.string.insights_empty_message),
        )
    }
}
