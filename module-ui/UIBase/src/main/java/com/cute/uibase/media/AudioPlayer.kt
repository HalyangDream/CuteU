package com.cute.uibase.media

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer

@OptIn(UnstableApi::class)
class AudioPlayer private constructor(private val exoPlayer: ExoPlayer) {


    companion object {
        internal fun build(context: Context): AudioPlayer {
            return AudioPlayer(
                ExoPlayer.Builder(context)
                    .setLoadControl(DefaultLoadControl.Builder()
                        .setPrioritizeTimeOverSizeThresholds(false)
                        .build())
                    .build()
            )
        }
    }


    fun play(uri: Uri, isLoop: Boolean = true) {
        exoPlayer.addMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.repeatMode = if (isLoop) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
        exoPlayer.play()
    }

    @OptIn(UnstableApi::class)
    fun play(raw: Int, isLoop: Boolean = true) {
        exoPlayer.addMediaItem(MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(raw)))
        exoPlayer.prepare()
        exoPlayer.repeatMode = if (isLoop) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun cancel() {
        exoPlayer.pause()
        exoPlayer.clearMediaItems()
    }


    fun release() {
        exoPlayer.clearMediaItems()
        exoPlayer.release()
    }
}