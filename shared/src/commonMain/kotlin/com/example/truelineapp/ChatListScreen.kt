package com.example.truelineapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Search
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
import com.example.truelineapp.network.chat.ChatConversationData
import com.example.truelineapp.ui.TrueLineWaveformLoader

@Composable
fun ChatListScreen(
    conversations: List<ChatConversationData>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onChatClick: (ChatConversationData) -> Unit,
    onBrowseListeners: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        onRefresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrueLineLightBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Messages",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TrueLineDarkBg
            )
        }

        if (isLoading && conversations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                TrueLineWaveformLoader(size = 40.dp)
            }
        } else if (conversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = TrueLineAccent.copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.ChatBubbleOutline,
                                contentDescription = null,
                                tint = TrueLineDarkBg,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "No Conversations Yet",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrueLineDarkBg
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Connect with verified listeners on Discover to chat privately and anonymously.",
                        fontSize = 13.5.sp,
                        color = TrueLineTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onBrowseListeners,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TrueLineAccent)
                    ) {
                        Text(
                            text = "Browse Listeners",
                            fontWeight = FontWeight.Bold,
                            color = TrueLineDarkBg,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(conversations) { chat ->
                    Surface(
                        onClick = { onChatClick(chat) },
                        color = Color.Transparent
                    ) {
                        ChatItem(chat)
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        thickness = 0.6.dp,
                        color = Color(0xFFE2E8F0)
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
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Listener Avatar Box
        Box(
            modifier = Modifier.size(52.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = CircleShape,
                color = TrueLinePrimary.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = chat.partner_name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TrueLinePrimary
                    )
                }
            }
            if (chat.partner_availability == "online") {
                Surface(
                    shape = CircleShape,
                    color = TrueLineOnline,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.BottomEnd)
                ) {}
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.partner_name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrueLineDarkBg
                )
                if (chat.last_message_time.isNotBlank()) {
                    Text(
                        text = formatTimestamp(chat.last_message_time),
                        fontSize = 11.sp,
                        color = TrueLineTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (chat.last_message.isNotBlank()) chat.last_message else "Tap to start conversation...",
                    fontSize = 13.sp,
                    color = if (chat.unread_count > 0) TrueLineDarkBg else TrueLineTextSecondary,
                    fontWeight = if (chat.unread_count > 0) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (chat.unread_count > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = CircleShape,
                        color = TrueLineAccent,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = chat.unread_count.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrueLineDarkBg
                            )
                        }
                    }
                }
            }
        }
    }
}
