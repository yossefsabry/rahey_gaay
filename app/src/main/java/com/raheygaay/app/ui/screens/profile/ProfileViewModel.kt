package com.raheygaay.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.data.model.Profile
import com.raheygaay.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: Profile? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun retry() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            runCatching { repository.getProfile() }
                .onSuccess { profile ->
                    _uiState.value = ProfileUiState(isLoading = false, profile = profile)
                }
                .onFailure {
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        errorMessage = it.localizedMessage ?: "profile"
                    )
                }
        }
    }
}
