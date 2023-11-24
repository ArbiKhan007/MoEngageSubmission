package com.example.moengageproject.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.moengageproject.R


object NotificationUtils {
    fun showNotification(context: Context, title: String?, body: String?) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel (for Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "random_channel_id"
            val channelName = "Random Channel"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Set the sound for the notification
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Build the notification
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, "foreground_channel_id")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(soundUri)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000)) // Vibrate pattern
                .setLights(-0xff0100, 300, 100) // LED pattern

        // Show the notification
        notificationManager.notify(1, builder.build())
    }
}