package com.jerries.expense

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.jerries.expense.core.common.ApplicationScope
import com.jerries.expense.domain.usecase.InitializeAppDataUseCase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Application entry point. Wires Hilt into WorkManager so Phase 2 background
 * work (recurring transactions, scheduled backups) can use injected workers.
 */
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
    }
}
