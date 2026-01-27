package com.raheygaay.app.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.data.local.UserPreferences
import com.raheygaay.app.data.local.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class UserPreferencesViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {
    val preferences: StateFlow<UserPreferences> = repository.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences(null, null, null))

    fun setDark(isDark: Boolean) {
        viewModelScope.launch { repository.setDark(isDark) }
    }

    fun setArabic(isArabic: Boolean) {
        viewModelScope.launch { repository.setArabic(isArabic) }
    }

    fun setAuthState(value: String) {
        viewModelScope.launch { repository.setAuthState(value) }
    }

}
