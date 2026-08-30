package com.example.truelineapp

import com.example.truelineapp.otp.getMsg91OtpGateway
import kotlin.test.Test
import kotlin.test.assertFalse
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
}
