package com.example.truelineapp.network.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatConversationData(
    val partner_id: String,
    val partner_name: String,
    val partner_title: String,
    val partner_photo_url: String,
    val partner_availability: String,
    val last_message: String,
    val last_message_sender: String,
    val last_message_time: String,
    val unread_count: Int
)

@Serializable
data class ChatMessageData(
    val id: String,
    val user_id: String,
    val partner_id: String,
    val sender_type: String,
    val content: String,
    val read_at: String? = null,
    val created_at: String
)
