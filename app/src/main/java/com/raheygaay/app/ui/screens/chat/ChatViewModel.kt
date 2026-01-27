package com.raheygaay.app.ui.screens.chat

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.R
import com.raheygaay.app.data.model.ChatMessage
import com.raheygaay.app.data.model.ChatSender
import com.raheygaay.app.data.model.ChatThread
import com.raheygaay.app.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val chatId: String = savedStateHandle["chatId"] ?: "support_team"
    private val _thread = MutableStateFlow<ChatThread?>(null)
    val thread: StateFlow<ChatThread?> = _thread.asStateFlow()

    private val _draft = MutableStateFlow("")
    val draft: StateFlow<String> = _draft.asStateFlow()
    private var draftJob: Job? = null

    val messages: StateFlow<List<ChatMessage>> = repository.observeMessages(chatId)
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            val existing = repository.getThread(chatId)
            if (existing == null) {
                repository.ensureThread(defaultThread(chatId))
            }
            _thread.value = repository.getThread(chatId)
            _draft.value = repository.getDraft(chatId).orEmpty()
        }
    }

    fun updateDraft(text: String) {
        _draft.value = text
        draftJob?.cancel()
        draftJob = viewModelScope.launch {
            delay(300)
            repository.setDraft(chatId, text)
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(chatId, text.trim(), isUser = true)
            draftJob?.cancel()
            _draft.value = ""
            repository.setDraft(chatId, "")
            val currentThread = repository.getThread(chatId)
            if (currentThread?.isSupport == true) {
                delay(1200)
                repository.sendMessage(
                    chatId,
                    context.getString(R.string.chat_support_auto_reply),
                    isUser = false
                )
            }
        }
    }

    private fun defaultThread(chatId: String): ChatThread {
        return when (chatId) {
            "support_team" -> ChatThread(
                id = chatId,
                name = context.getString(R.string.support_support_team_name),
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAfqjg95oELxGocKiDhZ2L9_qBSs_wPRy-Rf0Yzl7pP01vi0XdEV_49hAwnlJ4AWdv6VmviC7R8iFmrGme4M2OcNYOt-e8tqoPNL5h5AbTkT4JkkGrH7RuGMxfVtTazIYtYV8NL8uX6Ia1sP1b6Y8D78BuoDFsLtnJrCiC8d_XTd0G1E8ZG_WitSE-hr6ytSiLuk_rzEQmZqqVnlyfTR-qVtCOTqdgTve4MG2NoquVf2xMFLxou76rN7SiIbtZTx65yYBMpxF",
                isSupport = true
            )
            "user_fatma", "fatma_hassan" -> ChatThread(
                id = chatId,
                name = context.getString(R.string.name_fatma_hassan),
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBTRNJeM9sKk0uzgDq9pOG0aTSbuWcZ6XhPya6Q0YlXhpoFf8fi-PSgZKdrqzYsORcKtfm3vQgTymng7IRZ4J02t4_xI1nHfQ2hPcP7i2MkhMOnWvpiHjV7e_mL4aAsuG2kZ7Zk20YHBizbHXDQJbV2Udd-yKXgMD70iJH9pB0vBtwJgWOMdBlVO2xCzP74sDTbQhT4C_I2xBX9U5cHTG3kqDJ2G58Ff2tfx-jE_a_urGuWrxZbAQri8jC3LwmkG2csCB6U",
                isSupport = false
            )
            "user_omar", "omar_mostafa" -> ChatThread(
                id = chatId,
                name = context.getString(R.string.name_omar_mostafa),
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCBQBns0ZxQc4J6HfjE1eRkvv0d7WJ7UZ1TZyC83scdLcK8AD-zc9qoT6w2MjhUF2Yv7-bzUZ68ej1ByZq_mfqoS5F1dx3CQaA9CJ9zKcUsm9_UrCh2k8da3_kgBTo3qDBgaQAvTZcOs5a9LYy1RZMw9PxyP5JtA0CkZy6nUX4dYHdN2F1w9-qF5UIzXeMFXfObdlXNJaEdXyOChRKEyN_yjp2RwMvLU8qW8GNi1k4N9pZvVezLhX4PH7iDEbhF1q29NUhnE",
                isSupport = false
            )
            "salma_nour" -> ChatThread(
                id = chatId,
                name = context.getString(R.string.name_salma_nour),
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAc02jS639Sp0JH9EXXN8TjkCXy7ahFArxG6Y87KjhGuxRGwn5JtA_RAtSTwdS-IZ6jzzD-KcndkfGD4oX-jNqfCjF22qh8Q7FE6909ZVYTnRKmgy0hHJ3-T__cT-CkYfdcjjJKhYJdN0XxZYWOMUOTBSQ7gZ6TjkSgBcvTkGTb0aLlhywCSi7w0mK__jn5MeJRhN-uwRP4N31_flp_Fs6SnC9HH1ry969mgy4TNuHohdc69As5JpzNKMGUcc5gCN6BytWJjKkQ_Dk",
                isSupport = false
            )
            "ahmed_ali", "user_ahmed" -> ChatThread(
                id = chatId,
                name = context.getString(R.string.name_ahmed_ali),
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBhV3LxfKpDtJlQftYTzkkX7UKGJ_OaZOgxu6CaEwYatqZgnAoU0QaOuCuM-_bVx0pK-Jls8nRKTbB4axad-Sew6yXvTj2NwHNGl9-Qy1xmuBtmwbB200VY13Qk1y4uM4w2NWV0Dr8LQ_LZUvjjb3k6aQ5rx9VOt0cSBv0AkJJOa6euDRWpoYz1piS1mrczMuJ7r2VrmBecja9xbFz35cM-5OtK3OrrAycAFbnK8mE_nD2_qjWaa2zLm0Q7ofTK2jFfF_9Pgcna_U",
                isSupport = false
            )
            else -> ChatThread(
                id = chatId,
                name = context.getString(R.string.name_ahmed_ali),
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBhV3LxfKpDtJlQftYTzkkX7UKGJ_OaZOgxu6CaEwYatqZgnAoU0QaOuCuM-_bVx0pK-Jls8nRKTbB4axad-Sew6yXvTj2NwHNGl9-Qy1xmuBtmwbB200VY13Qk1y4uM4w2NWV0Dr8LQ_LZUvjjb3k6aQ5rx9VOt0cSBv0AkJJOa6euDRWpoYz1piS1mrczMuJ7r2VrmBecja9xbFz35cM-5OtK3OrrAycAFbnK8mE_nD2_qjWaa2zLm0Q7ofTK2jFfF_9Pgcna_U",
                isSupport = false
            )
        }
    }
}
