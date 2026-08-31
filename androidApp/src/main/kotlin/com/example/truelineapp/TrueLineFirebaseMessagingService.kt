package com.example.truelineapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrueLineFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHAT_CHANNEL_ID = "true_line_chats"
        const val CHAT_CHANNEL_NAME = "TrueLine Chats"
        private const val FCM_TOKEN_PREFS = "trueline_fcm_prefs"
        private const val FCM_TOKEN_KEY = "fcm_token"

        fun getSavedFCMToken(context: Context): String? {
            return context.getSharedPreferences(FCM_TOKEN_PREFS, Context.MODE_PRIVATE)
                .getString(FCM_TOKEN_KEY, null)
        }

        fun saveFCMToken(context: Context, token: String) {
            context.getSharedPreferences(FCM_TOKEN_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(FCM_TOKEN_KEY, token)
                .apply()
        }
    }

    override fun onNewToken(token: String) {
        if (token.isBlank()) return
        saveFCMToken(applicationContext, token)

        val storage = com.example.truelineapp.storage.getSessionStorage()
        val authToken = storage.getAuthToken()
        if (!authToken.isNullOrBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                val repository = com.example.truelineapp.network.customer.CustomerRepository()
                repository.registerAndroidFCMDevice(token)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data

        val senderName = remoteMessage.notification?.title 
            ?: data["sender_name"] 
            ?: "TrueLine"
        val content = remoteMessage.notification?.body 
            ?: data["content"] 
            ?: "You have received a new message"
        val partnerId = data["partner_id"] ?: ""

        showChatNotification(
            context = applicationContext,
            title = senderName,
            message = content,
            partnerId = partnerId
        )
    }

    private fun showChatNotification(
        context: Context,
        title: String,
        message: String,
        partnerId: String
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("partner_id", partnerId)
            putExtra("sender_name", title)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            partnerId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHAT_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                partnerId.hashCode().let { if (it == 0) (1000..9999).random() else it },
                notification
            )
        } catch (_: SecurityException) {
            // Android 13+ permission not granted yet
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHAT_CHANNEL_ID,
                CHAT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming chat messages"
                enableVibration(true)
                enableLights(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
