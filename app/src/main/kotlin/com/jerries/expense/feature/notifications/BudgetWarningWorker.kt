package com.jerries.expense.feature.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.jerries.expense.R
import com.jerries.expense.core.common.TimeProvider
import com.jerries.expense.domain.repository.BudgetRepository
import com.jerries.expense.domain.repository.TransactionRepository
import com.jerries.expense.domain.repository.UserPreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class BudgetWarningWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val timeProvider: TimeProvider,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = userPreferencesRepository.preferences.first()
        if (!prefs.notificationBudgetWarnings) return Result.success()

        ensureNotificationChannel()

        val today = timeProvider.today()
        val todayEpochDay = today.toEpochDay()
        val startOfMonth = today.withDayOfMonth(1).toEpochDay()

        val activeBudgets = budgetRepository.observeActive(todayEpochDay).first()

        for (budget in activeBudgets) {
            if (isStopped) return Result.success()

            val spent = if (budget.categoryId != null) {
                transactionRepository.observeSpendingForBudget(
                    budget.categoryId,
                    startOfMonth,
                    todayEpochDay,
                ).first()
            } else {
                transactionRepository.observeSpendingForBudgetByAccount(
                    budget.accountId ?: "",
                    startOfMonth,
                    todayEpochDay,
                ).first()
            }

            val percentage = if (budget.limitMinor > 0) {
                spent.toDouble() / budget.limitMinor.toDouble()
            } else 0.0

            if (percentage >= budget.alertThreshold) {
                showBudgetNotification(budget.limitMinor, spent, percentage)
            }
        }

        return Result.success()
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                applicationContext.getString(R.string.notification_channel_budget),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showBudgetNotification(limitMinor: Long, spentMinor: Long, percentage: Double) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val limitFormatted = com.jerries.expense.core.util.CurrencyFormatter.formatMinorUnits(limitMinor, "USD")
        val spentFormatted = com.jerries.expense.core.util.CurrencyFormatter.formatMinorUnits(spentMinor, "USD")

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(applicationContext.getString(R.string.notification_budget_warning_title))
            .setContentText(
                applicationContext.getString(
                    R.string.notification_budget_warning_body,
                    spentFormatted,
                    limitFormatted,
                    "${(percentage * 100).toInt()}%",
                ),
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "budget_warnings"
        private const val NOTIFICATION_ID = 2001
        private const val WORK_NAME = "budget_warning_check"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<BudgetWarningWorker>(
                6, TimeUnit.HOURS,
            ).setInitialDelay(2, TimeUnit.HOURS).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
