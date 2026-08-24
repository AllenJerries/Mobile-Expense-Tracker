package com.jerries.expense.core.common

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Generates storage identifiers; injectable for deterministic tests. */
fun interface IdGenerator {
    fun newId(): String
}

@Singleton
class UuidIdGenerator @Inject constructor() : IdGenerator {
    override fun newId(): String = UUID.randomUUID().toString()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class IdGeneratorModule {

    @Binds
    @Singleton
    abstract fun bindIdGenerator(impl: UuidIdGenerator): IdGenerator
}
