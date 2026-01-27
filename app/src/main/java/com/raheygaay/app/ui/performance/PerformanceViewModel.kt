package com.raheygaay.app.ui.performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.data.repository.PerformanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val repository: PerformanceRepository
) : ViewModel() {
    fun recordAppOpen() {
        viewModelScope.launch {
            repository.recordAppOpen()
        }
    }
}
