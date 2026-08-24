package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.repository.AccountRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Streams the aggregate balance across all non-archived accounts. */
class ObserveTotalBalanceUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(): Flow<Long> = accountRepository.observeTotalBalanceMinor()
}
