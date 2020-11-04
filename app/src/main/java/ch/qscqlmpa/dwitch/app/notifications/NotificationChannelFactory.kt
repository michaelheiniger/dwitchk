package ch.qscqlmpa.dwitch.app.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color

object NotificationChannelFactory {

    const val DEFAULT_CHANNEL_ID = "default_channel_id"
    private const val DEFAULT_CHANNEL_NAME = "General notifications"

    fun createDefaultNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            DEFAULT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
    }
}