package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.truelineapp.formatTimestamp
import org.jetbrains.compose.resources.painterResource
import truelineapp.shared.generated.resources.Res
import truelineapp.shared.generated.resources.profile_girl
import com.example.truelineapp.network.chat.ChatMessageData

data class Message(
    val text: String,
    val isFromUser: Boolean,
    val time: String = getCurrentTimeFormatted()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualChatScreen(
    partnerId: String, 
    senderName: String, 
    partnerPhotoUrl: String = "",
    messagesList: List<ChatMessageData> = emptyList(),
    isLoading: Boolean = false,
    onLoadMessages: () -> Unit = {},
    onSendMessage: (String) -> Unit = {},
    onBack: () -> Unit
) {
    var textState by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        onLoadMessages()
    }

    LaunchedEffect(messagesList) {
        messages.clear()
        messages.addAll(messagesList.map { 
            Message(
                text = it.content,
                isFromUser = it.sender_type == "user",
                time = formatTimestamp(it.created_at) // Format ISO or local string
            )
        })
    }
    
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TrueLineLightBg)
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.profile_girl),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = senderName, 
                                fontSize = 18.sp, 
                                fontWeight = FontWeight.Bold,
                                color = TrueLineDarkBg
                            )
                            Text("Online", fontSize = 12.sp, color = TrueLineOnline)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TrueLineLightBg),
                windowInsets = WindowInsets.statusBars
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = TrueLineLightBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = textState,
                        onValueChange = { textState = it },
                        placeholder = { Text("Type a message...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = TrueLinePrimary,
                            focusedTextColor = TrueLineDarkBg,
                            unfocusedTextColor = TrueLineDarkBg
                        ),
                        shape = RoundedCornerShape(28.dp),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FloatingActionButton(
                        onClick = {
                            if (textState.isNotBlank()) {
                                onSendMessage(textState)
                                textState = ""
                            }
                        },
                        containerColor = TrueLineAccent,
                        contentColor = Color.White,
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send, 
                            contentDescription = "Send", 
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(TrueLineLightBg)) {
            if (isLoading && messages.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = TrueLinePrimary
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    reverseLayout = true
                ) {
                    items(messages) { message ->
                        MessageBubble(message)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (message.isFromUser) TrueLinePrimary else Color(0xFFE9EEF0),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 2.dp,
                bottomEnd = if (message.isFromUser) 2.dp else 16.dp
            ),
            tonalElevation = if (message.isFromUser) 0.dp else 2.dp,
            shadowElevation = if (message.isFromUser) 1.dp else 0.5.dp,
            border = if (message.isFromUser) null else BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.2f))
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                color = if (message.isFromUser) Color.White else TrueLineDarkBg,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }
        Text(
            text = message.time,
            fontSize = 11.sp,
            color = Color(0xFF7A8B8A),
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}
