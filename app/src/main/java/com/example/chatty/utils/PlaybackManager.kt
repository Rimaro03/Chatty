package com.example.chatty.utils

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession

class PlaybackManager(private val context: Context, private val notifications: Notifications) {
    // PLAYER
    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private var initialized: Boolean = false



    fun startPlayback(url: String) {
        player = ExoPlayer.Builder(context).build()
        val mediaItem = MediaItem.fromUri(url.toUri())
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        mediaSession = MediaSession.Builder(context, player).build()

        notifications.createMediaNotification(player.isPlaying, mediaSession)
        initialized = true
    }

    fun stopPlayback() {
        player.stop()
    }

    fun release() {
        if(!initialized)
            return
        player.release()
        mediaSession.release()
    }
}