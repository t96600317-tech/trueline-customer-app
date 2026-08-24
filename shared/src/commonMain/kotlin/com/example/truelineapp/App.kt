package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.Pause
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.jetbrains.compose.resources.painterResource
import truelineapp.shared.generated.resources.Res
import truelineapp.shared.generated.resources.profile_girl
import com.example.truelineapp.network.ListenerDiscovery
import com.example.truelineapp.network.chat.ChatConversationData
import com.example.truelineapp.network.chat.ChatMessageData

@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()
    val viewModel = remember { MainViewModel(scope) }

    TrueLineTheme {
        val navController = rememberNavController()

        LaunchedEffect(viewModel.showPostCallRating) {
            if (viewModel.showPostCallRating) {
                try {
                    navController.navigate("post_call_rating") {
                        popUpTo("main/0") { inclusive = false }
                    }
                } catch (e: Exception) {}
            }
        }
        
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = if (viewModel.isAuthSuccess) "main/0" else "onboarding"
            ) {
                composable("onboarding") {
                    OnboardingScreen(
                        onGetStarted = {
                            navController.navigate("login")
                        }
                    )
                }
                composable("login") {
                    LoginScreen(
                        isLoading = viewModel.isLoading,
                        isSuccess = viewModel.isAuthSuccess,
                        errorMessage = viewModel.errorMessage,
                        otpCountdown = viewModel.otpCountdown,
                        canResendOtp = viewModel.canResendOtp,
                        onSendOtp = { phone, onSuccess -> viewModel.sendOtp(phone, onSuccess) },
                        onVerifyOtp = { phone, otp ->
                            viewModel.verifyOtp(phone, otp) {
                                navController.navigate("main/0") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        },
                        onResendOtp = { viewModel.resendOtp() },
                        onLoginSuccess = {
                            navController.navigate("main/0") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(
                    route = "main/{tab}",
                    arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 })
                ) { backStackEntry ->
                    val initialTab = backStackEntry.arguments?.getInt("tab") ?: 0
                    MainScreen(
                        initialTab = initialTab,
                        walletBalance = viewModel.walletBalance.toInt(),
                        userName = "User #${viewModel.userId}",
                        isFirstTimeNameChange = false,
                        selectedLanguageCode = viewModel.selectedLanguage,
                        partners = viewModel.partners,
                        isDiscoverLoading = viewModel.isDiscoverLoading,
                        searchQueryInitial = viewModel.searchQuery,
                        selectedDiscoverLanguage = viewModel.selectedDiscoverLanguage,
                        conversations = viewModel.conversations,
                        isChatListLoading = viewModel.isChatListLoading,
                        playingAudioUrl = viewModel.playingAudioUrl,
                        onChatClick = { partner ->
                            navController.navigate("chat_detail/${partner.partner_id}")
                        },
                        onNavigateToWallet = {
                            navController.navigate("wallet")
                        },
                        onAddCoins = { _ ->
                            navController.navigate("wallet")
                        },
                        onUpdateProfile = { _, _ -> },
                        onLanguageUpdate = { code ->
                            viewModel.updateLanguage(code)
                        },
                        onSearchChanged = { viewModel.onSearchChanged(it) },
                        onDiscoverLanguageSelected = { viewModel.onDiscoverLanguageSelected(it) },
                        onRefreshChatList = { viewModel.fetchConversations() },
                        onPlayAudio = { viewModel.toggleAudioPlayback(it) },
                        onConnectToListener = { listenerId ->
                            val partner = viewModel.partners.find { it.id == listenerId }
                            val partnerName = partner?.name ?: "Listener"
                            val roomId = "call_${listenerId.replace("-", "").take(16)}"
                            viewModel.connectToListener(listenerId)
                            com.example.truelineapp.call.getCallService().startAudioCall(
                                roomId = roomId,
                                targetUserId = listenerId,
                                targetUserName = partnerName,
                                token = "",
                                onCallEnd = {
                                    viewModel.onCallFinished(180)
                                }
                            )
                        },
                        onLogout = {
                            viewModel.logout {
                                navController.navigate("onboarding") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
                composable("wallet") {
                    WalletScreen(
                        balance = viewModel.walletBalance.toInt(),
                        isProcessing = viewModel.isPaymentProcessing,
                        transactions = viewModel.transactions,
                        isTransactionsLoading = viewModel.isTransactionsLoading,
                        onBack = { navController.popBackStack() },
                        onRecharge = { paise, coins ->
                            viewModel.initiateRecharge(paise, coins)
                        },
                        onRefreshTransactions = { viewModel.fetchTransactions() }
                    )
                }
                composable(
                    route = "audio_call/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    val name = viewModel.partners.find { it.id == id }?.name 
                        ?: viewModel.currentCallingPartner?.name 
                        ?: "Listener"
                    AudioCallScreen(
                        listenerName = name,
                        onHangUp = {
                            viewModel.onCallFinished(180)
                            navController.navigate("post_call_rating") {
                                popUpTo("audio_call/{id}") { inclusive = true }
                            }
                        }
                    )
                }
                composable("post_call_rating") {
                    val partnerName = viewModel.currentCallingPartner?.name ?: "Listener"
                    PostCallRatingScreen(
                        listenerName = partnerName,
                        callDurationSeconds = viewModel.lastCallDuration,
                        coinsDeducted = viewModel.lastCallCoinsDeducted,
                        onSubmit = { rating, tags, isFavorite ->
                            viewModel.submitRating(rating, tags, isFavorite)
                            navController.popBackStack("main/0", inclusive = false)
                        },
                        onSkip = {
                            viewModel.dismissRating()
                            navController.popBackStack("main/0", inclusive = false)
                        }
                    )
                }
                composable(
                    route = "chat_detail/{id}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    val partner = viewModel.partners.find { it.id == id }
                        ?: viewModel.conversations.find { it.partner_id == id }?.let {
                            ListenerDiscovery(
                                id = it.partner_id,
                                name = it.partner_name,
                                title = it.partner_title,
                                photo_url = it.partner_photo_url,
                                availability = it.partner_availability
                            )
                        }
                    val name = partner?.name ?: "Listener"
                    val title = partner?.title ?: ""
                    val photoUrl = partner?.photo_url ?: ""
                    IndividualChatScreen(
                        partnerId = id,
                        senderName = name,
                        partnerTitle = title,
                        partnerPhotoUrl = photoUrl,
                        messagesList = viewModel.currentChatMessages,
                        isLoading = viewModel.isChatMessagesLoading,
                        onLoadMessages = { viewModel.openChatRoom(id) },
                        onSendMessage = { content -> viewModel.sendChatMessage(id, content) },
                        onCallClick = {
                            val roomId = "call_${id.replace("-", "").take(16)}"
                            viewModel.connectToListener(id)
                            com.example.truelineapp.call.getCallService().startAudioCall(
                                roomId = roomId,
                                targetUserId = id,
                                targetUserName = name,
                                token = "",
                                onCallEnd = {
                                    viewModel.onCallFinished(180)
                                }
                            )
                        },
                        onBack = {
                            navController.navigate("main/1") {
                                popUpTo("main/0") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    initialTab: Int, 
    walletBalance: Int,
    userName: String,
    isFirstTimeNameChange: Boolean,
    selectedLanguageCode: String,
    partners: List<ListenerDiscovery>,
    isDiscoverLoading: Boolean,
    searchQueryInitial: String,
    selectedDiscoverLanguage: String,
    playingAudioUrl: String?,
    conversations: List<ChatConversationData>,
    isChatListLoading: Boolean,
    onChatClick: (ChatConversationData) -> Unit,
    onNavigateToWallet: () -> Unit,
    onAddCoins: (Int) -> Unit,
    onUpdateProfile: (String, Int) -> Unit,
    onLanguageUpdate: (String) -> Unit,
    onSearchChanged: (String) -> Unit,
    onDiscoverLanguageSelected: (String) -> Unit,
    onRefreshChatList: () -> Unit,
    onPlayAudio: (url: String) -> Unit,
    onConnectToListener: (String) -> Unit,
    onLogout: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf(searchQueryInitial) }
    val languages = listOf("All", "Hindi", "Bhojpuri", "Bengali", "Tamil", "Telugu", "Marathi", "Punjabi")
    
    var showAddCoinsSheet by remember { mutableStateOf(false) }
    var pendingListenerName by remember { mutableStateOf("") }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var selectedProfilePartner by remember { mutableStateOf<ListenerDiscovery?>(null) }

    var selectedTab by rememberSaveable { mutableIntStateOf(initialTab) }

    LaunchedEffect(Unit) {
        onSearchChanged(searchQueryInitial)
    }

    LaunchedEffect(searchQueryInitial) {
        searchQuery = searchQueryInitial
    }

    // Middle Screen Popup Dialog for Listener Profile Details & Actions (Call / Message)
    if (selectedProfilePartner != null) {
        val partner = selectedProfilePartner!!
        Dialog(onDismissRequest = { selectedProfilePartner = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close icon button at top right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { selectedProfilePartner = null },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text("✕", fontSize = 16.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Circular Avatar with Live Online/Offline Badge
                    Box(
                        modifier = Modifier.size(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(82.dp),
                            shape = CircleShape,
                            color = SurfaceElevated,
                            border = BorderStroke(2.dp, BorderSubtle),
                            shadowElevation = 2.dp
                        ) {
                            ListenerAvatar(
                                name = partner.name,
                                modifier = Modifier.fillMaxSize(),
                                fontSize = 32.sp,
                                backgroundColor = Primary.copy(alpha = 0.12f),
                                textColor = Primary
                            )
                        }

                        val isOnline = partner.availability.equals("online", ignoreCase = true)
                        Surface(
                            color = if (isOnline) OnlineSuccess else Color(0xFF94A3B8),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                        ) {
                            Text(
                                text = if (isOnline) "ONLINE" else "OFFLINE",
                                color = Color.White,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Name
                    Text(
                        text = partner.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Title
                    if (partner.title.isNotBlank()) {
                        Text(
                            text = partner.title,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Languages
                    if (partner.languages.isNotEmpty()) {
                        Text(
                            text = "🗣 " + partner.languages.joinToString(", "),
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Rating & Rate Info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = SurfaceElevated,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Accent, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${partner.rating_avg} (${partner.rating_count} reviews)",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        Surface(
                            color = Accent.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "🪙 ${partner.rate_per_min}/min",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Accent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Bio Description if available
                    if (partner.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = partner.bio,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons: Message & Call
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Message Button
                        Button(
                            onClick = {
                                val p = partner
                                selectedProfilePartner = null
                                onChatClick(ChatConversationData(
                                    partner_id = p.id,
                                    partner_name = p.name,
                                    partner_title = p.title,
                                    partner_photo_url = p.photo_url,
                                    partner_availability = p.availability,
                                    last_message = "",
                                    last_message_sender = "",
                                    last_message_time = "",
                                    unread_count = 0
                                ))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceElevated
                            ),
                            border = BorderStroke(1.2.dp, BorderSubtle)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Chat,
                                    contentDescription = "Message",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Message",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        // Call Button
                        Button(
                            onClick = {
                                val p = partner
                                selectedProfilePartner = null
                                if (walletBalance >= p.rate_per_min) {
                                    onConnectToListener(p.id)
                                } else {
                                    pendingListenerName = p.name
                                    showAddCoinsSheet = true
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Accent
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Call",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCoinsSheet) {
        AddCoinsBottomSheet(
            listenerName = pendingListenerName,
            currentBalance = walletBalance,
            onDismiss = { showAddCoinsSheet = false },
            onAddCoins = { pkg ->
                onAddCoins(pkg.coins)
                showAddCoinsSheet = false
            }
        )
    }

    if (showLanguageSheet) {
        LanguageSelectionBottomSheet(
            selectedLanguageCode = selectedLanguageCode,
            onDismiss = { showLanguageSheet = false },
            onLanguageSelected = onLanguageUpdate
        )
    }


    Scaffold(
        topBar = {
            Surface(
                color = SurfaceWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                TopAppBar(
                    title = {
                        TrueLineBrandHeader(
                            logoSize = 34.dp,
                            titleSize = 20.sp
                        )
                    },
                    actions = {
                        Surface(
                            onClick = onNavigateToWallet,
                            shape = RoundedCornerShape(20.dp),
                            color = SurfaceElevated,
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CoinLogo(size = 18.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = walletBalance.toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SurfaceWhite,
                        titleContentColor = TextPrimary
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                color = SurfaceWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                NavigationBar(
                    containerColor = SurfaceWhite,
                    tonalElevation = 0.dp
                ) {
                    val totalUnread = conversations.sumOf { it.unread_count }

                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(if (selectedTab == 0) Icons.Filled.Call else Icons.Outlined.Call, contentDescription = "Call") },
                        label = { Text("Call", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = Primary.copy(alpha = 0.08f)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            if (totalUnread > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = Danger,
                                            contentColor = Color.White
                                        ) {
                                            Text(totalUnread.toString())
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (selectedTab == 1) Icons.AutoMirrored.Filled.Chat else Icons.AutoMirrored.Outlined.Chat,
                                        contentDescription = "Chat"
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (selectedTab == 1) Icons.AutoMirrored.Filled.Chat else Icons.AutoMirrored.Outlined.Chat,
                                    contentDescription = "Chat"
                                )
                            }
                        },
                        label = { Text("Chat", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = Primary.copy(alpha = 0.08f)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = Primary.copy(alpha = 0.08f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Light)) {
            when (selectedTab) {
                0 -> {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            onSearchChanged(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        placeholder = { Text("Search listeners by name or language...", color = TextMuted, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = SurfaceWhite,
                            unfocusedContainerColor = SurfaceWhite,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = Primary
                        )
                    )

                    // Language Filters
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(languages) { lang ->
                            val isSelected = selectedDiscoverLanguage == lang
                            FilterChip(
                                selected = isSelected,
                                onClick = { onDiscoverLanguageSelected(lang) },
                                label = {
                                    Text(
                                        text = lang,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceWhite,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = BorderSubtle,
                                    selectedBorderColor = Primary,
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.dp
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }

                    // Listeners List
                    if (isDiscoverLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            com.example.truelineapp.ui.TrueLineWaveformLoader(size = 44.dp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp, top = 4.dp)
                        ) {
                            items(partners) { partner ->
                                ExactReplicaCard(
                                    partner = partner, 
                                    isPlaying = playingAudioUrl == partner.audio_sample_url,
                                    onCardClick = { 
                                        selectedProfilePartner = partner
                                    },
                                    onPlayClick = { onPlayAudio(partner.audio_sample_url) },
                                    onConnectClick = {
                                        if (walletBalance >= partner.rate_per_min) {
                                            onConnectToListener(partner.id)
                                        } else {
                                            pendingListenerName = partner.name
                                            showAddCoinsSheet = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    ChatListScreen(
                        conversations = conversations,
                        isLoading = isChatListLoading,
                        onRefresh = onRefreshChatList,
                        onChatClick = onChatClick,
                        onBrowseListeners = { selectedTab = 0 }
                    )
                }
                2 -> {
                    UserProfileScreen(
                        userName = userName,
                        walletBalance = walletBalance,
                        isFirstTimeNameChange = isFirstTimeNameChange,
                        selectedLanguageCode = selectedLanguageCode,
                        onLogout = onLogout,
                        onAddCoins = { showAddCoinsSheet = true },
                        onUpdateProfile = onUpdateProfile,
                        onLanguageClick = { showLanguageSheet = true }
                    )
                }
            }
        }
    }
}

@Composable
fun ExactReplicaCard(
    partner: ListenerDiscovery, 
    isPlaying: Boolean,
    onCardClick: () -> Unit,
    onPlayClick: () -> Unit,
    onConnectClick: () -> Unit
) {
    // OUTER CARD
    Surface(
        onClick = onCardClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        color = SurfaceWhite,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BorderSubtle),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LEFT: Circular Avatar Section
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Circular Image / Avatar with subtle border
                Surface(
                    modifier = Modifier.size(92.dp),
                    shape = CircleShape,
                    color = SurfaceElevated,
                    border = BorderStroke(2.dp, BorderSubtle),
                    shadowElevation = 2.dp
                ) {
                    ListenerAvatar(
                        name = partner.name,
                        modifier = Modifier.fillMaxSize(),
                        fontSize = 34.sp,
                        backgroundColor = Primary.copy(alpha = 0.12f),
                        textColor = Primary
                    )
                }

                // Availability Badge (ONLINE / OFFLINE)
                val isOnline = partner.availability.equals("online", ignoreCase = true)
                Surface(
                    color = if (isOnline) OnlineSuccess else Color(0xFF94A3B8),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                ) {
                    Text(
                        text = if (isOnline) "ONLINE" else "OFFLINE",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Rating Badge (Bottom Position)
                Surface(
                    color = SurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Accent, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${partner.rating_avg} (${partner.rating_count})", 
                            color = TextPrimary, 
                            fontSize = 10.5.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // RIGHT: Details & Action Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Info & Play Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = partner.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = partner.title,
                            fontSize = 13.5.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play voice intro",
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Language
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search, 
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = partner.languages.joinToString(", "),
                        fontSize = 12.5.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Connect Button (Amber CTA)
                Surface(
                    onClick = onConnectClick,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Accent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp).fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = Dark,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Connect",
                                color = Dark,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Rate Pill with Coin Logo
                        Surface(
                            color = Color.Black.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CoinLogo(size = 12.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${partner.rate_per_min.toInt()}/min",
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
