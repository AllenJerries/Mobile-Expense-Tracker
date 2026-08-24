package com.jerries.expense.data.repository

import com.jerries.expense.data.local.dao.CategoryDao
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.CategoryKind
import com.jerries.expense.domain.repository.CategoryRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class OfflineFirstCategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
) : CategoryRepository {

    override fun observeAll(): Flow<List<Category>> =
        categoryDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByKind(kind: CategoryKind): Flow<List<Category>> =
        categoryDao.observeByKind(kind.name).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Category? = categoryDao.getById(id)?.toDomain()

    override suspend fun upsert(category: Category) {
        categoryDao.upsert(category.toEntity())
    }
}
