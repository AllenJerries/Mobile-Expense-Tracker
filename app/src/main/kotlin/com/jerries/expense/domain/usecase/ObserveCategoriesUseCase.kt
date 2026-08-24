package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.CategoryKind
import com.jerries.expense.domain.repository.CategoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Streams categories, optionally filtered by kind. */
class ObserveCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(kind: CategoryKind? = null): Flow<List<Category>> =
        if (kind == null) {
            categoryRepository.observeAll()
        } else {
            categoryRepository.observeByKind(kind)
        }
}
