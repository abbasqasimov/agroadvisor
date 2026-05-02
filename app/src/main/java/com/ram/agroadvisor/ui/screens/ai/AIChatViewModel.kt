package com.ram.agroadvisor.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ram.agroadvisor.data.model.ChatMessageDto
import com.ram.agroadvisor.data.model.ChatRequestDto
import com.ram.agroadvisor.data.remote.AgroApi
import com.ram.agroadvisor.data.remote.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.Instant
import javax.inject.Inject

/**
 * Owns the AI chat session — the current message list, the active session id,
 * and the list of past sessions for the side drawer. The screen is driven by
 * the `messages`, `sessions`, and `state` flows exposed here.
 */
@HiltViewModel
class AIChatViewModel @Inject constructor(
    private val agroApi: AgroApi
) : ViewModel() {

    sealed class State {
        data object Idle : State()
        data object Sending : State()
        data object LoadingHistory : State()
        data class Error(val message: String) : State()
    }

    private val _messages = MutableStateFlow<List<ChatMessageDto>>(emptyList())
    val messages: StateFlow<List<ChatMessageDto>> = _messages

    private val _sessions = MutableStateFlow<List<String>>(emptyList())
    val sessions: StateFlow<List<String>> = _sessions

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state

    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId: StateFlow<String?> = _activeSessionId

    init {
        refreshSessions()
    }

    fun startNewChat() {
        _activeSessionId.value = null
        _messages.value = emptyList()
        _state.value = State.Idle
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            // Optimistic user-side echo so the UI feels instant.
            val nowIso = Instant.now().toString()
            _messages.value = _messages.value + ChatMessageDto(
                role = "user",
                content = trimmed,
                createdAtUtc = nowIso
            )
            _state.value = State.Sending
            try {
                val resp = agroApi.chat(
                    ChatRequestDto(
                        sessionId = _activeSessionId.value,
                        message = trimmed
                    )
                )
                _activeSessionId.value = resp.sessionId
                // Trust the server's history as the source of truth.
                _messages.value = resp.history
                _state.value = State.Idle

                // If this was a brand-new session, surface it in the drawer.
                if (_sessions.value.none { it == resp.sessionId }) {
                    _sessions.value = listOf(resp.sessionId) + _sessions.value
                }
            } catch (e: HttpException) {
                _state.value = State.Error(ApiErrorParser.parse(e, "Xəta: ${e.code()}"))
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "Bağlantı xətası")
            }
        }
    }

    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            _state.value = State.LoadingHistory
            try {
                val history = agroApi.getChatHistory(sessionId)
                _activeSessionId.value = sessionId
                _messages.value = history
                _state.value = State.Idle
            } catch (e: HttpException) {
                _state.value = State.Error(ApiErrorParser.parse(e, "Tarix yüklənmədi"))
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "Tarix yüklənmədi")
            }
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            try {
                agroApi.deleteChatSession(sessionId)
                _sessions.value = _sessions.value.filterNot { it == sessionId }
                if (_activeSessionId.value == sessionId) {
                    startNewChat()
                }
            } catch (e: HttpException) {
                _state.value = State.Error(ApiErrorParser.parse(e, "Silinmədi"))
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "Silinmədi")
            }
        }
    }

    fun refreshSessions() {
        viewModelScope.launch {
            try {
                _sessions.value = agroApi.getChatSessions()
            } catch (_: Exception) {
                // Silent — the drawer just stays empty if this fails.
            }
        }
    }

    fun clearError() {
        if (_state.value is State.Error) _state.value = State.Idle
    }
}
