package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.truelineapp.network.chat.ChatMessageData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualChatScreen(
    partnerId: String,
    senderName: String,
    partnerTitle: String = "",
    partnerPhotoUrl: String = "",
    messagesList: List<ChatMessageData> = emptyList(),
    isLoading: Boolean = false,
    onLoadMessages: () -> Unit = {},
    onSendMessage: (String) -> Unit = {},
    onCallClick: () -> Unit = {},
    onBack: () -> Unit
) {
    var textState by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(partnerId) {
        onLoadMessages()
    }

    LaunchedEffect(messagesList.size) {
        if (messagesList.isNotEmpty()) {
            listState.animateScrollToItem(messagesList.size - 1)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TrueLinePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = senderName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TrueLineDarkBg
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = senderName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrueLineDarkBg
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(7.dp),
                                    shape = CircleShape,
                                    color = TrueLineOnline
                                ) {}
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (partnerTitle.isNotBlank()) partnerTitle else "Online",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TrueLinePrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCallClick) {
                        Surface(
                            shape = CircleShape,
                            color = TrueLinePrimary.copy(alpha = 0.1f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = "Call",
                                    tint = TrueLinePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = textState,
                        onValueChange = { textState = it },
                        placeholder = { Text("Type a message...", color = Color.Gray, fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 46.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            disabledContainerColor = Color(0xFFF1F5F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (textState.isNotBlank()) {
                                onSendMessage(textState.trim())
                                textState = ""
                            }
                        },
                        modifier = Modifier.size(46.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (textState.isNotBlank()) TrueLinePrimary else TrueLinePrimary.copy(alpha = 0.4f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Encryption Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Messages are anonymous & end-to-end encrypted",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            if (isLoading && messagesList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TrueLinePrimary, strokeWidth = 2.5.dp)
                }
            } else if (messagesList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Text(
                            text = "Say hello to $senderName 👋",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrueLineDarkBg
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Share what's on your mind. Everything stays confidential.",
                            fontSize = 13.sp,
                            color = TrueLineTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(messagesList) { msg ->
                        val isFromUser = msg.sender_type == "user"
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isFromUser) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isFromUser) 16.dp else 4.dp,
                                    bottomEnd = if (isFromUser) 4.dp else 16.dp
                                ),
                                color = if (isFromUser) TrueLinePrimary else Color.White,
                                border = if (isFromUser) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                shadowElevation = 1.dp,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    Text(
                                        text = msg.content,
                                        color = if (isFromUser) Color.White else TrueLineDarkBg,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = msg.created_at.takeLast(8).take(5),
                                        color = if (isFromUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                        fontSize = 10.sp,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
