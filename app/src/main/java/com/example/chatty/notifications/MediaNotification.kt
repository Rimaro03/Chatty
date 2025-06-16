package com.example.chatty.notifications

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.chatty.MainActivity
import com.example.chatty.R
import com.example.chatty.utils.PlaybackManager
import javax.inject.Inject

class MediaNotification @Inject constructor(
    private val application: Application
) {
    companion object {
        private const val CHANNEL_MEDIA = "media"

    }

    private val appContext = application.applicationContext
    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @OptIn(UnstableApi::class)
    fun createMediaNotification(isPlaying: Boolean, mediaSession: MediaSession) {
        val playPauseAction = if (isPlaying) {
            val intent = Intent(application, PlaybackManager::class.java).apply {
                this.action = "pause"
            }

            NotificationCompat.Action(
                android.R.drawable.ic_media_pause, "Pause",
                PendingIntent.getService(application, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            )
        } else {
            val intent = Intent(application, PlaybackManager::class.java).apply {
                this.action = "play"
            }

            NotificationCompat.Action(
                android.R.drawable.ic_media_play, "Play",
                PendingIntent.getService(application, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            )
        }

        val stopIntent = Intent(application, PlaybackManager::class.java).apply {
            this.action = "stop"
        }

        val stopAction = NotificationCompat.Action(
            android.R.drawable.ic_menu_close_clear_cancel, "Stop",
            PendingIntent.getService(application, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)
        )

        val mainActivityIntent = Intent(application, MainActivity::class.java)

        val notification = NotificationCompat.Builder(application, CHANNEL_MEDIA)
            // Show controls on lock screen even when user hides sensitive content.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.music_note_icon)
            // Add media control buttons that invoke intents in your media service
            .addAction(playPauseAction)
            .addAction(stopAction)
            .setContentIntent(PendingIntent.getActivity(application, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE))
            // Apply the media style template.
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession)
                    .setShowActionsInCompactView(1 /* #1: pause button \*/))

        notificationManager.notify(1, notification.build())
    }
}