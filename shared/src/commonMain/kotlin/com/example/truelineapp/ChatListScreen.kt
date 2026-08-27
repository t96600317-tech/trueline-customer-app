package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
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
    selectedLanguageCode: String = "en",
    onRefresh: () -> Unit,
    onChatClick: (ChatConversationData) -> Unit,
    onBrowseListeners: () -> Unit = {}
) {
    val strings = com.example.truelineapp.i18n.getAppStrings(selectedLanguageCode)
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        onRefresh()
    }

    val filteredConversations = remember(conversations, searchQuery) {
        if (searchQuery.isBlank()) conversations
        else conversations.filter {
            it.displayName.contains(searchQuery, ignoreCase = true) ||
            it.displayTitle.contains(searchQuery, ignoreCase = true) ||
            it.last_message.contains(searchQuery, ignoreCase = true)
        }
    }

    val unreadCount = conversations.sumOf { it.unread_count }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrueLineLightBg)
    ) {
        // --- HEADER SECTION ---
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = strings.chatsTitle,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrueLineDarkBg
                        )
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TrueLineAccent,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "$unreadCount new",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrueLineDarkBg,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Security/Encryption Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = TrueLinePrimary.copy(alpha = 0.08f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Shield,
                                contentDescription = null,
                                tint = TrueLinePrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Encrypted",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TrueLinePrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Clean Modern Search Bar
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 14.5.sp,
                                color = TrueLineDarkBg,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(TrueLinePrimary),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = strings.searchChatsPlaceholder,
                                        fontSize = 14.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- ACTIVE / ONLINE LISTENERS HORIZONTAL TRAY ---
        if (conversations.isNotEmpty()) {
            val activeListeners = conversations.filter { it.displayAvailability.equals("online", ignoreCase = true) }
            if (activeListeners.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = "ONLINE NOW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrueLineTextSecondary,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(activeListeners) { listener ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onChatClick(listener) }
                                    .padding(4.dp)
                            ) {
                                ListenerAvatar(
                                    name = listener.displayName,
                                    isOnline = true,
                                    fontSize = 18.sp,
                                    modifier = Modifier.size(54.dp),
                                    badgeSize = 13.dp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = listener.displayName.split(" ").firstOrNull() ?: listener.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TrueLineDarkBg,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    thickness = 0.8.dp,
                    color = Color(0xFFF1F5F9)
                )
            }
        }

        // --- MAIN CONVERSATION LIST / EMPTY STATE ---
        if (isLoading && conversations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                TrueLineWaveformLoader(size = 40.dp)
            }
        } else if (filteredConversations.isEmpty()) {
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
                        text = if (searchQuery.isNotBlank()) "No matching conversations" else strings.noConversationsYet,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrueLineDarkBg
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (searchQuery.isNotBlank())
                            "Try searching for another listener name or specialty."
                        else
                            "Connect with verified listeners on Discover to chat privately and anonymously.",
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
                            text = strings.browseListeners,
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
                contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
            ) {
                items(filteredConversations) { chat ->
                    Surface(
                        onClick = { onChatClick(chat) },
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                    ) {
                        ChatItem(chat)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: ChatConversationData) {
    val isOnline = chat.displayAvailability.equals("online", ignoreCase = true)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Listener Rich Gradient Avatar
        ListenerAvatar(
            name = chat.displayName,
            isOnline = isOnline,
            fontSize = 20.sp,
            modifier = Modifier.size(52.dp),
            badgeSize = 14.dp
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // Top Row: Name + Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = chat.displayName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrueLineDarkBg,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (chat.last_message_time.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatTimestamp(chat.last_message_time),
                        fontSize = 12.sp,
                        color = if (chat.unread_count > 0) TrueLinePrimary else Color(0xFF94A3B8),
                        fontWeight = if (chat.unread_count > 0) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Bottom Row: Message preview / Subtitle + Unread Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isSentByUser = chat.last_message_sender.equals("user", ignoreCase = true)
                val previewText = when {
                    chat.last_message.isNotBlank() -> if (isSentByUser) "You: ${chat.last_message}" else chat.last_message
                    chat.displayTitle.isNotBlank() -> chat.displayTitle
                    else -> "Tap to start conversation"
                }

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSentByUser && chat.last_message.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Sent",
                            tint = Color(0xFF64748B),
                            modifier = Modifier
                                .size(14.dp)
                                .padding(end = 3.dp)
                        )
                    }
                    Text(
                        text = previewText,
                        fontSize = 13.5.sp,
                        color = if (chat.unread_count > 0) TrueLineDarkBg else Color(0xFF64748B),
                        fontWeight = if (chat.unread_count > 0) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

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
                                fontSize = 10.5.sp,
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
