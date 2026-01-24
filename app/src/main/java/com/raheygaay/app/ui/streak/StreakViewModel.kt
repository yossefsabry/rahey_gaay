package com.raheygaay.app.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.data.model.StreakState
import com.raheygaay.app.data.repository.StreakRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

@HiltViewModel
class StreakViewModel @Inject constructor(
    private val repository: StreakRepository
) : ViewModel() {
    val streakState: StateFlow<StreakState> = repository.streakFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StreakState())

    private val _popupVisible = MutableStateFlow(false)
    val popupVisible: StateFlow<Boolean> = _popupVisible.asStateFlow()

    private var hasStarted = false

    fun onAppOpened() {
        if (hasStarted) return
        hasStarted = true
        viewModelScope.launch {
            val updated = repository.updateOnAppOpen()
            if (shouldShowPopup(updated)) {
                delay(20_000)
                _popupVisible.value = true
            }
        }
    }

    fun dismissPopup() {
        viewModelScope.launch {
            val today = LocalDate.now().toEpochDay()
            repository.markPopupShown(today)
            _popupVisible.value = false
        }
    }

    private fun shouldShowPopup(state: StreakState): Boolean {
        val today = LocalDate.now().toEpochDay()
        val earnedToday = state.lastEarnedEpochDay == today
        val alreadyShown = state.lastPopupEpochDay == today
        return earnedToday && !alreadyShown
    }
}
