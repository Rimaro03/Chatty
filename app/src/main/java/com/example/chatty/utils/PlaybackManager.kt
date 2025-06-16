package com.example.chatty.utils

import android.app.Application
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.example.chatty.models.Chat
import com.example.chatty.notifications.MediaNotification
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor(
    application: Application,
    private val mediaNotification: MediaNotification
) {
    private var player: ExoPlayer = ExoPlayer.Builder(application.applicationContext).build()
    private var mediaSession = MediaSession.Builder(application.applicationContext, player).build()

    fun startPlayback(chat: Chat) {
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle("My presentation")
            .setArtist(chat.name)
            .setArtworkUri("android.resource://com.example.chatty/${chat.icon}".toUri())
            .build()
        val mediaItem = MediaItem.Builder()
            .setUri(chat.audio.toUri())
            .setMediaMetadata(mediaMetadata)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        mediaNotification.createMediaNotification(player.isPlaying, mediaSession)
    }

    fun stopPlayback() {
        player.stop()
    }

    fun release() {
        player.release()
        mediaSession.release()
    }
}