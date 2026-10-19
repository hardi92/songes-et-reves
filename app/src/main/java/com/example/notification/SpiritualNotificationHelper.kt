package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object SpiritualNotificationHelper {
    private const val CHANNEL_ID = "channel_spiritual_dreams"
    private const val CHANNEL_NAME = "Rappels Spirituels des Songes"
    private const val NOTIFICATION_ID_MORNING = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Rappels au réveil pour enregistrer les songes et visions reçus"
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendMorningDreamPromptNotification(context: Context) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Avez-vous fait un songe cette nuit ?")
            .setContentText("Enregistrez votre songe par la voix avant qu'il ne s'échappe de votre mémoire.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("« Dieu parle cependant, par des songes, par des visions nocturnes... » (Job 33:14). Prenez un instant pour dicter votre songe et découvrir son interprétation spirituelle.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID_MORNING, builder.build())
        } catch (_: SecurityException) {
            // Permission POST_NOTIFICATIONS may not be granted yet on Android 13+
        }
    }
}
