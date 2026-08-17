package com.example.truelineapp.network.customer

import com.example.truelineapp.network.*
import com.example.truelineapp.network.user.UserProfileData
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
import kotlinx.serialization.json.Json

class CustomerRepository(
    private val baseUrl: String = "10.0.2.2:8080",
    private val useHttps: Boolean = false
) {
    private var authToken: String? = null
    private val httpProtocol = if (useHttps) "https" else "http"
    private val wsProtocol = if (useHttps) "wss" else "ws"

    private val client = HttpClient {
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
        defaultRequest {
            url("$httpProtocol://$baseUrl/api/v1/")
            authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    fun setAuthToken(token: String) {
        authToken = token
    }

    suspend fun requestOtp(phone: String): ApiResponse<Map<String, String>> {
        return try {
            client.post("auth/otp/request") {
                contentType(ContentType.Application.Json)
                setBody(OtpRequest(phone, "user"))
            }.body()
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Unknown error"))
        }
    }

    suspend fun verifyOtp(phone: String, otp: String): ApiResponse<AuthResponse> {
        val response: ApiResponse<AuthResponse> = try {
            client.post("auth/otp/verify") {
                contentType(ContentType.Application.Json)
                setBody(OtpVerifyRequest(phone, otp, "user"))
            }.body()
        } catch (e: Exception) {
            return ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Unknown error"))
        }

        if (response.success && response.data != null) {
            setAuthToken(response.data.token)
        }
        return response
    }

    suspend fun getUserProfile(): ApiResponse<UserProfileData> {
        return try {
            client.get("user/me").body()
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Unknown error"))
        }
    }

    suspend fun getListeners(language: String? = null, search: String? = null): ApiResponse<List<ListenerDiscovery>> {
        return try {
            client.get("listeners") {
                language?.let { parameter("language", it) }
                search?.let { parameter("search", it) }
            }.body()
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Unknown error"))
        }
    }

    suspend fun initiateCall(listenerId: String): ApiResponse<CallInitiateResponse> {
        return try {
            client.post("calls") {
                contentType(ContentType.Application.Json)
                setBody(CallInitiateRequest(listenerId))
            }.body()
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Unknown error"))
        }
    }

    suspend fun endCall(sessionId: String, reason: String = "user_hangup"): ApiResponse<Map<String, String>> {
        return try {
            client.post("calls/$sessionId/end") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("reason" to reason))
            }.body()
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Unknown error"))
        }
    }

    fun observeCallEvents(sessionId: String): Flow<CallEvent> = flow {
        client.webSocket(
            method = HttpMethod.Get,
            host = baseUrl.split(":")[0],
            port = baseUrl.split(":").getOrNull(1)?.toInt() ?: 80,
            path = "/api/v1/calls/$sessionId/events?token=$authToken"
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

    suspend fun getRechargeCatalogue(): ApiResponse<List<RechargePack>> {
        return try {
            client.get("payments/catalogue").body()
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Unknown error"))
        }
    }

    suspend fun initiateRecharge(packId: String): ApiResponse<RechargeResponse> {
        return try {
            client.post("user/recharge") {
                contentType(ContentType.Application.Json)
                setBody(RechargeRequest(packId))
            }.body()
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Unknown error"))
        }
    }

    suspend fun rateCall(sessionId: String, rating: Int, tags: List<String>, isFavorite: Boolean): ApiResponse<Map<String, String>> {
        return try {
            client.post("calls/$sessionId/rate") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "rating" to rating,
                    "tags" to tags,
                    "is_favorite" to isFavorite
                ))
            }.body()
        } catch (e: Exception) {
            ApiResponse(false, error = ApiError("NETWORK_ERROR", e.message ?: "Unknown error"))
        }
    }
}
