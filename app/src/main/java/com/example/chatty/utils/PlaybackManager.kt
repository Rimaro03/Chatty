package com.example.chatty.utils

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor(
    application: Application,
    private val notifications: Notifications
) {
    private var player: ExoPlayer = ExoPlayer.Builder(application.applicationContext).build()
    private lateinit var mediaSession: MediaSession

    init {
        Log.d("PlaybackManager", "PlaybackManager created")
        mediaSession = MediaSession.Builder(application.applicationContext, player).build()
    }

    fun startPlayback(url: String) {
        val mediaItem = MediaItem.fromUri(url.toUri())
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        notifications.createMediaNotification(player.isPlaying, mediaSession)
    }

    fun stopPlayback() {
        player.stop()
    }

    fun release() {
        player.release()
        mediaSession.release()
    }
}