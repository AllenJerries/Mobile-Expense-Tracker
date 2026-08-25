package com.jerries.expense.feature.categories

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.CategoryIcon
import com.jerries.expense.core.designsystem.component.EmptyContent
import com.jerries.expense.core.designsystem.component.GlassTopBar
import com.jerries.expense.core.designsystem.theme.LocalSpacing

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    Column(modifier = modifier.fillMaxSize()) {
        GlassTopBar(title = { Text(stringResource(R.string.categories_title)) })
        when {
            state.isLoading -> Unit

            state.isEmpty -> EmptyContent(
                icon = Icons.Filled.Category,
                title = stringResource(R.string.empty_generic_title),
                message = stringResource(R.string.empty_generic_message),
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = spacing.large),
            ) {
                items(state.categories, key = { it.id }) { category ->
                    ListItem(
                        headlineContent = { Text(category.name) },
                        supportingContent = { Text(category.kindLabel) },
                        leadingContent = {
                            CategoryIcon(icon = category.icon, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
