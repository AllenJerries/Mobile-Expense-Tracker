package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.repository.AccountRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Streams all accounts in insertion order. */
class ObserveAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(): Flow<List<Account>> = accountRepository.observeAll()
}
