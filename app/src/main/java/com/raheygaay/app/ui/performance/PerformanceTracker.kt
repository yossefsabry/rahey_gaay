package com.raheygaay.app.ui.performance

import android.os.SystemClock
import android.util.Log
import android.view.Window
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.metrics.performance.JankStats
import androidx.tracing.Trace
import com.raheygaay.app.BuildConfig
import java.util.Locale

private const val TAG = "PerformanceTracker"

private data class ScreenStats(
    var totalFrames: Int = 0,
    var jankFrames: Int = 0,
    var lastTtffMs: Long? = null
) {
    fun reset() {
        totalFrames = 0
        jankFrames = 0
        lastTtffMs = null
    }
}

class PerformanceTracker private constructor(
    private val enabled: Boolean,
    private val window: Window,
    private val clockMs: () -> Long = SystemClock::elapsedRealtime
) : DefaultLifecycleObserver {
    private var jankStats: JankStats? = null
    private var currentRoute: String? = null
    private var currentStartMs: Long = 0L
    private var traceActive = false
    private val statsByRoute = mutableMapOf<String, ScreenStats>()

    companion object {
        fun create(window: Window, enabled: Boolean = BuildConfig.DEBUG): PerformanceTracker {
            return PerformanceTracker(enabled, window)
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        if (!enabled) return
        ensureJankStats()?.isTrackingEnabled = true
    }

    override fun onStop(owner: LifecycleOwner) {
        if (!enabled) return
        jankStats?.isTrackingEnabled = false
        currentRoute?.let { logAndReset(it) }
    }

    fun onScreenStart(route: String) {
        if (!enabled) return
        ensureJankStats()
        if (route == currentRoute) return
        currentRoute?.let { logAndReset(it) }
        currentRoute = route
        currentStartMs = clockMs()
        startTrace(route)
    }

    fun onScreenFirstFrame(route: String) {
        if (!enabled) return
        if (route != currentRoute) return
        val ttff = (clockMs() - currentStartMs).coerceAtLeast(0L)
        val stats = statsByRoute.getOrPut(route) { ScreenStats() }
        stats.lastTtffMs = ttff
        Log.d(TAG, "TTFF route=$route ms=$ttff")
        endTrace()
    }

    private fun onFrame(isJank: Boolean) {
        val route = currentRoute ?: return
        val stats = statsByRoute.getOrPut(route) { ScreenStats() }
        stats.totalFrames += 1
        if (isJank) {
            stats.jankFrames += 1
        }
    }

    private fun logAndReset(route: String) {
        val stats = statsByRoute.getOrPut(route) { ScreenStats() }
        val total = stats.totalFrames
        val jank = stats.jankFrames
        val pct = if (total > 0) (jank.toDouble() / total.toDouble()) * 100.0 else 0.0
        val ttff = stats.lastTtffMs?.toString() ?: "n/a"
        Log.d(
            TAG,
            String.format(
                Locale.US,
                "Screen route=%s ttff_ms=%s frames=%d jank=%d jank_pct=%.2f",
                route,
                ttff,
                total,
                jank,
                pct
            )
        )
        stats.reset()
    }

    private fun ensureJankStats(): JankStats? {
        if (!enabled) return null
        val existing = jankStats
        if (existing != null) return existing
        if (window.peekDecorView() == null) return null
        val created = runCatching {
            JankStats.createAndTrack(window) { frameData ->
                onFrame(frameData.isJank)
            }
        }.getOrNull()
        created?.isTrackingEnabled = true
        jankStats = created
        return created
    }

    private fun startTrace(route: String) {
        if (traceActive) {
            Trace.endSection()
            traceActive = false
        }
        Trace.beginSection("screen_ttff:$route")
        traceActive = true
    }

    private fun endTrace() {
        if (!traceActive) return
        Trace.endSection()
        traceActive = false
    }
}
