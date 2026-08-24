package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.model.AccountBalance
import com.jerries.expense.domain.repository.AccountRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Streams the computed balance of every account in minor units. */
class ObserveAccountBalancesUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(): Flow<List<AccountBalance>> = accountRepository.observeBalances()
}
