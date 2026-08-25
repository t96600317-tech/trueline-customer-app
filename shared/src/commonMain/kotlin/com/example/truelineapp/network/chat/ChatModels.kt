package com.example.truelineapp.network.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatConversationData(
    val partner_id: String = "",
    val partner_name: String = "",
    val partner_title: String = "",
    val partner_photo_url: String = "",
    val partner_availability: String = "online",
    val listener_id: String = "",
    val listener_name: String = "",
    val listener_title: String = "",
    val listener_photo_url: String = "",
    val listener_availability: String = "online",
    val user_id: String = "",
    val user_name: String = "",
    val user_title: String = "",
    val user_photo_url: String = "",
    val user_availability: String = "online",
    val last_message: String = "",
    val last_message_sender: String = "",
    val last_message_time: String = "",
    val unread_count: Int = 0
) {
    val displayId: String
        get() = partner_id.ifBlank { listener_id.ifBlank { user_id } }

    val displayName: String
        get() = partner_name.ifBlank { listener_name.ifBlank { user_name.ifBlank { "Listener" } } }

    val displayTitle: String
        get() = partner_title.ifBlank { listener_title.ifBlank { user_title } }

    val displayPhotoUrl: String
        get() = partner_photo_url.ifBlank { listener_photo_url.ifBlank { user_photo_url } }

    val displayAvailability: String
        get() = partner_availability.ifBlank { listener_availability.ifBlank { user_availability } }
}

@Serializable
data class ChatMessageData(
    val id: String = "",
    val user_id: String = "",
    val partner_id: String = "",
    val listener_id: String = "",
    val sender_type: String = "user",
    val content: String = "",
    val moderation_status: String = "",
    val read_at: String? = null,
    val created_at: String = ""
) {
    val targetPartnerId: String
        get() = partner_id.ifBlank { listener_id }
}
