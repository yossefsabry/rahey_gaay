package com.raheygaay.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.data.model.HomeContent
import com.raheygaay.app.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val content: HomeContent? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun retry() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        loadHome()
    }

    private fun loadHome() {
        viewModelScope.launch {
            runCatching { repository.getHomeContent() }
                .onSuccess { content ->
                    _uiState.value = HomeUiState(isLoading = false, content = content)
                }
                .onFailure {
                    _uiState.value = HomeUiState(
                        isLoading = false,
                        errorMessage = it.localizedMessage ?: "home"
                    )
                }
        }
    }
}
