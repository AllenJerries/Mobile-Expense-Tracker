package com.jerries.expense

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.jerries.expense.core.common.ApplicationScope
import com.jerries.expense.domain.usecase.InitializeAppDataUseCase
import com.jerries.expense.feature.notifications.BudgetWarningWorker
import com.jerries.expense.feature.notifications.RecurringReminderWorker
import com.jerries.expense.feature.notifications.SavingsReminderWorker
import com.jerries.expense.feature.recurring.RecurringWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class JerriesExpenseApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var initializeAppData: InitializeAppDataUseCase

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch { initializeAppData() }
        createNotificationChannels()
        scheduleWorkers()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val budgetChannel = NotificationChannel(
                "budget_warnings",
                getString(R.string.notification_channel_budget),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            val savingsChannel = NotificationChannel(
                "savings_reminders",
                getString(R.string.notification_channel_savings),
                NotificationManager.IMPORTANCE_LOW,
            )
            val recurringChannel = NotificationChannel(
                "recurring_reminders",
                getString(R.string.notification_channel_recurring),
                NotificationManager.IMPORTANCE_DEFAULT,
            )

            manager.createNotificationChannel(budgetChannel)
            manager.createNotificationChannel(savingsChannel)
            manager.createNotificationChannel(recurringChannel)
        }
    }

    private fun scheduleWorkers() {
        RecurringWorker.schedule(this)
        BudgetWarningWorker.schedule(this)
        SavingsReminderWorker.schedule(this)
        RecurringReminderWorker.schedule(this)
    }
}
