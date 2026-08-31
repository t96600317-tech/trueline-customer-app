package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.truelineapp.network.chat.ChatMessageData
import com.example.truelineapp.ui.TrueLineWaveformLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualChatScreen(
    partnerId: String,
    senderName: String,
    partnerTitle: String = "",
    partnerPhotoUrl: String = "",
    messagesList: List<ChatMessageData> = emptyList(),
    isLoading: Boolean = false,
    userWalletBalance: Double = 0.0,
    onLoadMessages: () -> Unit = {},
    onSendMessage: (String) -> Unit = {},
    onRechargeClick: () -> Unit = {},
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
        containerColor = TrueLineLightBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(TrueLinePrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = senderName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = TrueLinePrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = senderName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrueLineDarkBg,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 1.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(6.5.dp),
                                    shape = CircleShape,
                                    color = TrueLineOnline
                                ) {}
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (partnerTitle.isNotBlank() && !partnerTitle.contains("•") && partnerTitle.length <= 15) {
                                        "Online · $partnerTitle"
                                    } else {
                                        "Online"
                                    },
                                    fontSize = 11.5.sp,
                                    color = TrueLineOnline,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
                            tint = TrueLineDarkBg
                        )
                    }
                },
                actions = {
                    Surface(
                        onClick = onRechargeClick,
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CoinLogo(size = 14.dp)
                            Spacer(modifier = Modifier.width(4.dp))
                            val displayBal = if (userWalletBalance % 1.0 == 0.0) {
                                "${userWalletBalance.toLong()}"
                            } else {
                                ((userWalletBalance * 10).toLong() / 10.0).toString()
                            }
                            Text(
                                text = displayBal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = TrueLineDarkBg
                            )
                        }
                    }

                    IconButton(
                        onClick = onCallClick,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TrueLineAccent,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = "Call",
                                    tint = TrueLineDarkBg,
                                    modifier = Modifier.size(17.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    // Cost / Balance Info Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp, start = 4.dp, end = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CoinLogo(size = 12.dp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "0.3 coins / message",
                                fontSize = 11.5.sp,
                                color = TrueLineTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (userWalletBalance < 0.3) {
                            Text(
                                text = "Low Balance · Tap to Recharge",
                                fontSize = 11.5.sp,
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onRechargeClick() }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = textState,
                            onValueChange = { textState = it },
                            placeholder = { Text("Type a message (0.3 coins)...", color = Color(0xFFA0AEC0), fontSize = 14.sp) },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 14.5.sp,
                                color = TrueLineDarkBg,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = TrueLineDarkBg,
                                unfocusedTextColor = TrueLineDarkBg,
                                focusedContainerColor = Color(0xFFF1F5F9),
                                unfocusedContainerColor = Color(0xFFF1F5F9),
                                disabledContainerColor = Color(0xFFF1F5F9),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = TrueLinePrimary
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (textState.isNotBlank()) {
                                    if (userWalletBalance < 0.3) {
                                        onRechargeClick()
                                    } else {
                                        onSendMessage(textState.trim())
                                        textState = ""
                                    }
                                }
                            },
                            modifier = Modifier.size(46.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (textState.isNotBlank()) TrueLineAccent else TrueLineAccent.copy(alpha = 0.35f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = TrueLineDarkBg,
                                modifier = Modifier.size(20.dp)
                            )
                        }
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
                Icon(Icons.Filled.Lock, contentDescription = null, tint = TrueLineTextSecondary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Messages are 100% anonymous & encrypted",
                    fontSize = 11.sp,
                    color = TrueLineTextSecondary
                )
            }

            if (isLoading && messagesList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TrueLineWaveformLoader(size = 38.dp)
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
                                        text = formatTimestamp(msg.created_at),
                                        color = if (isFromUser) Color.White.copy(alpha = 0.75f) else Color(0xFF64748B),
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium,
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
