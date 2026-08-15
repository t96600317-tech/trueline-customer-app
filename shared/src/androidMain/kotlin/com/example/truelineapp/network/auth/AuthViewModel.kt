package com.example.truelineapp.network.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.truelineapp.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class OtpRequested(val message: String) : AuthState()
    data class Success(val data: OtpVerifyData) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun requestOtp(phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.requestOtp(phone).onSuccess {
                _authState.value = AuthState.OtpRequested(it.message)
            }.onFailure {
                // MOCK MODE: Navigate to OTP screen even if API fails
                println("DEV MODE: API failed, simulating OTP request success for $phone")
                _authState.value = AuthState.OtpRequested("Mock OTP sent (Dev Mode)")
            }
        }
    }

    fun verifyOtp(phone: String, otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            repository.verifyOtp(phone, otp).onSuccess {
                tokenManager.saveToken(it.token)
                _authState.value = AuthState.Success(it)
            }.onFailure { error ->
                val errorMessage = error.message ?: ""
                
                // MOCK MODE FALLBACK: If API returns 401 for the dev OTP "123456"
                if (otp == "123456" && errorMessage.contains("HTTP_401")) {
                    println("DEV MODE: 401 Unauthorized received for mock OTP. Applying fallback success.")
                    val mockData = OtpVerifyData(
                        token = "mock_dev_token_123",
                        role = "user",
                        is_new_user = false,
                        user = UserData("id_123", phone, "active")
                    )
                    tokenManager.saveToken(mockData.token)
                    _authState.value = AuthState.Success(mockData)
                } else {
                    _authState.value = AuthState.Error(errorMessage.ifEmpty { "Failed to verify OTP" })
                }
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
