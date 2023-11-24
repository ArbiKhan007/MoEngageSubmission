package com.example.moengageproject.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Message received!")
        if (remoteMessage.data.isNotEmpty()) {
            // Handle data payload
            val data = remoteMessage.data["key"]
            // Process the data as needed
        }
        if (remoteMessage.notification != null) {
            // Handle notification payload
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body
            val image = remoteMessage.notification!!.imageUrl
            // Show notification using NotificationManager
            // or pass the data to an activity to display
            try {
                val handler = Handler(Looper.getMainLooper())

                handler.post {
                    NotificationUtils.showNotification(applicationContext, title, body)
                }
            }catch (ex:Exception){
                ex.printStackTrace()
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")
        // Send the token to your server if needed
    }
}