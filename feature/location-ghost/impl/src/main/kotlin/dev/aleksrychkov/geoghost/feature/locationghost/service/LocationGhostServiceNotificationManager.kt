package dev.aleksrychkov.geoghost.feature.locationghost.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import dev.aleksrychkov.geoghost.feature.locationghost.R


internal interface LocationGhostServiceNotificationManager {
    companion object {
        lateinit var instance: LocationGhostServiceNotificationManager
        fun initialize(context: Context) {
            instance = LocationGhostServiceNotificationManagerImpl(context)
        }
    }

    fun createNotification(): Notification
}

private class LocationGhostServiceNotificationManagerImpl(
    private val context: Context
) : LocationGhostServiceNotificationManager {

    private companion object {
        const val NOTIFICATION_CHANNEL_ID =
            "dev.aleksrychkov.geoghost.location.ghost.service.channel"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Location ghost notifications channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Location ghost notifications channel"
                importance = NotificationManager.IMPORTANCE_MIN
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun createNotification(): Notification {
        val pm = context.packageManager
        val startIntent = pm.getLaunchIntentForPackage(context.packageName)
        startIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            startIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Location ghost is enabled")
            .setContentText("Current user location is mocked")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()
    }
}
