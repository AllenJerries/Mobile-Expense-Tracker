package com.jerries.expense.domain.repository

import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.CategoryKind
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeAll(): Flow<List<Category>>

    fun observeByKind(kind: CategoryKind): Flow<List<Category>>

    suspend fun getById(id: String): Category?

    suspend fun upsert(category: Category)
}
