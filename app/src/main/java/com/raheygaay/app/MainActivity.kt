package com.raheygaay.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.appcompat.app.AppCompatActivity
import com.raheygaay.app.ui.AppRoot
import com.raheygaay.app.ui.rememberAppState
import com.raheygaay.app.ui.performance.PerformanceTracker
import com.raheygaay.app.ui.theme.RaheyGaayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var performanceTracker: PerformanceTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performanceTracker = PerformanceTracker.create(window)
        lifecycle.addObserver(performanceTracker)
        setContent {
            val appState = rememberAppState()
            RaheyGaayTheme(
                isDark = appState.isDark,
                isArabic = appState.isArabic
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(appState = appState, performanceTracker = performanceTracker)
                }
            }
        }
    }
}
