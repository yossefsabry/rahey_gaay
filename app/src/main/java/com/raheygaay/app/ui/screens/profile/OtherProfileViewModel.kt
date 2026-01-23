package com.raheygaay.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.data.model.OtherProfile
import com.raheygaay.app.data.repository.OtherProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OtherProfileUiState(
    val isLoading: Boolean = true,
    val profile: OtherProfile? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class OtherProfileViewModel @Inject constructor(
    private val repository: OtherProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OtherProfileUiState())
    val uiState: StateFlow<OtherProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            runCatching { repository.getOtherProfile() }
                .onSuccess { profile ->
                    _uiState.value = OtherProfileUiState(isLoading = false, profile = profile)
                }
                .onFailure {
                    _uiState.value = OtherProfileUiState(isLoading = false, errorMessage = "profile")
                }
        }
    }
}
