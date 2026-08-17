package com.example.truelineapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.truelineapp.formatTimestamp
import org.jetbrains.compose.resources.painterResource
import truelineapp.shared.generated.resources.Res
import truelineapp.shared.generated.resources.profile_girl
import com.example.truelineapp.network.chat.ChatConversationData

private val defaultMockConversations = listOf(
    ChatConversationData(
        partner_id = "1",
        partner_name = "Afreen",
        partner_title = "Joy Helper",
        partner_photo_url = "",
        partner_availability = "online",
        last_message = "Haan bilkul! Feel free to call anytime.",
        last_message_sender = "partner",
        last_message_time = "2026-08-11T20:15:00Z",
        unread_count = 1
    ),
    ChatConversationData(
        partner_id = "2",
        partner_name = "Ahmedi",
        partner_title = "Calm Friend",
        partner_photo_url = "",
        partner_availability = "online",
        last_message = "I'll be online tomorrow at 10 AM.",
        last_message_sender = "partner",
        last_message_time = "2026-08-11T19:15:00Z",
        unread_count = 0
    ),
    ChatConversationData(
        partner_id = "3",
        partner_name = "Saima",
        partner_title = "Calm Friend",
        partner_photo_url = "",
        partner_availability = "online",
        last_message = "Hey! Let's talk soon.",
        last_message_sender = "partner",
        last_message_time = "2026-08-11T18:30:00Z",
        unread_count = 0
    )
)

@Composable
fun ChatListScreen(
    conversations: List<ChatConversationData>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onChatClick: (ChatConversationData) -> Unit
) {
    LaunchedEffect(Unit) {
        onRefresh()
    }
    
    val activeConversations = conversations.ifEmpty { defaultMockConversations }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrueLineLightBg) // Theme background
    ) {
        Text(
            text = "Messages",
            modifier = Modifier.padding(16.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TrueLineDarkBg
        )

        if (isLoading && conversations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TrueLinePrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(activeConversations) { chat ->
                    Surface(
                        onClick = { onChatClick(chat) },
                        color = Color.Transparent
                    ) {
                        ChatItem(chat)
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: ChatConversationData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            ListenerAvatar(
                name = chat.partner_name,
                modifier = Modifier.fillMaxSize(),
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.partner_name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrueLineDarkBg
                )
                Text(
                    text = formatTimestamp(chat.last_message_time),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val prefix = when (chat.last_message_sender) {
                    "user" -> "You: "
                    "partner" -> "${chat.partner_name}: "
                    else -> ""
                }
                Text(
                    text = prefix + chat.last_message,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                if (chat.unread_count > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(TrueLineOnline), // Circular green unread badge
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unread_count.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
