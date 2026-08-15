package com.example.truelineapp.network.chat

import com.example.truelineapp.network.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Response

class ChatRepository(private val apiService: ChatApiService) {

    private val chatMessagesMap = mutableMapOf<String, MutableList<ChatMessageData>>()
    private val conversationsList = mutableListOf<ChatConversationData>()
    
    private val _conversationsFlow = MutableStateFlow<List<ChatConversationData>>(emptyList())
    val conversationsFlow: StateFlow<List<ChatConversationData>> = _conversationsFlow.asStateFlow()

    fun getStoredConversations(): List<ChatConversationData> {
        return conversationsList.toList()
    }

    private fun updateConversationLastMessage(partnerId: String, content: String, senderType: String, time: String) {
        val index = conversationsList.indexOfFirst { it.partner_id == partnerId }
        if (index != -1) {
            val old = conversationsList[index]
            conversationsList[index] = old.copy(
                last_message = content,
                last_message_sender = senderType,
                last_message_time = time
            )
            // Push update to flow
            _conversationsFlow.value = conversationsList.toList()
        }
    }

    fun getStoredMessages(partnerId: String): List<ChatMessageData> {
        return chatMessagesMap[partnerId] ?: emptyList()
    }

    private fun storeMessages(partnerId: String, messages: List<ChatMessageData>) {
        val current = chatMessagesMap.getOrPut(partnerId) { mutableListOf() }
        messages.forEach { msg ->
            if (current.none { it.id == msg.id }) {
                current.add(msg)
            }
        }
    }

    private fun addMessageToStore(partnerId: String, message: ChatMessageData) {
        val current = chatMessagesMap.getOrPut(partnerId) { mutableListOf() }
        if (current.none { it.id == message.id }) {
            current.add(message)
        }
    }

    fun addOptimisticMessage(partnerId: String, message: ChatMessageData) {
        val current = chatMessagesMap.getOrPut(partnerId) { mutableListOf() }
        current.add(message)
        
        // SYNC: Also update the conversation list preview immediately
        updateConversationLastMessage(partnerId, message.content, message.sender_type, message.created_at)
    }

    suspend fun getConversations(): Result<List<ChatConversationData>> {
        return try {
            val response = apiService.getConversations()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    conversationsList.clear()
                    conversationsList.addAll(body.data)
                    _conversationsFlow.value = conversationsList.toList()
                    Result.success(conversationsList)
                } else {
                    Result.failure(Exception(body?.error?.message ?: "Unknown error"))
                }
            } else {
                Result.failure(Exception("HTTP_${response.code()}"))
            }
        } catch (e: Exception) {
            // DEV MODE: Fallback mock data if API fails and list is empty
            if (conversationsList.isEmpty()) {
                val mock = listOf(
                    ChatConversationData(
                        "1", "Afreen", "Joy Helper", "", "online", 
                        "Haan bilkul! Feel free to call anytime.", "partner", "2026-08-11T20:15:00Z", 1
                    ),
                    ChatConversationData(
                        "2", "Ahmedi", "Calm Friend", "", "online", 
                        "I'll be online tomorrow at 10 AM.", "partner", "2026-08-11T19:15:00Z", 0
                    )
                )
                conversationsList.clear()
                conversationsList.addAll(mock)
                _conversationsFlow.value = conversationsList.toList()
                return Result.success(conversationsList)
            }
            Result.failure(e)
        }
    }

    suspend fun getMessages(partnerId: String): Result<List<ChatMessageData>> {
        return try {
            val response = apiService.getMessages(partnerId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    storeMessages(partnerId, body.data)
                    // If we have history, update the last message in conversation list
                    if (body.data.isNotEmpty()) {
                        val last = body.data.first() // Assuming API returns latest first
                        updateConversationLastMessage(partnerId, last.content, last.sender_type, last.created_at)
                    }
                    Result.success(getStoredMessages(partnerId))
                } else {
                    Result.failure(Exception(body?.error?.message ?: "Unknown error"))
                }
            } else {
                Result.failure(Exception("HTTP_${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(partnerId: String, content: String): Result<ChatMessageData> {
        return try {
            val response = apiService.sendMessage(partnerId, mapOf("content" to content))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    addMessageToStore(partnerId, body.data)
                    // SYNC: Update the conversation list immediately
                    updateConversationLastMessage(partnerId, body.data.content, "user", body.data.created_at)
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.error?.message ?: "Unknown error"))
                }
            } else {
                Result.failure(Exception("HTTP_${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsRead(partnerId: String): Result<Map<String, String>> {
        return try {
            val response = apiService.markAsRead(partnerId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.error?.message ?: "Unknown error"))
                }
            } else {
                Result.failure(Exception("HTTP_${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
