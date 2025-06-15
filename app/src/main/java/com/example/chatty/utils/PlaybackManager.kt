package com.example.chatty.utils

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.legacy.MediaMetadataCompat
import com.example.chatty.R
import com.example.chatty.models.Chat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor(
    application: Application,
    private val notifications: Notifications
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

        //val mediaItem = MediaItem.fromUri(chat.audio.toUri())
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()


        notifications.createMediaNotification(player.isPlaying, mediaSession)
    }

    fun stopPlayback() {
        player.stop()
    }

    fun release() {
        Log.d("PlaybackManager", "PlaybackManager released")
        player.release()
        mediaSession.release()
    }
}