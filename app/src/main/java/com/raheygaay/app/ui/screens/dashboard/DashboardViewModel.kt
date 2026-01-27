package com.raheygaay.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.data.model.DashboardContent
import com.raheygaay.app.data.repository.PerformanceRepository
import com.raheygaay.app.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val content: DashboardContent? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
    private val performanceRepository: PerformanceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun retry() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            runCatching { repository.getDashboard() }
                .onSuccess { content ->
                    val stats = performanceRepository.getWeeklyStats()
                    val adjusted = if (stats.isNotEmpty()) {
                        val visits = stats.map { it.appOpens }
                        val totalVisits = stats.sumOf { it.appOpens }
                        content.copy(
                            visitTrend = visits,
                            summary = content.summary.copy(trips = totalVisits)
                        )
                    } else {
                        content
                    }
                    _uiState.value = DashboardUiState(isLoading = false, content = adjusted)
                }
                .onFailure {
                    _uiState.value = DashboardUiState(
                        isLoading = false,
                        errorMessage = it.localizedMessage ?: "dashboard"
                    )
                }
        }
    }
}
