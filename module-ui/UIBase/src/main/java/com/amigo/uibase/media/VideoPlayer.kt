package com.amigo.uibase.media

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.C.VIDEO_SCALING_MODE_DEFAULT
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView


@OptIn(UnstableApi::class)
class VideoPlayer private constructor(
    private val context: Context,
    private val exoPlayer: ExoPlayer
) : Player.Listener {


    private val handler = Handler(Looper.getMainLooper())

    private val updateProgressAction = object : Runnable {
        override fun run() {
            if (exoPlayer.isPlaying) {
                val currentPosition = exoPlayer.currentPosition // 当前播放位置
                videoListener?.onVideoPlayDuration(currentPosition)
            }
            // 更新 UI 或其他操作
            handler.postDelayed(this, 500) // 每秒更新一次
        }
    }


    companion object {

        internal fun build(context: Context): VideoPlayer {
            return VideoPlayer(
                context,
                ExoPlayer.Builder(context).setLoadControl(
                    DefaultLoadControl.Builder()
                        .setPrioritizeTimeOverSizeThresholds(false)
                        .build()
                ).setRenderersFactory(
                    DefaultRenderersFactory(context)
                        .setEnableDecoderFallback(true)
                ).build()
            )
        }
    }

    private var videoListener: VideoPlayerListener? = null

    init {
        exoPlayer.videoScalingMode = VIDEO_SCALING_MODE_DEFAULT
        exoPlayer.addListener(this)
        handler.postDelayed(updateProgressAction, 500)
    }

    private fun getMediaSource(context: Context, uri: Uri): MediaSource {
        val contentType = Util.inferContentType(uri)
        val mediaSource = when (contentType) {
            C.CONTENT_TYPE_HLS -> HlsMediaSource.Factory(
                VideoPlayerManager.getCacheDataSource(
                    context
                )
            )
                .createMediaSource(MediaItem.fromUri(uri))

            C.CONTENT_TYPE_DASH -> DashMediaSource.Factory(
                VideoPlayerManager.getCacheDataSource(
                    context
                )
            ).createMediaSource(MediaItem.fromUri(uri))

            C.CONTENT_TYPE_SS -> SsMediaSource.Factory(VideoPlayerManager.getCacheDataSource(context))
                .createMediaSource(MediaItem.fromUri(uri))


            C.CONTENT_TYPE_RTSP -> RtspMediaSource.Factory()
                .createMediaSource(MediaItem.fromUri(uri))

            else -> ProgressiveMediaSource.Factory(VideoPlayerManager.getCacheDataSource(context))
                .createMediaSource(MediaItem.fromUri(uri))
        }
        return mediaSource
    }

    private fun prepare(
        mediaSource: MediaSource,
        isLoop: Boolean = false
    ) {

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.repeatMode = if (isLoop) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
        exoPlayer.prepare()

    }

    fun prepare(uri: Uri, isLoop: Boolean = false) {
        val mediaSource = getMediaSource(context, uri)
        if (mediaSource !is RtspMediaSource) {
            VideoPlayerManager.downloadCache(uri.toString())
        }
        prepare(mediaSource, isLoop)
    }

    fun prepare(rawResourceId: Int, isLoop: Boolean = false) {
        val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context)

        val progressiveUri = RawResourceDataSource.buildRawResourceUri(rawResourceId)
        val mediaSource: MediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(progressiveUri))

        prepare(mediaSource, isLoop)
    }

    fun play(videoView: PlayerView) {
        videoView.player = exoPlayer
        exoPlayer.play()
    }

    fun isPlaying(): Boolean = exoPlayer.isPlaying

    fun pause() {
        exoPlayer.pause()
    }

    fun resume() {
        exoPlayer.play()
    }

    fun seek(duration: Long) {
        exoPlayer.seekTo(duration)
    }

    fun handlePlayerError() {
        exoPlayer.stop()
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun muteVoice() {
        exoPlayer.volume = 0f
    }


    fun release() {
        val uri = exoPlayer.currentMediaItem?.localConfiguration?.uri
        if (uri != null) {
            VideoPlayerManager.stopDownloadCache(uri.toString())
        }
        this.videoListener = null
        exoPlayer.removeListener(this)
        exoPlayer.clearVideoSurface()
        exoPlayer.clearMediaItems()
        exoPlayer.release()
    }

    fun addListener(videoListener: VideoPlayerListener?) {
        this.videoListener = videoListener
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            Player.STATE_BUFFERING -> videoListener?.onBuffering()
            Player.STATE_ENDED -> videoListener?.onVideoPlayComplete()
            Player.STATE_READY -> {
                videoListener?.onVideoPrepare()
                if (exoPlayer.duration != C.TIME_UNSET) {
                    videoListener?.onVideoTotalDuration(exoPlayer.duration)
                }
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        videoListener?.onVideoError(error.errorCode, error.errorCodeName)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        if (isPlaying) {
            videoListener?.onVideoPlaying()
        } else {
            videoListener?.onVideoPause()
        }
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        super.onVideoSizeChanged(videoSize)
        if (videoSize.height != 0 && videoSize.width != 0) {
            videoListener?.onVideoSize(videoSize.width, videoSize.height)
        }
    }
}