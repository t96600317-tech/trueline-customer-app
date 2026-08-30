package com.example.truelineapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Initialize Android Session Storage for persistent customer JWT
        com.example.truelineapp.storage.initCustomerSessionStorage(this)
        com.example.truelineapp.otp.initMsg91Otp(
            widgetId = BuildConfig.MSG91_WIDGET_ID,
            authToken = BuildConfig.MSG91_AUTH_TOKEN
        )
        com.example.truelineapp.audio.initAudioPlayer(this)
        com.example.truelineapp.call.initCallService(this)

        // Ensure status bar icons are dark (since background is light)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            App()
        }
    }
}
