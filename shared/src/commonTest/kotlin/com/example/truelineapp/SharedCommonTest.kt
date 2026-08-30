package com.example.truelineapp

import kotlinx.coroutines.CoroutineScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.coroutines.EmptyCoroutineContext

class SharedCommonTest {

    @Test
    fun example() {
        assertEquals(3, 1 + 2)
    }

    @Test
    fun callConnectionFailureKeepsTheFullDiagnostic() {
        val viewModel = MainViewModel(CoroutineScope(EmptyCoroutineContext))
        val diagnostic = "Voice connection failed: Zego room login failed (reason=LOGIN_FAILED, code=100203)"

        viewModel.onCallConnectionFailed(diagnostic)

        assertEquals(diagnostic, viewModel.voiceCallErrorMessage)
    }
}
