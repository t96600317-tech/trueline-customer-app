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
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.truelineapp.network.partner.PartnerData
import com.example.truelineapp.network.chat.ChatConversationData
import com.example.truelineapp.network.chat.ChatMessageData

@Composable
@Preview
fun App(
    isLoading: Boolean = false,
    isSuccess: Boolean = false,
    errorMessage: String? = null,
    walletBalanceInitial: Int = 10,
    selectedLanguageInitial: String = "en",
    partners: List<PartnerData> = emptyList(),
    isDiscoverLoading: Boolean = false,
    searchQueryInitial: String = "",
    selectedDiscoverLanguage: String = "All",
    playingAudioUrl: String? = null,
    conversations: List<ChatConversationData> = emptyList(),
    chatMessages: List<ChatMessageData> = emptyList(),
    isChatListLoading: Boolean = false,
    isChatMessagesLoading: Boolean = false,
    onSendOtp: (String) -> Unit = {},
    onVerifyOtp: (String, String) -> Unit = { _, _ -> },
    onLanguageUpdate: (String) -> Unit = {},
    onSearchChanged: (String) -> Unit = {},
    onDiscoverLanguageSelected: (String) -> Unit = {},
    onPlayAudio: (url: String) -> Unit = {},
    onLoadMessages: (partnerId: String) -> Unit = {},
    onSendMessage: (partnerId: String, content: String) -> Unit = { _, _ -> },
    onRefreshChatList: () -> Unit = {},
    onAuthSuccess: (token: String) -> Unit = {}
) {
    TrueLineTheme {
        val navController = rememberNavController()
        var walletBalance by rememberSaveable { mutableIntStateOf(walletBalanceInitial) }
        var isFirstTimeNameChange by rememberSaveable { mutableStateOf(true) }
        var userName by rememberSaveable { mutableStateOf("Prithvi") }
        var selectedLanguageCode by rememberSaveable { mutableStateOf(selectedLanguageInitial) }
        
        LaunchedEffect(walletBalanceInitial) {
            walletBalance = walletBalanceInitial
        }

        LaunchedEffect(selectedLanguageInitial) {
            selectedLanguageCode = selectedLanguageInitial
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "onboarding"
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
                        isLoading = isLoading,
                        isSuccess = isSuccess,
                        errorMessage = errorMessage,
                        onSendOtp = onSendOtp,
                        onVerifyOtp = onVerifyOtp,
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
                        walletBalance = walletBalance,
                        userName = userName,
                        isFirstTimeNameChange = isFirstTimeNameChange,
                        selectedLanguageCode = selectedLanguageCode,
                        partners = partners,
                        isDiscoverLoading = isDiscoverLoading,
                        searchQueryInitial = searchQueryInitial,
                        selectedDiscoverLanguage = selectedDiscoverLanguage,
                        conversations = conversations,
                        isChatListLoading = isChatListLoading,
                        onChatClick = { partner ->
                            val photoUrlEncoded = partner.partner_photo_url.ifBlank { "none" }
                            val titleEncoded = partner.partner_title.ifBlank { "none" }
                            navController.navigate("chat_detail/${partner.partner_id}/${partner.partner_name}/$titleEncoded/$photoUrlEncoded")
                        },
                        onNavigateToWallet = {
                            navController.navigate("wallet")
                        },
                        onAddCoins = { amount -> 
                            walletBalance += amount 
                        },
                        onUpdateProfile = { newName, cost ->
                            userName = newName
                            walletBalance -= cost
                            isFirstTimeNameChange = false
                        },
                        onLanguageUpdate = { code ->
                            selectedLanguageCode = code
                            onLanguageUpdate(code)
                        },
                        onSearchChanged = onSearchChanged,
                        onDiscoverLanguageSelected = onDiscoverLanguageSelected,
                        playingAudioUrl = playingAudioUrl,
                        onRefreshChatList = onRefreshChatList,
                        onPlayAudio = onPlayAudio,
                        onCallClick = { partner ->
                            navController.navigate("audio_call/${partner.name}")
                        }
                    )
                }
                composable("wallet") {
                    WalletScreen(
                        balance = walletBalance,
                        onBack = { navController.popBackStack() },
                        onRecharge = { amount -> 
                            println("Initiating payment for $amount coins...")
                            walletBalance += amount
                        }
                    )
                }
                composable(
                    route = "audio_call/{name}",
                    arguments = listOf(navArgument("name") { type = NavType.StringType })
                ) { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    AudioCallScreen(
                        listenerName = name,
                        onHangUp = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(
                    route = "chat_detail/{id}/{name}/{title}/{photoUrl}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType },
                        navArgument("title") { type = NavType.StringType },
                        navArgument("photoUrl") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val title = backStackEntry.arguments?.getString("title") ?: ""
                    val photoUrl = backStackEntry.arguments?.getString("photoUrl") ?: ""
                    IndividualChatScreen(
                        partnerId = id,
                        senderName = name,
                        partnerTitle = title,
                        partnerPhotoUrl = photoUrl,
                        messagesList = chatMessages,
                        isLoading = isChatMessagesLoading,
                        onLoadMessages = { onLoadMessages(id) },
                        onSendMessage = { content -> onSendMessage(id, content) },
                        onBack = {
                            onRefreshChatList()
                            navController.navigate("main/1") {
                                popUpTo("main/1") { inclusive = true }
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
    partners: List<PartnerData>,
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
    onCallClick: (PartnerData) -> Unit
) {
    var searchQuery by remember { mutableStateOf(searchQueryInitial) }
    val languages = listOf("All", "Hindi", "Bhojpuri", "Bengali", "Tamil", "Telugu", "Marathi", "Punjabi")
    
    var showAddCoinsSheet by remember { mutableStateOf(false) }
    var pendingListenerName by remember { mutableStateOf("") }
    var showLanguageSheet by remember { mutableStateOf(false) }

    var selectedTab by rememberSaveable { mutableIntStateOf(initialTab) }

    LaunchedEffect(searchQueryInitial) {
        searchQuery = searchQueryInitial
    }

    if (showAddCoinsSheet) {
        AddCoinsBottomSheet(
            listenerName = pendingListenerName,
            currentBalance = walletBalance,
            onDismiss = { showAddCoinsSheet = false },
            onAddCoins = { amount ->
                onAddCoins(amount)
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
            TopAppBar(
                title = { 
                   Row(verticalAlignment = Alignment.CenterVertically) {
                       TrueLineLogo(size = 32.dp)
                       Spacer(modifier = Modifier.width(8.dp))
                       TrueLineBrandText(fontSize = 22.sp)
                   }
                },
                actions = {
                    Surface(
                        onClick = onNavigateToWallet, 
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CoinLogo(size = 18.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(walletBalance.toString(), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TrueLinePrimary)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(if (selectedTab == 0) Icons.Filled.Call else Icons.Outlined.Call, null) },
                    label = { Text("Call") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TrueLinePrimary,
                        selectedTextColor = TrueLinePrimary,
                        indicatorColor = TrueLinePrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { 
                        Icon(
                            imageVector = if (selectedTab == 1) 
                                Icons.AutoMirrored.Filled.Chat 
                            else 
                                Icons.AutoMirrored.Outlined.Chat, 
                            contentDescription = "Chat"
                        ) 
                    },
                    label = { Text("Chat") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TrueLinePrimary,
                        selectedTextColor = TrueLinePrimary,
                        indicatorColor = TrueLinePrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Outlined.Person, null) }, 
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TrueLinePrimary,
                        selectedTextColor = TrueLinePrimary,
                        indicatorColor = TrueLinePrimary.copy(alpha = 0.1f)
                    )
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(TrueLineLightBg)) {
            when (selectedTab) {
                0 -> {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            onSearchChanged(it)
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        placeholder = { Text("Search name") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.LightGray,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = TrueLineDarkBg,
                            unfocusedTextColor = TrueLineDarkBg,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        )
                    )

                    // Language Filters
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(languages) { lang ->
                            FilterChip(
                                selected = selectedDiscoverLanguage == lang,
                                onClick = { onDiscoverLanguageSelected(lang) },
                                label = { Text(lang, fontWeight = if (selectedDiscoverLanguage == lang) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TrueLinePrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = Color.Gray
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedDiscoverLanguage == lang,
                                    borderColor = Color.LightGray,
                                    selectedBorderColor = TrueLinePrimary,
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
                            CircularProgressIndicator(color = TrueLinePrimary)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(partners) { partner ->
                                ExactReplicaCard(
                                    partner = partner, 
                                    isPlaying = playingAudioUrl == partner.audio_sample_url,
                                    onCardClick = { 
                                        onChatClick(ChatConversationData(
                                            partner_id = partner.id,
                                            partner_name = partner.name,
                                            partner_title = partner.title,
                                            partner_photo_url = partner.photo_url,
                                            partner_availability = partner.availability,
                                            last_message = "",
                                            last_message_sender = "",
                                            last_message_time = "",
                                            unread_count = 0
                                        ))
                                    },
                                    onPlayClick = { onPlayAudio(partner.audio_sample_url) },
                                    onConnectClick = {
                                        if (walletBalance >= partner.rate_per_min) {
                                            onCallClick(partner)
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
                        onChatClick = onChatClick
                    )
                }
                2 -> {
                    UserProfileScreen(
                        userName = userName,
                        walletBalance = walletBalance,
                        isFirstTimeNameChange = isFirstTimeNameChange,
                        selectedLanguageCode = selectedLanguageCode,
                        onLogout = { /* Handle Logout */ },
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
    partner: PartnerData, 
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
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
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                // Circular Image with White Border
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = TrueLineLightBg,
                    border = BorderStroke(2.dp, Color.White),
                    shadowElevation = 4.dp
                ) {
                    ListenerAvatar(
                        name = partner.name,
                        modifier = Modifier.fillMaxSize(),
                        fontSize = 42.sp
                    )
                }

                // ONLINE Badge (Top Right Position)
                if (partner.availability == "online") {
                    Surface(
                        color = TrueLineOnline,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-2).dp, y = 2.dp)
                    ) {
                        Text(
                            "ONLINE",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Rating Badge (Bottom Position)
                Surface(
                    color = TrueLineDarkBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, null, tint = TrueLineAccent, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${partner.rating_avg} (${partner.rating_count})", 
                            color = Color.White, 
                            fontSize = 10.sp, 
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
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrueLineDarkBg
                        )
                        Text(
                            text = partner.title,
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }
                    
                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(TrueLineLightBg)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = TrueLinePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Language
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search, 
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = partner.languages.joinToString(", "),
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Connect Button (Amber)
                Surface(
                    onClick = onConnectClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = TrueLineAccent 
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
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Connect",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Rate Pill with Coin Logo
                        Surface(
                            color = Color.Black.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CoinLogo(size = 12.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${partner.rate_per_min.toInt()}/min",
                                    color = Color.White,
                                    fontSize = 12.sp,
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
