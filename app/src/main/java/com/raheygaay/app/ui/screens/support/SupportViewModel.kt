package com.raheygaay.app.ui.screens.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.data.model.SupportContent
import com.raheygaay.app.data.repository.SupportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SupportUiState(
    val isLoading: Boolean = true,
    val content: SupportContent? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val repository: SupportRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState: StateFlow<SupportUiState> = _uiState.asStateFlow()

    init {
        loadSupport()
    }

    private fun loadSupport() {
        viewModelScope.launch {
            runCatching { repository.getSupportContent() }
                .onSuccess { content ->
                    _uiState.value = SupportUiState(isLoading = false, content = content)
                }
                .onFailure {
                    _uiState.value = SupportUiState(isLoading = false, errorMessage = "support")
                }
        }
    }
}
