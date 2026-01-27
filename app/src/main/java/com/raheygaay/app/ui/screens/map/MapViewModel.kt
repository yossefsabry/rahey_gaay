package com.raheygaay.app.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.data.model.MapContent
import com.raheygaay.app.data.repository.MapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val isLoading: Boolean = true,
    val content: MapContent? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: MapRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadMap()
    }

    fun retry() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        loadMap()
    }

    private fun loadMap() {
        viewModelScope.launch {
            runCatching { repository.getMapContent() }
                .onSuccess { content ->
                    _uiState.value = MapUiState(isLoading = false, content = content)
                }
                .onFailure {
                    _uiState.value = MapUiState(
                        isLoading = false,
                        errorMessage = it.localizedMessage ?: "map"
                    )
                }
        }
    }
}
