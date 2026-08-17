package com.example.truelineapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.truelineapp.network.ApiResponse
import com.example.truelineapp.network.AuthResponse
import com.example.truelineapp.network.ListenerDiscovery
import com.example.truelineapp.network.customer.CustomerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val scope: CoroutineScope) {
    private val repository = CustomerRepository()

    // --- Auth State ---
    var isLoading by mutableStateOf(false)
    var isAuthSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var authToken by mutableStateOf<String?>(null)

    // --- User State ---
    var walletBalance by mutableDoubleStateOf(0.0)
    var selectedLanguage by mutableStateOf("hi")

    // --- Discovery State ---
    var partners by mutableStateOf<List<ListenerDiscovery>>(emptyList())
    var isDiscoverLoading by mutableStateOf(false)
    var selectedDiscoverLanguage by mutableStateOf("All")
    var searchQuery by mutableStateOf("")

    // --- Call State ---
    var activeSessionId by mutableStateOf<String?>(null)
    var currentCallingPartner by mutableStateOf<ListenerDiscovery?>(null)
    var showPostCallRating by mutableStateOf(false)
    var lastCallDuration by mutableStateOf(180)
    var lastCallCoinsDeducted by mutableStateOf(27)
    var callEventsJob: Job? = null

    fun sendOtp(phone: String) {
        isLoading = true
        errorMessage = null
        scope.launch {
            val res = repository.requestOtp(phone)
            isLoading = false
            if (!res.success) {
                errorMessage = res.error?.message ?: "Failed to send OTP"
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
                onLoginSuccess()
            } else {
                errorMessage = res.error?.message ?: "Invalid OTP"
            }
        }
    }

    fun fetchUserProfile() {
        scope.launch {
            val res = repository.getUserProfile()
            if (res.success && res.data != null) {
                walletBalance = res.data.balance
                selectedLanguage = res.data.user.language_pref
            }
        }
    }

    fun fetchListeners() {
        isDiscoverLoading = true
        scope.launch {
            val res = repository.getListeners(
                language = if (selectedDiscoverLanguage == "All") null else selectedDiscoverLanguage,
                search = if (searchQuery.isBlank()) null else searchQuery
            )
            isDiscoverLoading = false
            if (res.success && res.data != null) {
                partners = res.data
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

    fun initiateRecharge(paise: Long, micros: Long) {
        scope.launch {
            val res = repository.initiateRecharge("pack_${paise / 100}")
            if (res.success && res.data != null) {
                println("Order created: ${res.data.order_id}")
            }
        }
    }

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
        val pid = currentCallingPartner?.id
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
            repository.observeCallEvents(sessionId).collect { event ->
                when (event.type) {
                    "balance_updated" -> fetchUserProfile()
                    "call_ended" -> {
                        callEventsJob?.cancel()
                    }
                }
            }
        }
    }
}
