package com.example.truelineapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // Register FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                if (!token.isNullOrBlank()) {
                    TrueLineFirebaseMessagingService.saveFCMToken(this, token)
                    val storage = com.example.truelineapp.storage.getSessionStorage()
                    if (!storage.getAuthToken().isNullOrBlank()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val repo = com.example.truelineapp.network.customer.CustomerRepository()
                            repo.registerAndroidFCMDevice(token)
                        }
                    }
                }
            }
        }

        // Ensure status bar icons are dark (since background is light)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            App()
        }
    }
}
