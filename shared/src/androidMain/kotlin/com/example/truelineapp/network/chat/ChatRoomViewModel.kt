package com.example.truelineapp.network.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.truelineapp.getCurrentTimeFormatted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessageData>>(emptyList())
    val messages: StateFlow<List<ChatMessageData>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadMessages(partnerId: String) {
        // Step 1: Immediately emit cached messages from Repository
        val cached = repository.getStoredMessages(partnerId)
        _messages.value = cached.reversed()
        
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMessages(partnerId).onSuccess {
                // repository.getMessages already updates the store and returns merged list
                _messages.value = it.reversed()
                markAsRead(partnerId)
            }.onFailure {
                println("DEV MODE: Message fetch failed. API error: ${it.message}")
                // Fallback: If cache is empty, seed it with mock data
                if (repository.getStoredMessages(partnerId).isEmpty()) {
                    val mock = listOf(
                        ChatMessageData("1", "u1", partnerId, "partner", "I am good! Thanks for asking. How can I help you today?", null, "2026-08-11T20:01:00Z"),
                        ChatMessageData("2", "u1", partnerId, "user", "Hi Afreen, how are you?", null, "2026-08-11T20:00:00Z")
                    )
                    // Store mock messages in repository so they persist on send
                    mock.forEach { repository.addOptimisticMessage(partnerId, it) }
                    _messages.value = repository.getStoredMessages(partnerId).reversed()
                }
            }
            _isLoading.value = false
        }
    }

    fun markAsRead(partnerId: String) {
        viewModelScope.launch {
            repository.markAsRead(partnerId).onFailure {
                println("DEV MODE: Mark as read failed. Error: ${it.message}")
            }
        }
    }

    fun sendMessage(partnerId: String, content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            // Correct ISO Format for created_at
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            
            val newMessage = ChatMessageData(
                id = UUID.randomUUID().toString(),
                user_id = "me",
                partner_id = partnerId,
                sender_type = "user",
                content = content,
                created_at = isoFormat.format(Date())
            )
            
            // Persist to repository store immediately to ensure it appends to existing history
            repository.addOptimisticMessage(partnerId, newMessage)
            
            // Emit updated list from store (Preserves all previous messages)
            _messages.value = repository.getStoredMessages(partnerId).reversed()

            repository.sendMessage(partnerId, content).onSuccess {
                // repository.sendMessage already updates the store with the real message
                _messages.value = repository.getStoredMessages(partnerId).reversed()
            }.onFailure {
                println("DEV MODE: Send message failed. Error: ${it.message}")
            }
        }
    }
}
