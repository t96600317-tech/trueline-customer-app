package com.example.truelineapp.network.customer

import com.example.truelineapp.network.*
import com.example.truelineapp.network.chat.ChatConversationData
import com.example.truelineapp.network.chat.ChatMessageData
import com.example.truelineapp.network.user.UserProfileData
import com.example.truelineapp.storage.getSessionStorage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CashfreeOrderResponse(
    val order_id: String,
    val payment_session_id: String,
    val order_status: String = "ACTIVE"
)

@Serializable
data class TransactionItem(
    val id: String,
    val amount: Double,
    val type: String, // credit, debit
    val description: String,
    val created_at: String
)

class CustomerRepository(
    private var primaryHost: String = "192.168.1.6:8080"
) {
    private val storage = getSessionStorage()
    private var authToken: String? = storage.getAuthToken()
    private val candidateHosts = listOf(
        "192.168.1.6:8080",
        "10.0.2.2:8080",
        "127.0.0.1:8080",
        "localhost:8080"
    ).distinct()

    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 2500
            socketTimeoutMillis = 15000
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    fun setAuthToken(token: String) {
        authToken = token
        storage.saveAuthToken(token)
    }

    fun getAuthToken(): String? {
        if (authToken == null) {
            authToken = storage.getAuthToken()
        }
        return authToken
    }

    fun clearAuthSession() {
        authToken = null
        storage.clearSession()
    }

    private suspend inline fun <reified T> executeWithFallback(
        crossinline block: suspend (host: String) -> T
    ): T {
        var lastException: Exception? = null
        for (host in candidateHosts) {
            try {
                val result = block(host)
                primaryHost = host // Remember working host
                return result
            } catch (e: Exception) {
                lastException = e
            }
        }
        throw (lastException ?: Exception("Failed to connect to any backend host ($candidateHosts)"))
    }

    // --- Auth API ---
    suspend fun requestOtp(phone: String): ApiResponse<OtpResponse> {
        return try {
            executeWithFallback { host ->
                client.post("http://$host/api/v1/auth/otp/request") {
                    contentType(ContentType.Application.Json)
                    setBody(OtpRequest(phone, "user"))
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to connect to backend service"))
        }
    }

    suspend fun verifyOtp(phone: String, otp: String): ApiResponse<AuthResponse> {
        val response: ApiResponse<AuthResponse> = try {
            executeWithFallback { host ->
                client.post("http://$host/api/v1/auth/otp/verify") {
                    contentType(ContentType.Application.Json)
                    setBody(OtpVerifyRequest(phone, otp, "user"))
                }.body()
            }
        } catch (e: Exception) {
            return ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to connect to backend service"))
        }

        if (response.success && response.data != null) {
            setAuthToken(response.data.token)
            storage.savePhone(phone)
        }
        return response
    }

    // --- User Profile & Language ---
    suspend fun getUserProfile(): ApiResponse<UserProfileData> {
        val token = getAuthToken() ?: return ApiResponse(false, error = ApiError("UNAUTHORIZED", "Not logged in"))
        return try {
            executeWithFallback { host ->
                client.get("http://$host/api/v1/user/me") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to load profile"))
        }
    }

    suspend fun updateLanguagePreference(langCode: String): ApiResponse<Map<String, String>> {
        val token = getAuthToken() ?: return ApiResponse(false, error = ApiError("UNAUTHORIZED", "Not logged in"))
        storage.saveLanguage(langCode)
        return try {
            executeWithFallback { host ->
                client.patch("http://$host/api/v1/user/language") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(mapOf("language_pref" to langCode))
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to update language"))
        }
    }

    // --- Discovery ---
    suspend fun getListeners(language: String? = null, search: String? = null): ApiResponse<List<ListenerDiscovery>> {
        return try {
            executeWithFallback { host ->
                client.get("http://$host/api/v1/listeners") {
                    language?.let { parameter("language", it) }
                    search?.let { parameter("search", it) }
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to load listeners"))
        }
    }

    // --- Calling ---
    suspend fun initiateCall(listenerId: String): ApiResponse<CallInitiateResponse> {
        val token = getAuthToken() ?: return ApiResponse(false, error = ApiError("UNAUTHORIZED", "Not logged in"))
        return try {
            executeWithFallback { host ->
                client.post("http://$host/api/v1/calls/initiate") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(CallInitiateRequest(listenerId))
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Call initiation failed"))
        }
    }

    suspend fun endCall(sessionId: String, reason: String = "user_hangup"): ApiResponse<Map<String, String>> {
        val token = getAuthToken() ?: return ApiResponse(false, error = ApiError("UNAUTHORIZED", "Not logged in"))
        return try {
            executeWithFallback { host ->
                client.post("http://$host/api/v1/calls/$sessionId/end") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(mapOf("reason" to reason))
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to end call"))
        }
    }

    suspend fun rateCall(sessionId: String, rating: Int, tags: List<String>, isFavorite: Boolean): ApiResponse<Map<String, String>> {
        val token = getAuthToken() ?: return ApiResponse(false, error = ApiError("UNAUTHORIZED", "Not logged in"))
        return try {
            executeWithFallback { host ->
                client.post("http://$host/api/v1/calls/$sessionId/rate") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(mapOf(
                        "rating" to rating,
                        "tags" to tags,
                        "is_favorite" to isFavorite
                    ))
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to submit rating"))
        }
    }

    fun observeCallEvents(sessionId: String): Flow<CallEvent> = flow {
        val token = getAuthToken()
        client.webSocket(
            method = HttpMethod.Get,
            host = primaryHost.split(":")[0],
            port = primaryHost.split(":").getOrNull(1)?.toInt() ?: 8080,
            path = "/api/v1/calls/$sessionId/events?token=$token"
        ) {
            while (true) {
                try {
                    val event = receiveDeserialized<CallEvent>()
                    emit(event)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }

    // --- Payments & Wallet ---
    suspend fun getRechargeCatalogue(): ApiResponse<List<RechargePack>> {
        return try {
            executeWithFallback { host ->
                client.get("http://$host/api/v1/payments/catalogue").body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to load catalogue"))
        }
    }

    suspend fun createCashfreeOrder(amountPaise: Long, coins: Long): ApiResponse<CashfreeOrderResponse> {
        val token = getAuthToken() ?: return ApiResponse(false, error = ApiError("UNAUTHORIZED", "Not logged in"))
        return try {
            executeWithFallback { host ->
                client.post("http://$host/api/v1/payments/create-order") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(mapOf(
                        "amount_paise" to amountPaise,
                        "coins" to coins
                    ))
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to create payment order"))
        }
    }

    suspend fun verifyCashfreeOrder(orderId: String): ApiResponse<Map<String, String>> {
        val token = getAuthToken() ?: return ApiResponse(false, error = ApiError("UNAUTHORIZED", "Not logged in"))
        return try {
            executeWithFallback { host ->
                client.get("http://$host/api/v1/payments/orders/$orderId/verify") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to verify order"))
        }
    }

    suspend fun getTransactionHistory(): ApiResponse<List<TransactionItem>> {
        val token = getAuthToken() ?: return ApiResponse(false, error = ApiError("UNAUTHORIZED", "Not logged in"))
        return try {
            executeWithFallback { host ->
                client.get("http://$host/api/v1/wallet/transactions") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to load transactions"))
        }
    }

    // --- Chat APIs ---
    suspend fun getChatConversations(): ApiResponse<List<ChatConversationData>> {
        val token = getAuthToken() ?: return ApiResponse(false, error = ApiError("UNAUTHORIZED", "Not logged in"))
        return try {
            executeWithFallback { host ->
                client.get("http://$host/api/v1/chat/conversations") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to load chats"))
        }
    }

    suspend fun getChatMessages(partnerId: String): ApiResponse<List<ChatMessageData>> {
        val token = getAuthToken() ?: return ApiResponse(false, error = ApiError("UNAUTHORIZED", "Not logged in"))
        return try {
            executeWithFallback { host ->
                client.get("http://$host/api/v1/chat/conversations/$partnerId/messages") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to load messages"))
        }
    }

    suspend fun sendChatMessage(partnerId: String, content: String): ApiResponse<ChatMessageData> {
        val token = getAuthToken() ?: return ApiResponse(false, error = ApiError("UNAUTHORIZED", "Not logged in"))
        return try {
            executeWithFallback { host ->
                client.post("http://$host/api/v1/chat/conversations/$partnerId/messages") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(mapOf("content" to content))
                }.body()
            }
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Failed to send message"))
        }
    }
}
