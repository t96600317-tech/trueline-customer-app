package com.example.truelineapp.call

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment

class ZegoCallActivity : FragmentActivity() {

    companion object {
        var onCallEndCallback: (() -> Unit)? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appId = intent.getLongExtra("APP_ID", 123456789L)
        val appSign = intent.getStringExtra("APP_SIGN") ?: "zegocloud_secret_32_bytes_long!"
        val userId = intent.getStringExtra("USER_ID") ?: ("user_" + System.currentTimeMillis())
        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        val callId = intent.getStringExtra("CALL_ID") ?: ("call_" + System.currentTimeMillis())

        val config = ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall().apply {
            turnOnCameraWhenJoining = false
            turnOnMicrophoneWhenJoining = true
            useSpeakerWhenJoining = true
        }

        val fragment = ZegoUIKitPrebuiltCallFragment.newInstance(
            appId,
            appSign,
            userId,
            userName,
            callId,
            config
        )

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .commitNow()
    }

    override fun onDestroy() {
        super.onDestroy()
        onCallEndCallback?.invoke()
        onCallEndCallback = null
    }
}
