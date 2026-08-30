package com.example.truelineapp.call

expect class CallServiceWrapper {
    fun initialize(appId: Long, userId: String, userName: String)
    fun startAudioCall(
        roomId: String,
        targetUserId: String,
        targetUserName: String,
        token: String = "",
        signedUserId: String = "",
        zegoConfigFingerprint: String = "",
        onCallEnd: (durationSeconds: Int) -> Unit = {},
        onCallStartFailed: (message: String) -> Unit = {}
    )
    fun endCall()
}

expect fun getCallService(): CallServiceWrapper
