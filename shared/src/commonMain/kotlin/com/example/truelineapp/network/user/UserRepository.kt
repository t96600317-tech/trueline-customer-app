package com.example.truelineapp.network.user

import com.example.truelineapp.network.customer.CustomerRepository
import com.example.truelineapp.network.ApiResponse

class UserRepository(private val repository: CustomerRepository) {

    suspend fun getUserProfile(): ApiResponse<UserProfileData> {
        // We'll need a better way to map generic Map from CustomerRepository to UserProfileData
        // For now, let's just make CustomerRepository return the right type or use generic
        return repository.getUserProfile()
    }
    
    suspend fun getListeners(language: String? = null, search: String? = null) = 
        repository.getListeners(language, search)

    suspend fun requestOtp(phone: String) = repository.requestOtp(phone)
    
    suspend fun verifyOtp(phone: String, otp: String) = repository.verifyOtp(phone, otp)
    
    suspend fun initiateCall(listenerId: String) = repository.initiateCall(listenerId)
    
    suspend fun endCall(sessionId: String, reason: String) = repository.endCall(sessionId, reason)
    
    fun observeCallEvents(sessionId: String) = repository.observeCallEvents(sessionId)
    
    suspend fun initiateRecharge(amountPaise: Long, coinsMicros: Long) = 
        repository.initiateRecharge(amountPaise, coinsMicros)
}
