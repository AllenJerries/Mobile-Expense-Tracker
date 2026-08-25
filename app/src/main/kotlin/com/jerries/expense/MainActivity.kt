package com.jerries.expense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.core.designsystem.theme.JerriesExpenseTheme
import com.jerries.expense.core.security.AuthState
import com.jerries.expense.domain.model.ThemeSetting
import com.jerries.expense.feature.security.PinEntryScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val mainState by viewModel.uiState.collectAsStateWithLifecycle()
            val windowSizeClass = calculateWindowSizeClass(this)

            JerriesExpenseTheme(
                darkTheme = when (mainState.preferences.theme) {
                    ThemeSetting.SYSTEM -> isSystemInDarkTheme()
                    ThemeSetting.LIGHT -> false
                    ThemeSetting.DARK -> true
                },
                dynamicColor = mainState.preferences.useDynamicColors,
            ) {
                when (mainState.authState) {
                    AuthState.LOCKED -> {
                        PinEntryScreen(
                            onUnlocked = { viewModel.unlock() },
                        )
                    }
                    AuthState.SETUP -> {
                        PinEntryScreen(
                            onUnlocked = { viewModel.unlock() },
                        )
                    }
                    AuthState.UNLOCKED -> {
                        JerriesExpenseApp(
                            windowWidthSizeClass = windowSizeClass.widthSizeClass,
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.onAppForeground()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onAppBackground()
    }
}
