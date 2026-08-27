package com.example.truelineapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.truelineapp.network.ApiResponse
import com.example.truelineapp.network.AuthResponse
import com.example.truelineapp.network.ListenerDiscovery
import com.example.truelineapp.network.chat.ChatConversationData
import com.example.truelineapp.network.chat.ChatMessageData
import com.example.truelineapp.network.customer.CustomerRepository
import com.example.truelineapp.network.customer.TransactionItem
import com.example.truelineapp.payment.PaymentServiceWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val scope: CoroutineScope) {
    val repository = CustomerRepository()

    // --- Auth State ---
    var isLoading by mutableStateOf(false)
    var isAuthSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var authToken by mutableStateOf<String?>(null)
    var currentPhoneNumber by mutableStateOf("")
    var otpCountdown by mutableStateOf(30)
    var canResendOtp by mutableStateOf(false)
    private var otpTimerJob: Job? = null

    companion object {
        fun generateDefaultUsername(): String {
            val randomDigits = kotlin.random.Random.nextInt(100000, 1000000)
            return "user$randomDigits"
        }
    }

    // --- User & Profile State ---
    var walletBalance by mutableDoubleStateOf(1000.0)
    var selectedLanguage by mutableStateOf("en")
    var userId by mutableStateOf("User")
    var userName by mutableStateOf(generateDefaultUsername())
    var userPhotoPath by mutableStateOf<String?>(null)
    var isFirstTimeNameChange by mutableStateOf(true)
    var isProfileLoading by mutableStateOf(false)

    // --- Discovery State ---
    var partners by mutableStateOf<List<ListenerDiscovery>>(emptyList())
    var isDiscoverLoading by mutableStateOf(false)
    var selectedDiscoverLanguage by mutableStateOf("All")
    var searchQuery by mutableStateOf("")
    var playingAudioUrl by mutableStateOf<String?>(null)

    // --- Chat State ---
    var conversations = mutableStateListOf<ChatConversationData>()
    var isChatListLoading by mutableStateOf(false)
    var activeChatPartnerId by mutableStateOf<String?>(null)
    var currentChatMessages = mutableStateListOf<ChatMessageData>()
    var isChatMessagesLoading by mutableStateOf(false)

    // --- Wallet & Cashfree Payment State ---
    var isPaymentProcessing by mutableStateOf(false)
    var transactions = mutableStateListOf<TransactionItem>()
    var isTransactionsLoading by mutableStateOf(false)

    // --- Call State ---
    var activeSessionId by mutableStateOf<String?>(null)
    var currentCallingPartner by mutableStateOf<ListenerDiscovery?>(null)
    var showPostCallRating by mutableStateOf(false)
    var lastCallDuration by mutableStateOf(180)
    var lastCallCoinsDeducted by mutableStateOf(27)
    private var callEventsJob: Job? = null

    init {
        val storage = com.example.truelineapp.storage.getSessionStorage()
        val savedName = storage.getUserName()
        if (savedName.isNullOrBlank() || savedName.startsWith("User #")) {
            val defaultName = generateDefaultUsername()
            storage.saveUserName(defaultName)
            userName = defaultName
        } else {
            userName = savedName
        }
        userPhotoPath = storage.getUserPhoto()
        walletBalance = storage.getWalletBalance() ?: 1000.0
        isFirstTimeNameChange = !storage.isNameChangedBefore()
        checkAutoLogin()
        fetchListeners()
    }

    private fun checkAutoLogin() {
        val token = repository.getAuthToken()
        val phone = repository.getSavedPhone()
        if (!token.isNullOrBlank()) {
            authToken = token
            isAuthSuccess = true
            currentPhoneNumber = phone ?: ""
            fetchUserProfile()
            fetchListeners()
            fetchConversations()
        }
    }

    // --- Auth Methods ---
    fun sendOtp(phone: String, onSuccess: () -> Unit = {}) {
        currentPhoneNumber = phone
        isLoading = true
        errorMessage = null
        scope.launch {
            val res = repository.requestOtp(phone)
            isLoading = false
            if (res.success) {
                startOtpTimer()
                onSuccess()
            } else {
                errorMessage = res.error?.message ?: "Failed to send OTP. Please check your connection."
            }
        }
    }

    fun verifyOtp(phone: String, otp: String, onLoginSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        scope.launch {
            val res = repository.verifyOtp(phone, otp)
            isLoading = false
            if (res.success && res.data != null) {
                authToken = res.data.token
                isAuthSuccess = true
                fetchUserProfile()
                fetchListeners()
                fetchConversations()
                onLoginSuccess()
            } else {
                errorMessage = res.error?.message ?: "Invalid OTP. Please enter the 6-digit code sent to your phone."
            }
        }
    }

    fun resendOtp() {
        if (canResendOtp && currentPhoneNumber.isNotBlank()) {
            sendOtp(currentPhoneNumber)
        }
    }

    private fun startOtpTimer() {
        otpCountdown = 30
        canResendOtp = false
        otpTimerJob?.cancel()
        otpTimerJob = scope.launch {
            while (otpCountdown > 0) {
                delay(1000)
                otpCountdown -= 1
            }
            canResendOtp = true
        }
    }

    // --- User Profile & Language ---
    fun fetchUserProfile() {
        isProfileLoading = true
        scope.launch {
            val storage = com.example.truelineapp.storage.getSessionStorage()
            val savedBalance = storage.getWalletBalance()
            val savedName = storage.getUserName()
            val savedPhoto = storage.getUserPhoto()
            if (savedName.isNullOrBlank() || savedName.startsWith("User #")) {
                val defaultName = generateDefaultUsername()
                storage.saveUserName(defaultName)
                userName = defaultName
            } else {
                userName = savedName
            }
            if (savedPhoto != null) userPhotoPath = savedPhoto
            isFirstTimeNameChange = !storage.isNameChangedBefore()

            val res = repository.getUserProfile()
            isProfileLoading = false
            if (res.success && res.data != null) {
                walletBalance = if (savedBalance != null) savedBalance else if (res.data.balance <= 0.0) 1000.0 else res.data.balance
                selectedLanguage = res.data.user.language_pref
                userId = res.data.user.id.take(8).uppercase()
                try {
                    com.example.truelineapp.call.getCallService().initialize(
                        628007464L,
                        "e7dffb8a9cb6a89f1fc2afddcc16f4ce4df9cd1e8ca346076161caf69cbd465e",
                        res.data.user.id,
                        userName
                    )
                } catch (e: Exception) {}
            } else {
                if (savedBalance != null) {
                    walletBalance = savedBalance
                } else if (walletBalance <= 0.0) {
                    walletBalance = 1000.0
                }
            }
        }
    }

    fun updateUserPhoto(photoPath: String, cost: Int = 59) {
        if (walletBalance >= cost) {
            walletBalance -= cost
            userPhotoPath = photoPath
            val storage = com.example.truelineapp.storage.getSessionStorage()
            storage.saveUserPhoto(photoPath)
            storage.saveWalletBalance(walletBalance)
            transactions.add(
                0,
                TransactionItem(
                    id = "tx_${kotlin.random.Random.nextInt(100000, 999999)}",
                    amount = cost.toDouble(),
                    type = "debit",
                    description = "Profile Photo Update",
                    created_at = "Just now"
                )
            )
        }
    }

    fun removeUserPhoto() {
        val oldPath = userPhotoPath
        userPhotoPath = null
        val storage = com.example.truelineapp.storage.getSessionStorage()
        storage.saveUserPhoto("")
        if (!oldPath.isNullOrBlank()) {
            try {
                val file = java.io.File(oldPath)
                if (file.exists()) file.delete()
            } catch (e: Exception) {}
        }
    }

    fun updateUserName(newName: String, cost: Int) {
        if (walletBalance >= cost) {
            if (cost > 0) {
                walletBalance -= cost
            }
            userName = newName
            isFirstTimeNameChange = false
            val storage = com.example.truelineapp.storage.getSessionStorage()
            storage.saveUserName(newName)
            storage.saveWalletBalance(walletBalance)
            storage.saveNameChangedBefore(true)
            if (cost > 0) {
                transactions.add(
                    0,
                    TransactionItem(
                        id = "tx_${kotlin.random.Random.nextInt(100000, 999999)}",
                        amount = cost.toDouble(),
                        type = "debit",
                        description = "Profile Name Update ($newName)",
                        created_at = "Just now"
                    )
                )
            }
        }
    }

    fun updateLanguage(langCode: String) {
        selectedLanguage = langCode
        scope.launch {
            repository.updateLanguagePreference(langCode)
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        repository.clearAuthSession()
        authToken = null
        isAuthSuccess = false
        walletBalance = 0.0
        conversations.clear()
        currentChatMessages.clear()
        onLoggedOut()
    }

    // --- Discovery Methods ---
    fun fetchListeners() {
        isDiscoverLoading = true
        scope.launch {
            val res = repository.getListeners(
                language = if (selectedDiscoverLanguage == "All") null else selectedDiscoverLanguage,
                search = if (searchQuery.isBlank()) null else searchQuery
            )
            isDiscoverLoading = false
            if (res.success && res.data != null) {
                partners = res.data.sortedWith(
                    compareByDescending<ListenerDiscovery> { it.availability.equals("online", ignoreCase = true) }
                        .thenByDescending { it.rating_avg }
                )
            }
        }
    }

    fun onSearchChanged(query: String) {
        searchQuery = query
        fetchListeners()
    }

    fun onDiscoverLanguageSelected(lang: String) {
        selectedDiscoverLanguage = lang
        fetchListeners()
    }

    fun toggleAudioPlayback(url: String) {
        if (url.isBlank()) return
        val player = com.example.truelineapp.audio.getAudioPlayer()
        if (playingAudioUrl == url) {
            player.stop()
            playingAudioUrl = null
        } else {
            player.stop()
            playingAudioUrl = url
            player.play(url) {
                if (playingAudioUrl == url) {
                    playingAudioUrl = null
                }
            }
        }
    }

    // --- Chat Methods ---
    fun fetchConversations() {
        isChatListLoading = true
        scope.launch {
            val res = repository.getChatConversations()
            isChatListLoading = false
            if (res.success && res.data != null) {
                conversations.clear()
                conversations.addAll(res.data)
            }
        }
    }

    fun openChatRoom(partnerId: String) {
        activeChatPartnerId = partnerId
        isChatMessagesLoading = true
        currentChatMessages.clear()
        scope.launch {
            val res = repository.getChatMessages(partnerId)
            isChatMessagesLoading = false
            if (res.success && res.data != null) {
                currentChatMessages.addAll(res.data)
            }
        }
    }

    fun sendChatMessage(partnerId: String, content: String) {
        val trimmed = content.trim()
        if (trimmed.isBlank()) return
        
        val tempId = "temp_${System.currentTimeMillis()}"
        val tempMsg = ChatMessageData(
            id = tempId,
            user_id = userId,
            partner_id = partnerId,
            sender_type = "user",
            content = trimmed,
            created_at = getCurrentTimeFormatted()
        )
        currentChatMessages.add(tempMsg)

        scope.launch {
            val res = repository.sendChatMessage(partnerId, trimmed)
            if (res.success && res.data != null) {
                val index = currentChatMessages.indexOfFirst { it.id == tempId }
                if (index != -1) {
                    currentChatMessages[index] = res.data
                }
                fetchConversations()
            }
        }
    }

    // --- Wallet & Cashfree Payment Methods ---
    fun initiateRecharge(amountPaise: Long, coins: Long) {
        isPaymentProcessing = true
        scope.launch {
            val orderRes = repository.createCashfreeOrder(amountPaise, coins)
            if (orderRes.success && orderRes.data != null) {
                // Auto verify order for test/sandbox flow
                repository.verifyCashfreeOrder(orderRes.data.order_id)
                fetchUserProfile()
                fetchTransactions()
                isPaymentProcessing = false
            } else {
                isPaymentProcessing = false
                errorMessage = orderRes.error?.message ?: "Recharge failed"
            }
        }
    }

    fun initiateCashfreeRecharge(
        activity: Any,
        amountPaise: Long,
        coins: Long,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        isPaymentProcessing = true
        scope.launch {
            val orderRes = repository.createCashfreeOrder(amountPaise, coins)
            if (orderRes.success && orderRes.data != null) {
                val order = orderRes.data
                val paymentWrapper = PaymentServiceWrapper()
                paymentWrapper.startCheckout(
                    activity = activity,
                    orderId = order.order_id,
                    paymentSessionId = order.payment_session_id,
                    onSuccess = { orderId: String ->
                        scope.launch {
                            repository.verifyCashfreeOrder(orderId)
                            fetchUserProfile()
                            fetchTransactions()
                            isPaymentProcessing = false
                            onSuccess()
                        }
                    },
                    onFailure = { err: String ->
                        isPaymentProcessing = false
                        errorMessage = err
                        onFailure(err)
                    }
                )
            } else {
                isPaymentProcessing = false
                errorMessage = orderRes.error?.message ?: "Failed to initialize payment gateway"
                onFailure(errorMessage ?: "Payment error")
            }
        }
    }

    fun fetchTransactions() {
        isTransactionsLoading = true
        scope.launch {
            val res = repository.getTransactionHistory()
            isTransactionsLoading = false
            if (res.success && res.data != null) {
                transactions.clear()
                transactions.addAll(res.data)
            }
        }
    }

    // --- Calling Methods ---
    fun connectToListener(
        partnerId: String,
        onCallReady: (roomId: String, token: String, targetUserId: String, targetUserName: String) -> Unit = { _, _, _, _ -> }
    ) {
        val partner = partners.find { it.id == partnerId }
        currentCallingPartner = partner
        isLoading = true
        errorMessage = null

        scope.launch {
            val res = repository.initiateCall(partnerId)
            isLoading = false
            if (res.success && res.data != null) {
                activeSessionId = res.data.session_id
                startCallEventObserver(res.data.session_id)
                onCallReady(
                    res.data.room_id,
                    res.data.user_token,
                    partnerId,
                    partner?.name ?: "Listener"
                )
            } else {
                errorMessage = res.error?.message ?: "Call initiation failed"
            }
        }
    }

    fun onCallFinished(durationSeconds: Int = 180) {
        lastCallDuration = durationSeconds
        val rate = currentCallingPartner?.rate_per_min ?: 9.0
        lastCallCoinsDeducted = (((durationSeconds + 59) / 60) * rate).toInt()
        showPostCallRating = true
        activeSessionId?.let { sid ->
            scope.launch {
                repository.endCall(sid, "user_hangup")
                fetchUserProfile()
            }
        }
    }

    fun submitRating(rating: Int, tags: List<String>, isFavorite: Boolean) {
        val sid = activeSessionId
        showPostCallRating = false
        currentCallingPartner = null
        activeSessionId = null
        if (sid != null) {
            scope.launch {
                repository.rateCall(sid, rating, tags, isFavorite)
            }
        }
    }

    fun dismissRating() {
        showPostCallRating = false
        currentCallingPartner = null
        activeSessionId = null
    }

    private fun startCallEventObserver(sessionId: String) {
        callEventsJob?.cancel()
        callEventsJob = scope.launch {
            try {
                repository.observeCallEvents(sessionId).collect { event ->
                    when (event.type) {
                        "balance_updated" -> fetchUserProfile()
                        "call_ended" -> {
                            callEventsJob?.cancel()
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore call event stream exceptions
            }
        }
    }
}
