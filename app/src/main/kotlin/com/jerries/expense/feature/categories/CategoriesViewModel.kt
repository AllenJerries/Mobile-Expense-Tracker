package com.jerries.expense.feature.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerries.expense.core.designsystem.icon.JeIcons
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CategoryRow(
    val id: String,
    val name: String,
    val kindLabel: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

data class CategoriesUiState(
    val isLoading: Boolean = true,
    val categories: List<CategoryRow> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && categories.isEmpty()
}

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    observeCategories: ObserveCategoriesUseCase,
) : ViewModel() {

    val uiState: StateFlow<CategoriesUiState> = observeCategories()
        .map { categories ->
            CategoriesUiState(
                isLoading = false,
                categories = categories.map { it.toRow() },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CategoriesUiState(),
        )

    private fun Category.toRow() = CategoryRow(
        id = id,
        name = name,
        kindLabel = kind.name.lowercase().replaceFirstChar { it.uppercase() },
        icon = JeIcons.category(iconKey),
    )
}
