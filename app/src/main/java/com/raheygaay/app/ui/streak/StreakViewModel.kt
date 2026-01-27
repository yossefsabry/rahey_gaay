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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

@HiltViewModel
class StreakViewModel @Inject constructor(
    private val repository: StreakRepository
) : ViewModel() {
    private val ownerKey = MutableStateFlow<String?>(null)

    val streakState: StateFlow<StreakState> = ownerKey
        .flatMapLatest { key ->
            if (key.isNullOrBlank()) {
                flowOf(StreakState())
            } else {
                repository.streakFlow(key)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StreakState())

    private val _popupVisible = MutableStateFlow(false)
    val popupVisible: StateFlow<Boolean> = _popupVisible.asStateFlow()

    private var hasStarted = false
    private var activeOwnerKey: String? = null

    fun setOwnerKey(key: String?) {
        if (activeOwnerKey == key) return
        activeOwnerKey = key
        ownerKey.value = key
        hasStarted = false
        _popupVisible.value = false
    }

    fun onAppOpened() {
        if (hasStarted) return
        val key = ownerKey.value
        if (key.isNullOrBlank()) return
        hasStarted = true
        viewModelScope.launch {
            val updated = repository.updateOnAppOpen(key)
            if (shouldShowPopup(updated)) {
                delay(20_000)
                _popupVisible.value = true
            }
        }
    }

    fun dismissPopup() {
        viewModelScope.launch {
            val key = ownerKey.value
            val today = LocalDate.now().toEpochDay()
            if (!key.isNullOrBlank()) {
                repository.markPopupShown(key, today)
            }
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
