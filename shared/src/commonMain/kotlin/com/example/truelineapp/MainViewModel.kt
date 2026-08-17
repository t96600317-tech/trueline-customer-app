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

    fun initiateRecharge(packId: String) {
        scope.launch {
            val res = repository.initiateRecharge(packId)
            if (res.success && res.data != null) {
                println("Order created: ${res.data.order_id}")
            }
        }
    }

    fun connectToListener(listenerId: String) {
        scope.launch {
            val res = repository.initiateCall(listenerId)
            if (res.success && res.data != null) {
                activeSessionId = res.data.session_id
                startCallEventObserver(res.data.session_id)
                // In real app, join Zego room here
            } else {
                errorMessage = res.error?.message ?: "Call initiation failed"
            }
        }
    }

    private fun startCallEventObserver(sessionId: String) {
        callEventsJob?.cancel()
        callEventsJob = scope.launch {
            repository.observeCallEvents(sessionId).collect { event ->
                when (event.type) {
                    "balance_updated" -> fetchUserProfile()
                    "call_ended" -> {
                        activeSessionId = null
                        callEventsJob?.cancel()
                    }
                }
            }
        }
    }
}
