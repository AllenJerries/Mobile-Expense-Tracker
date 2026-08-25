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
import com.jerries.expense.domain.repository.RecurringTransactionRepository
import com.jerries.expense.domain.repository.UserPreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class RecurringReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val timeProvider: TimeProvider,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = userPreferencesRepository.preferences.first()
        if (!prefs.notificationRecurringReminders) return Result.success()

        ensureNotificationChannel()

        val todayEpochDay = timeProvider.today().toEpochDay()
        val dueRecurring = recurringTransactionRepository.getDueOccurrences(todayEpochDay)

        for ((index, recurring) in dueRecurring.withIndex()) {
            if (isStopped) return Result.success()
            val description = recurring.description ?: recurring.type.name
            showRecurringNotification(description, NOTIFICATION_ID_BASE + index)
        }

        return Result.success()
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                applicationContext.getString(R.string.notification_channel_recurring),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showRecurringNotification(description: String, notificationId: Int) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(applicationContext.getString(R.string.notification_recurring_title))
            .setContentText(
                applicationContext.getString(R.string.notification_recurring_body, description),
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(notificationId, notification)
    }

    companion object {
        private const val CHANNEL_ID = "recurring_reminders"
        private const val NOTIFICATION_ID_BASE = 4001
        private const val WORK_NAME = "recurring_reminder"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<RecurringReminderWorker>(
                12, TimeUnit.HOURS,
            ).setInitialDelay(1, TimeUnit.HOURS).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
