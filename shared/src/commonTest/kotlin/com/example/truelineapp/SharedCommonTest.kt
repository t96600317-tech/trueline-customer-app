package com.example.truelineapp

import com.example.truelineapp.network.CallInitiateResponse
import com.example.truelineapp.network.OtpVerifyRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.coroutines.EmptyCoroutineContext

class SharedCommonTest {

    @Test
    fun otpVerificationKeepsTheMsg91AccessTokenForTheBackendFallback() {
        val payload = Json.encodeToString(
            OtpVerifyRequest(
                phone = "919876543210",
                otp = "123456",
                role = "user",
                request_id = "msg91-request-id",
                msg91_access_token = "msg91-success-token"
            )
        )

        assertTrue(payload.contains("\"request_id\":\"msg91-request-id\""))
        assertTrue(payload.contains("\"msg91_access_token\":\"msg91-success-token\""))
    }

    @Test
    fun callInitiateResponseRetainsTheServerSignedZegoIdentity() {
        val response = Json.decodeFromString<CallInitiateResponse>(
            """{"session_id":"session-42","room_id":"room-42","user_token":"signed-token","zego_user_id":"customer-42","zego_config_fingerprint":"app-1939552281"}"""
        )

        assertEquals("session-42", response.session_id)
        assertEquals("customer-42", response.zego_user_id)
        assertEquals("app-1939552281", response.zego_config_fingerprint)
    }

    @Test
    fun callInitiateResponseSupportsServersThatDoNotSendOptionalZegoDiagnostics() {
        val response = Json.decodeFromString<CallInitiateResponse>(
            """{"session_id":"session-42","room_id":"room-42","user_token":"signed-token"}"""
        )

        assertEquals("", response.zego_user_id)
        assertEquals("", response.zego_config_fingerprint)
    }

    @Test
    fun callConnectionFailureKeepsTheFullDiagnostic() {
        val viewModel = MainViewModel(CoroutineScope(EmptyCoroutineContext))
        val diagnostic = "Voice connection failed: Zego room login failed (reason=LOGIN_FAILED, code=100203)"

        viewModel.onCallConnectionFailed(diagnostic)

        assertEquals(diagnostic, viewModel.voiceCallErrorMessage)
    }
}
