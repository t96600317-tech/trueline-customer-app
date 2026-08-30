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
import com.example.truelineapp.otp.Msg91OtpResult
import com.example.truelineapp.otp.getMsg91OtpGateway
import com.example.truelineapp.call.callConnectionDiagnosticForDisplay
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
    private val msg91Otp = getMsg91OtpGateway()
    private var msg91RequestId: String? = null

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
    var lastCallDuration by mutableStateOf(0)
    var lastCallCoinsDeductedMicros by mutableStateOf(0L)
    var voiceCallErrorMessage by mutableStateOf<String?>(null)
    private var callEventsJob: Job? = null
    private var isEndingCall = false

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
        val savedLang = storage.getLanguage()
        if (!savedLang.isNullOrBlank()) {
            selectedLanguage = savedLang
        }
        isFirstTimeNameChange = !storage.isNameChangedBefore()
        checkAutoLogin()
        fetchListeners()

        // Live Presence Heartbeat Loop (Pings every 8 seconds while logged in)
        scope.launch {
            while (true) {
                if (authToken != null) {
                    try {
                        repository.sendHeartbeat()
                    } catch (_: Exception) {}
                }
                delay(8000)
            }
        }
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
        msg91RequestId = null
        isLoading = true
        errorMessage = null
        scope.launch {
            val msg91Result = if (msg91Otp.isConfigured) msg91Otp.sendOtp(phone) else null
            if (msg91Result?.success == true) {
                msg91RequestId = msg91Result.requestId
                isLoading = false
                startOtpTimer()
                onSuccess()
            } else if (msg91Result != null) {
                isLoading = false
                errorMessage = msg91Result.errorMessage ?: "Failed to send OTP. Please try again."
            } else {
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
    }

    fun verifyOtp(phone: String, otp: String, onLoginSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        scope.launch {
            val msg91Result = if (msg91Otp.isConfigured) {
                val requestId = msg91RequestId
                if (requestId.isNullOrBlank()) {
                    Msg91OtpResult(false, errorMessage = "Please request a new OTP.")
                } else {
                    msg91Otp.verifyOtp(requestId, otp)
                }
            } else {
                null
            }
            if (msg91Result != null && !msg91Result.success) {
                isLoading = false
                errorMessage = msg91Result.errorMessage ?: "Invalid OTP. Please try again."
                return@launch
            }
            if (msg91Result != null && msg91Result.accessToken.isNullOrBlank()) {
                msg91RequestId = null
                isLoading = false
                errorMessage = "MSG91 verified the code but did not return an access token. Please request a new OTP."
                return@launch
            }

            val res = repository.verifyOtp(phone, otp, msg91RequestId, msg91Result?.accessToken)
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
            val requestId = msg91RequestId
            if (msg91Otp.isConfigured && !requestId.isNullOrBlank()) {
                isLoading = true
                errorMessage = null
                scope.launch {
                    val result = msg91Otp.retryOtp(requestId)
                    isLoading = false
                    if (result.success) {
                        startOtpTimer()
                    } else {
                        errorMessage = result.errorMessage ?: "Failed to resend OTP. Please try again."
                    }
                }
            } else {
                sendOtp(currentPhoneNumber)
            }
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
            val savedLang = storage.getLanguage()
            if (!savedLang.isNullOrBlank()) {
                selectedLanguage = savedLang
            } else if (res.success && res.data != null && res.data.user.language_pref.isNotBlank()) {
                selectedLanguage = res.data.user.language_pref
                storage.saveLanguage(selectedLanguage)
            }

            isProfileLoading = false
            if (res.success && res.data != null) {
                if (res.data.user.name.isNotBlank() && res.data.user.name != userName) {
                    // If backend has a custom name saved, use it
                    userName = res.data.user.name
                    storage.saveUserName(userName)
                } else if (userName.isNotBlank()) {
                    repository.updateUserName(userName)
                }
                walletBalance = if (savedBalance != null) savedBalance else if (res.data.balance <= 0.0) 1000.0 else res.data.balance
                userId = res.data.user.id.take(8).uppercase()
                try {
                    com.example.truelineapp.call.getCallService().initialize(
                        1939552281L,
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
            deleteLocalFile(oldPath)
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
            scope.launch {
                repository.updateUserName(newName)
            }
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
        val storage = com.example.truelineapp.storage.getSessionStorage()
        storage.saveLanguage(langCode)
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
    private var listenersPollingJob: Job? = null

    fun startLiveListenersPolling() {
        listenersPollingJob?.cancel()
        listenersPollingJob = scope.launch {
            while (true) {
                fetchListeners(silent = partners.isNotEmpty())
                delay(3000) // Poll every 3 seconds for live online/busy/offline status updates
            }
        }
    }

    fun stopLiveListenersPolling() {
        listenersPollingJob?.cancel()
        listenersPollingJob = null
    }

    fun fetchListeners(silent: Boolean = false) {
        if (!silent) {
            isDiscoverLoading = true
        }
        scope.launch {
            val res = repository.getListeners(
                language = if (selectedDiscoverLanguage == "All") null else selectedDiscoverLanguage,
                search = if (searchQuery.isBlank()) null else searchQuery
            )
            if (!silent) {
                isDiscoverLoading = false
            }
            if (res.success && res.data != null) {
                val availabilityPriority = { avail: String ->
                    when (avail.lowercase().trim()) {
                        "online" -> 3
                        "busy" -> 2
                        else -> 1
                    }
                }
                partners = res.data.sortedWith(
                    compareByDescending<ListenerDiscovery> { availabilityPriority(it.availability) }
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
        
        val tempId = "temp_${currentPlatformTimeMillis()}"
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

    fun notifyWhenOnline(listenerId: String) {
        scope.launch {
            val res = repository.notifyWhenOnline(listenerId)
            if (!res.success) {
                errorMessage = res.error?.message ?: "Could not register your notification request"
            }
        }
    }

    // --- Calling Methods ---
    fun connectToListener(
        partnerId: String,
        onCallReady: (
            roomId: String,
            token: String,
            targetUserId: String,
            targetUserName: String,
            signedUserId: String,
            zegoConfigFingerprint: String
        ) -> Unit = { _, _, _, _, _, _ -> }
    ) {
        val partner = partners.find { it.id == partnerId }
        currentCallingPartner = partner
        isLoading = true
        errorMessage = null
        voiceCallErrorMessage = null

        scope.launch {
            val res = repository.initiateCall(partnerId)
            isLoading = false
            if (res.success && res.data != null) {
                if (res.data.user_token.isBlank() || res.data.room_id.isBlank()) {
                    errorMessage = "Unable to get a secure voice-call token"
                    return@launch
                }
                activeSessionId = res.data.session_id
                startCallEventObserver(res.data.session_id)
                onCallReady(
                    res.data.room_id,
                    res.data.user_token,
                    partnerId,
                    partner?.name ?: "Listener",
                    res.data.zego_user_id,
                    res.data.zego_config_fingerprint
                )
            } else {
                errorMessage = res.error?.message ?: "Call initiation failed"
            }
        }
    }

    fun onCallFinished(durationSeconds: Int) {
        val sid = activeSessionId ?: return
        if (isEndingCall) return
        isEndingCall = true
        callEventsJob?.cancel()

        scope.launch {
            repository.endCall(sid, "user_hangup")
            val summary = repository.getCallSummary(sid)
            isEndingCall = false
            if (summary.success && summary.data != null && summary.data.duration_seconds > 0) {
                lastCallDuration = summary.data.duration_seconds
                lastCallCoinsDeductedMicros = summary.data.coins_deducted_micros
                showPostCallRating = true
            } else {
                errorMessage = "Voice call ended before the listener connected. No coins were charged."
                activeSessionId = null
                currentCallingPartner = null
            }
            fetchUserProfile()
        }
    }

    fun onCallConnectionFailed(message: String) {
        if (isEndingCall) return
        callEventsJob?.cancel()
        activeSessionId?.let { sid ->
            scope.launch {
                repository.endCall(sid, "connection_failed")
                fetchUserProfile()
            }
        }
        activeSessionId = null
        currentCallingPartner = null
        showPostCallRating = false
        val diagnostic = callConnectionDiagnosticForDisplay(message)
        errorMessage = diagnostic
        voiceCallErrorMessage = diagnostic
    }

    fun dismissVoiceCallError() {
        voiceCallErrorMessage = null
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
