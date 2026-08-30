package com.example.truelineapp

import com.example.truelineapp.otp.getMsg91OtpGateway
import com.example.truelineapp.call.getCallService
import com.example.truelineapp.storage.getSessionStorage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SharedLogicIOSTest {

    @Test
    fun iosUsesTheBackendOtpFallbackUntilTheNativeMsg91BridgeExists() {
        assertFalse(getMsg91OtpGateway().isConfigured)
    }

    @Test
    fun iosPlatformIdentificationIsAvailableToSharedCode() {
        assertTrue(getPlatform().name.contains("iOS"))
    }

    @Test
    fun iosProvidesAPlatformTimestampForOptimisticMessageIds() {
        assertTrue(currentPlatformTimeMillis() > 0)
    }

    @Test
    fun iosSessionStoragePersistsAndClearsCustomerAuthentication() {
        val storage = getSessionStorage()
        storage.clearSession()

        storage.saveAuthToken("customer-token")
        storage.savePhone("919876543210")

        assertEquals("customer-token", storage.getAuthToken())
        assertEquals("919876543210", storage.getPhone())

        storage.clearSession()

        assertNull(storage.getAuthToken())
        assertNull(storage.getPhone())
    }

    @Test
    fun iosCallBridgeRejectsAnEmptyServerIssuedToken() {
        var error: String? = null

        getCallService().startAudioCall(
            roomId = "room-42",
            targetUserId = "listener-42",
            targetUserName = "Listener",
            token = "",
            onCallStartFailed = { error = it }
        )

        assertEquals("A Zego token is required to start a voice call", error)
    }
}
