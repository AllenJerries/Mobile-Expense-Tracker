package com.jerries.expense.feature.recurring

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.jerries.expense.core.common.IdGenerator
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.model.TransactionType
import com.jerries.expense.domain.repository.RecurringTransactionRepository
import com.jerries.expense.domain.usecase.AddTransactionUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@HiltWorker
class RecurringWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val recurringRepository: RecurringTransactionRepository,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val timeProvider: TimeProvider,
    private val idGenerator: IdGenerator,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val today = timeProvider.today()
        val todayEpochDay = today.toEpochDay()
        val dueTransactions = recurringRepository.getDueOccurrences(todayEpochDay)

        for (recurring in dueTransactions) {
            if (isStopped) return Result.success()

            val tx = Transaction(
                id = idGenerator.newId(),
                accountId = recurring.accountId,
                categoryId = recurring.categoryId,
                amountMinor = recurring.amountMinor,
                type = recurring.type,
                dateEpochDay = todayEpochDay,
                title = recurring.description,
                note = null,
                createdAtEpochMillis = timeProvider.nowMillis(),
                updatedAtEpochMillis = timeProvider.nowMillis(),
                paymentMethod = null,
                destinationAccountId = recurring.destinationAccountId,
                recurringTransactionId = recurring.id,
                attachmentUri = null,
                isDeleted = false,
            )

            addTransactionUseCase(tx)

            val nextDay = computeNextOccurrence(todayEpochDay, recurring.frequency)
            if (recurring.endDateEpochDay != null && nextDay > recurring.endDateEpochDay) {
                recurringRepository.deactivate(recurring.id)
            } else {
                recurringRepository.updateNextOccurrence(recurring.id, nextDay)
            }
        }
        return Result.success()
    }

    private fun computeNextOccurrence(currentEpochDay: Long, frequency: com.jerries.expense.domain.model.RecurrenceFrequency): Long {
        val current = java.time.LocalDate.ofEpochDay(currentEpochDay)
        val next = when (frequency) {
            com.jerries.expense.domain.model.RecurrenceFrequency.DAILY -> current.plusDays(1)
            com.jerries.expense.domain.model.RecurrenceFrequency.WEEKLY -> current.plusWeeks(1)
            com.jerries.expense.domain.model.RecurrenceFrequency.BIWEEKLY -> current.plusWeeks(2)
            com.jerries.expense.domain.model.RecurrenceFrequency.MONTHLY -> current.plusMonths(1)
            com.jerries.expense.domain.model.RecurrenceFrequency.QUARTERLY -> current.plusMonths(3)
            com.jerries.expense.domain.model.RecurrenceFrequency.YEARLY -> current.plusYears(1)
        }
        return next.toEpochDay()
    }

    companion object {
        private const val WORK_NAME = "recurring_transactions"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<RecurringWorker>(
                1, TimeUnit.DAYS,
            ).setInitialDelay(1, TimeUnit.HOURS).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
