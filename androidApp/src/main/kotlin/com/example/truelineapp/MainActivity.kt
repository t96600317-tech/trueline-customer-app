package com.example.truelineapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import com.example.truelineapp.network.TokenManager
import com.example.truelineapp.network.RetrofitClient
import com.example.truelineapp.network.auth.*
import com.example.truelineapp.network.user.*
import com.example.truelineapp.network.partner.*
import com.example.truelineapp.network.chat.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)
        
        // Ensure status bar icons are dark (since our background is light)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // Manual Injection for Auth
        val authApiService = RetrofitClient.createService(this, AuthApiService::class.java)
        val authRepository = AuthRepository(authApiService)
        val tokenManager = TokenManager(this)
        val authViewModel = AuthViewModel(authRepository, tokenManager)

        // Manual Injection for User
        val userApiService = RetrofitClient.createService(this, UserApiService::class.java)
        val userRepository = UserRepository(userApiService)
        val userViewModel = UserViewModel(userRepository)

        // Manual Injection for Discover
        val partnerApiService = RetrofitClient.createService(this, PartnerApiService::class.java)
        val partnerRepository = PartnerRepository(partnerApiService)
        val discoverViewModel = DiscoverViewModel(partnerRepository)

        // Manual Injection for Chat
        val chatApiService = RetrofitClient.createService(this, ChatApiService::class.java)
        val chatRepository = ChatRepository(chatApiService)
        val chatListViewModel = ChatListViewModel(chatRepository)
        val chatRoomViewModel = ChatRoomViewModel(chatRepository)

        val mediaPlayer = android.media.MediaPlayer()
        var playingUrl by mutableStateOf<String?>(null)

        setContent {
            val authState by authViewModel.authState.collectAsState()
            val walletBalance by userViewModel.balance.collectAsState()
            val selectedLanguage by userViewModel.selectedLanguage.collectAsState()
            
            val partners by discoverViewModel.partners.collectAsState()
            val isDiscoverLoading by discoverViewModel.isLoading.collectAsState()
            val searchQuery by discoverViewModel.searchQuery.collectAsState()
            val selectedDiscoverLanguage by discoverViewModel.selectedLanguage.collectAsState()
            
            val conversations by chatListViewModel.conversations.collectAsState()
            val isChatListLoading by chatListViewModel.isLoading.collectAsState()
            
            val chatMessages by chatRoomViewModel.messages.collectAsState()
            val isChatRoomLoading by chatRoomViewModel.isLoading.collectAsState()
            
            var isLoading by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            
            // Handle Audio Playback Effect
            DisposableEffect(Unit) {
                onDispose {
                    mediaPlayer.release()
                }
            }

            // Handle Auth State
            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Loading -> {
                        isLoading = true
                        errorMessage = null
                    }
                    is AuthState.Error -> {
                        isLoading = false
                        errorMessage = (authState as AuthState.Error).message
                    }
                    is AuthState.Success -> {
                        isLoading = false
                        errorMessage = null
                        userViewModel.fetchUserProfile()
                        discoverViewModel.fetchPartners() 
                        chatListViewModel.fetchConversations() 
                    }
                    else -> {
                        isLoading = false
                    }
                }
            }
            
            // Initial fetch
            LaunchedEffect(Unit) {
                if (tokenManager.getToken() != null) {
                    userViewModel.fetchUserProfile()
                    discoverViewModel.fetchPartners()
                    chatListViewModel.fetchConversations()
                }
            }

            val isSuccess = authState is AuthState.Success
            
            App(
                isLoading = isLoading,
                isSuccess = isSuccess,
                errorMessage = errorMessage,
                walletBalanceInitial = walletBalance.toInt(),
                selectedLanguageInitial = selectedLanguage,
                partners = partners,
                isDiscoverLoading = isDiscoverLoading,
                searchQueryInitial = searchQuery,
                selectedDiscoverLanguage = selectedDiscoverLanguage,
                playingAudioUrl = playingUrl,
                conversations = conversations,
                chatMessages = chatMessages,
                isChatListLoading = isChatListLoading,
                isChatMessagesLoading = isChatRoomLoading,
                onSendOtp = { phone -> authViewModel.requestOtp(phone) },
                onVerifyOtp = { phone, otp -> authViewModel.verifyOtp(phone, otp) },
                onLanguageUpdate = { code -> userViewModel.updateLanguage(code) },
                onSearchChanged = { discoverViewModel.onSearchQueryChanged(it) },
                onDiscoverLanguageSelected = { discoverViewModel.onLanguageSelected(it) },
                onLoadMessages = { partnerId -> chatRoomViewModel.loadMessages(partnerId) },
                onSendMessage = { partnerId, content -> chatRoomViewModel.sendMessage(partnerId, content) },
                onRefreshChatList = { chatListViewModel.fetchConversations() },
                onPlayAudio = { url ->
                    if (playingUrl == url) {
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                        playingUrl = null
                    } else {
                        try {
                            mediaPlayer.reset()
                            mediaPlayer.setDataSource(url)
                            mediaPlayer.prepareAsync()
                            mediaPlayer.setOnPreparedListener { 
                                it.start() 
                                playingUrl = url
                            }
                            mediaPlayer.setOnCompletionListener { 
                                playingUrl = null 
                            }
                        } catch (e: Exception) {
                            println("Error playing audio: ${e.message}")
                        }
                    }
                }
            )
        }
    }
}
