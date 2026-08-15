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

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TrueLinePrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(conversations) { chat ->
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
                .clip(CircleShape)
                .background(TrueLineLightBg),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.profile_girl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
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
