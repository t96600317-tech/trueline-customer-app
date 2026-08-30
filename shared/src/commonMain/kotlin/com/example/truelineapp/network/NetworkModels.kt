package com.example.truelineapp.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null
)

@Serializable
data class ApiError(
    val code: String,
    val message: String
)

@Serializable
data class OtpRequest(
    val phone: String,
    val role: String = "user"
)

@Serializable
data class OtpResponse(
    val message: String = "",
    val phone: String = "",
    val expires_in_seconds: Int = 300,
    val mock_otp: String? = null
)

@Serializable
data class OtpVerifyRequest(
    val phone: String,
    val otp: String,
    val role: String = "user",
    val request_id: String? = null,
    val msg91_access_token: String? = null
)

@Serializable
data class AuthResponse(
    val token: String,
    val role: String,
    val is_new_user: Boolean,
    val user: UserProfile? = null
)

@Serializable
data class UserProfile(
    val id: String,
    val language_pref: String,
    val status: String
)

@Serializable
data class ListenerDiscovery(
    val id: String,
    val name: String,
    val title: String,
    val photo_url: String = "",
    val audio_sample_url: String = "",
    val bio: String = "",
    val languages: List<String> = emptyList(),
    val rate_per_min: Double = 9.0,
    val rating_avg: Double = 4.8,
    val rating_count: Int = 0,
    val availability: String = "offline",
    val is_favourite: Boolean = false
)

@Serializable
data class CallInitiateRequest(
    val listener_id: String
)

@Serializable
data class CallInitiateResponse(
    val session_id: String,
    val room_id: String,
    val user_token: String,
    val zego_user_id: String = "",
    val zego_config_fingerprint: String = ""
)

@Serializable
data class CallSummary(
    val session_id: String,
    val status: String,
    val duration_seconds: Int,
    val coins_deducted_micros: Long,
    val end_reason: String = ""
)

@Serializable
data class CallEvent(
    val type: String,
    val session_id: String? = null,
    val reason: String? = null
)

@Serializable
data class RechargePack(
    val id: String,
    val amount_inr: Long,
    val amount_paise: Long,
    val coins: Long,
    val coins_micros: Long
)

@Serializable
data class RechargeRequest(
    val pack_id: String
)

@Serializable
data class RechargeResponse(
    val order_id: String,
    val payment_session_id: String = "",
    val amount_inr: Long = 0,
    val amount_paise: Long = 0,
    val coins: Long = 0,
    val coins_micros: Long = 0
)
