package com.example.truelineapp.network.auth

import kotlinx.serialization.Serializable

// --- Request Models ---
@Serializable
data class OtpRequest(
    val phone: String,
    val role: String = "user"
)

@Serializable
data class OtpVerifyRequest(
    val phone: String,
    val otp: String,
    val role: String = "user"
)

// --- Response Data Models ---
@Serializable
data class OtpRequestData(
    val message: String,
    val phone: String,
    val expires_in_seconds: Int,
    val mock_otp: String? = null
)

@Serializable
data class OtpVerifyData(
    val token: String,
    val role: String,
    val is_new_user: Boolean,
    val user: UserData
)

@Serializable
data class UserData(
    val id: String,
    val phone: String,
    val status: String
)
