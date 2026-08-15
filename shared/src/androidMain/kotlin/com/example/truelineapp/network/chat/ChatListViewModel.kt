package com.example.truelineapp.network.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatListViewModel(private val repository: ChatRepository) : ViewModel() {

    val conversations: StateFlow<List<ChatConversationData>> = repository.conversationsFlow

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchConversations()
    }

    fun fetchConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getConversations().onFailure {
                println("DEV MODE: Chat list fetch failed. API error: ${it.message}")
                // In production, we might show an error. 
                // For dev, repository already handles mock data or flow emission.
            }
            _isLoading.value = false
        }
    }
}
