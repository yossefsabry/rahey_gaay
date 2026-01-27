package com.raheygaay.app.ui.screens.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.data.model.SupportContent
import com.raheygaay.app.data.repository.ChatRepository
import com.raheygaay.app.data.repository.SupportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.raheygaay.app.R
import com.raheygaay.app.data.model.SupportChat

data class SupportUiState(
    val isLoading: Boolean = true,
    val content: SupportContent? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val repository: SupportRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState: StateFlow<SupportUiState> = _uiState.asStateFlow()

    init {
        loadSupport()
    }

    fun retry() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        loadSupport()
    }

    private fun loadSupport() {
        viewModelScope.launch {
            runCatching { repository.getSupportContent() }
                .onSuccess { content ->
                    content.chats.forEach { chat ->
                        chatRepository.ensureThread(chat)
                    }
                    _uiState.value = SupportUiState(isLoading = false, content = content)
                }
                .onFailure {
                    _uiState.value = SupportUiState(
                        isLoading = false,
                        errorMessage = it.localizedMessage ?: "support"
                    )
                }
        }
    }

    suspend fun ensureSupportChat(): String {
        val content = _uiState.value.content
        val supportId = supportChatId()
        val existing = content?.chats?.firstOrNull { it.id == supportId }
        if (existing != null) {
            chatRepository.ensureThread(existing)
            return supportId
        }
        val supportChat = supportTeamChat()
        chatRepository.ensureThread(supportChat)
        if (content != null) {
            _uiState.value = _uiState.value.copy(
                content = content.copy(chats = listOf(supportChat) + content.chats)
            )
        }
        return supportId
    }

    private fun supportChatId(): String = "support_team"

    private fun supportTeamChat(): SupportChat {
        return SupportChat(
            id = supportChatId(),
            nameRes = R.string.support_support_team_name,
            messageRes = R.string.support_support_team_message,
            timeRes = R.string.support_support_team_time,
            tagRes = null,
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAfqjg95oELxGocKiDhZ2L9_qBSs_wPRy-Rf0Yzl7pP01vi0XdEV_49hAwnlJ4AWdv6VmviC7R8iFmrGme4M2OcNYOt-e8tqoPNL5h5AbTkT4JkkGrH7RuGMxfVtTazIYtYV8NL8uX6Ia1sP1b6Y8D78BuoDFsLtnJrCiC8d_XTd0G1E8ZG_WitSE-hr6ytSiLuk_rzEQmZqqVnlyfTR-qVtCOTqdgTve4MG2NoquVf2xMFLxou76rN7SiIbtZTx65yYBMpxF",
            isSupport = true
        )
    }
}
