package com.raheygaay.app.ui.screens.sahby

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raheygaay.app.R
import com.raheygaay.app.data.local.db.SahbyDao
import com.raheygaay.app.data.local.db.SahbyMessageEntity
import com.raheygaay.app.data.local.db.SahbyThreadEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SahbyUiState(
    val messages: List<SahbyMessageEntity> = emptyList(),
    val threads: List<SahbyThreadEntity> = emptyList(),
    val currentThreadId: String? = null,
    val input: String = "",
    val isTyping: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SahbyViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sahbyDao: SahbyDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(SahbyUiState())
    val uiState: StateFlow<SahbyUiState> = _uiState.asStateFlow()
    private val currentThreadId = MutableStateFlow<String?>(null)

    init {
        observeThreads()
        observeMessages()
    }

    fun updateInput(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    fun clearChat() {
        val threadId = currentThreadId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            sahbyDao.clearMessages(threadId)
            sahbyDao.updateThread(threadId, null, System.currentTimeMillis())
        }
        _uiState.update { it.copy(input = "", isTyping = false) }
    }

    fun newChat() {
        viewModelScope.launch {
            val threadId = createThread()
            currentThreadId.value = threadId
            _uiState.update { it.copy(currentThreadId = threadId) }
        }
    }

    fun selectThread(threadId: String) {
        currentThreadId.value = threadId
        _uiState.update { it.copy(currentThreadId = threadId) }
    }

    fun sendMessage(message: String? = null) {
        val text = message?.trim() ?: _uiState.value.input.trim()
        if (text.isBlank()) return
        _uiState.update { it.copy(input = "", isTyping = true) }
        viewModelScope.launch {
            val threadId = ensureThreadId()
            val now = System.currentTimeMillis()
            withContext(Dispatchers.IO) {
                val userMessage = SahbyMessageEntity(
                    id = UUID.randomUUID().toString(),
                    threadId = threadId,
                    role = ROLE_USER,
                    content = text,
                    createdAt = now
                )
                sahbyDao.insertMessage(userMessage)
                sahbyDao.updateThread(threadId, text, now)
            }

            val reply = generateResponse(text)
            withContext(Dispatchers.IO) {
                val assistantMessage = SahbyMessageEntity(
                    id = UUID.randomUUID().toString(),
                    threadId = threadId,
                    role = ROLE_ASSISTANT,
                    content = reply,
                    createdAt = now + 1
                )
                sahbyDao.insertMessage(assistantMessage)
                sahbyDao.updateThread(threadId, reply, System.currentTimeMillis())
            }
            _uiState.update { it.copy(isTyping = false) }
        }
    }

    private fun generateResponse(input: String): String {
        val isArabic = input.any { ch -> ch in '\u0600'..'\u06FF' || ch in '\u0750'..'\u077F' || ch in '\u08A0'..'\u08FF' }
        val lower = input.lowercase()

        fun containsAny(vararg words: String): Boolean = words.any { lower.contains(it) }

        return when {
            containsAny("hi", "hello", "hey", "السلام", "مرحبا", "اهلاً", "أهلاً", "هاي") -> {
                if (isArabic) context.getString(R.string.sahby_reply_greeting_ar) else context.getString(R.string.sahby_reply_greeting)
            }
            containsAny("price", "cost", "fee", "fees", "charge", "pricing", "سعر", "تكلفة", "رسوم") -> {
                if (isArabic) context.getString(R.string.sahby_reply_price_ar) else context.getString(R.string.sahby_reply_price)
            }
            containsAny("verify", "verified", "verification", "id", "identity", "توثيق", "تحقق", "هوية", "بطاقة") -> {
                if (isArabic) context.getString(R.string.sahby_reply_verification_ar) else context.getString(R.string.sahby_reply_verification)
            }
            containsAny("safe", "safety", "secure", "trust", "أمان", "سلامة", "موثوق") -> {
                if (isArabic) context.getString(R.string.sahby_reply_safety_ar) else context.getString(R.string.sahby_reply_safety)
            }
            containsAny("map", "search", "find", "route", "خريطة", "بحث", "مسار", "طريق") -> {
                if (isArabic) context.getString(R.string.sahby_reply_map_ar) else context.getString(R.string.sahby_reply_map)
            }
            containsAny("trip", "travel", "delivery", "package", "parcel", "ship", "رحلة", "توصيل", "شحنة", "طرد") -> {
                if (isArabic) context.getString(R.string.sahby_reply_trip_ar) else context.getString(R.string.sahby_reply_trip)
            }
            containsAny("rating", "review", "stars", "تقييم", "مراجعة") -> {
                if (isArabic) context.getString(R.string.sahby_reply_rating_ar) else context.getString(R.string.sahby_reply_rating)
            }
            containsAny("support", "help", "contact", "problem", "issue", "دعم", "مساعدة", "تواصل", "مشكلة") -> {
                if (isArabic) context.getString(R.string.sahby_reply_support_ar) else context.getString(R.string.sahby_reply_support)
            }
            containsAny("profile", "account", "settings", "حساب", "ملف", "إعدادات") -> {
                if (isArabic) context.getString(R.string.sahby_reply_profile_ar) else context.getString(R.string.sahby_reply_profile)
            }
            else -> if (isArabic) context.getString(R.string.sahby_reply_fallback_ar) else context.getString(R.string.sahby_reply_fallback)
        }
    }

    companion object {
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
    }

    private fun observeThreads() {
        viewModelScope.launch {
            sahbyDao.observeThreads().collect { threads ->
                _uiState.update { it.copy(threads = threads) }
                if (currentThreadId.value == null && threads.isNotEmpty()) {
                    currentThreadId.value = threads.first().id
                    _uiState.update { it.copy(currentThreadId = threads.first().id) }
                }
                if (currentThreadId.value == null && threads.isEmpty()) {
                    newChat()
                }
            }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            currentThreadId.filterNotNull()
                .flatMapLatest { threadId -> sahbyDao.observeMessages(threadId) }
                .collect { messages ->
                    _uiState.update { it.copy(messages = messages, currentThreadId = currentThreadId.value) }
                }
        }
    }

    private fun buildThreadTitle(): String {
        val index = (_uiState.value.threads.size + 1).coerceAtLeast(1)
        return context.getString(R.string.sahby_thread_title, index)
    }

    private suspend fun ensureThreadId(): String {
        val existing = currentThreadId.value
        if (existing != null) return existing
        val created = createThread()
        currentThreadId.value = created
        _uiState.update { it.copy(currentThreadId = created) }
        return created
    }

    private suspend fun createThread(): String = withContext(Dispatchers.IO) {
        val threadId = UUID.randomUUID().toString()
        val title = buildThreadTitle()
        val now = System.currentTimeMillis()
        val thread = SahbyThreadEntity(
            id = threadId,
            title = title,
            lastMessage = null,
            updatedAt = now
        )
        sahbyDao.upsertThread(thread)
        threadId
    }
}
