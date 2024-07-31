package com.cute.uibase.media

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.SurfaceView
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.collection.arrayMapOf
import androidx.media3.common.C
import androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import androidx.media3.common.C.VideoScalingMode
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.drm.DefaultDrmSessionManagerProvider
import androidx.media3.exoplayer.hls.DefaultHlsDataSourceFactory
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.hls.playlist.DefaultHlsPlaylistParserFactory
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import java.io.File
import java.util.concurrent.Executor

@OptIn(UnstableApi::class)
internal class DouYinPlayerHelper private constructor(val context: Context) : Player.Listener {


    //    private var curTextureView: SurfaceView? = null
    private var playerView: PlayerView? = null
    private val videoSizeMap = arrayMapOf<String, VideoSize>()
    private val mediaSources = hashMapOf<String, MediaSource>()


    private val databaseProvider = StandaloneDatabaseProvider(context)

    // A download cache should not evict media, so should use a NoopCacheEvictor.
    private val downloadCache =
        SimpleCache(File(context.cacheDir, "short_video"), NoOpCacheEvictor(), databaseProvider)

    // Create a factory for reading the data from the network.
    private val dataSourceFactory = DefaultHttpDataSource.Factory()

    // Choose an executor for downloading data. Using Runnable::run will cause each download task to
    // download data on its own thread. Passing an executor that uses multiple threads will speed up
    // download tasks that can be split into smaller parts for parallel execution. Applications that
    // already have an executor for background downloads may wish to reuse their existing executor.
    private val downloadExecutor = Executor(Runnable::run)

    private val coreDownloadManager by lazy(LazyThreadSafetyMode.NONE) {
        val downloadManager = DownloadManager(
            context, databaseProvider, downloadCache, dataSourceFactory, downloadExecutor
        )
// Optionally, properties can be assigned to configure the download manager.
        downloadManager.maxParallelDownloads = 1
        downloadManager
    }

    private val downloadManager by lazy(LazyThreadSafetyMode.NONE) {
        val downloadManager = DownloadManager(
            context, databaseProvider, downloadCache, dataSourceFactory, downloadExecutor
        )
// Optionally, properties can be assigned to configure the download manager.
        downloadManager.maxParallelDownloads = 5
        downloadManager
    }

    private val cacheDataSource by lazy(LazyThreadSafetyMode.NONE) {
        CacheDataSource.Factory().setCache(downloadCache)
            .setUpstreamDataSourceFactory(dataSourceFactory).setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private val mediaSourceFactory by lazy(LazyThreadSafetyMode.NONE) {
        DefaultMediaSourceFactory(context).setDataSourceFactory(cacheDataSource)
    }

    private val exoPlayer = ExoPlayer.Builder(context).setLoadControl(
        DefaultLoadControl.Builder().setPrioritizeTimeOverSizeThresholds(false).build()
    ).setMediaSourceFactory(mediaSourceFactory).build()

    private val playerListeners = mutableListOf<PlayerListener>()

    private val handler = Handler(Looper.getMainLooper())

    private val updateProgressAction = object : Runnable {
        override fun run() {
            if (exoPlayer.isPlaying) {
                val uri = exoPlayer.currentMediaItem?.localConfiguration?.uri ?: ""
                val currentPosition = exoPlayer.currentPosition // 当前播放位置
                synchronized(playerListeners) {
                    for (playerListener in playerListeners) {
                        playerListener.onUpdateVideoPosition(uri.toString(), currentPosition)
                    }
                }
            }
            // 更新 UI 或其他操作
            handler.postDelayed(this, 500) // 每秒更新一次
        }
    }

    init {
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
        exoPlayer.addListener(this)
        handler.postDelayed(updateProgressAction, 500)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var helper: DouYinPlayerHelper? = null


        @Synchronized
        fun getHelper(context: Context): DouYinPlayerHelper {
            if (helper == null) {
                synchronized(DouYinPlayerHelper::class.java) {
                    helper = DouYinPlayerHelper(context)
                }
            }
            return helper!!
        }
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)
        synchronized(playerListeners) {
            val uri = exoPlayer.currentMediaItem?.localConfiguration?.uri ?: ""
            for (playerListener in playerListeners) {

                playerListener.onLoadingVideo(uri.toString(), isLoading && !exoPlayer.isPlaying)
            }
        }
    }


    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        synchronized(playerListeners) {
            val uri = exoPlayer.currentMediaItem?.localConfiguration?.uri ?: ""
            for (playerListener in playerListeners) {
                if (isPlaying) {
                    playerListener.onVideoPlaying(uri.toString())
                } else {
                    playerListener.onVideoPause(uri.toString())
                }
            }
        }
    }


    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        if (playbackState == ExoPlayer.STATE_READY && exoPlayer.duration != C.TIME_UNSET) {
            // 有效的时长
            val uri = exoPlayer.currentMediaItem?.localConfiguration?.uri ?: ""
            synchronized(playerListeners) {
                for (playerListener in playerListeners) {
                    playerListener.onVideoDuration(uri.toString(), exoPlayer.duration)
                }
            }
        }

        if (playbackState == ExoPlayer.STATE_BUFFERING) {
            synchronized(playerListeners) {
                val uri = exoPlayer.currentMediaItem?.localConfiguration?.uri ?: ""
                for (playerListener in playerListeners) {
                    playerListener.onBuffering(uri.toString())
                }
            }
        }
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        super.onVideoSizeChanged(videoSize)
        if (videoSize.height != 0 && videoSize.width != 0) {
            val uri = exoPlayer.currentMediaItem?.localConfiguration?.uri ?: ""
            videoSizeMap[uri.toString()] = videoSize
            synchronized(playerListeners) {
                for (playerListener in playerListeners) {
                    playerListener.onVideoSize(uri.toString(), videoSize.width, videoSize.height)
                }
            }
        }
    }


    private fun addCoreDownload(url: String) {
        val list = coreDownloadManager.currentDownloads
        if (list.isNotEmpty()) {
            val first = list[0]
            coreDownloadManager.removeDownload(first.request.id)
            addDownload(first.request.uri.toString())
        }
        downloadManager.removeDownload(url)
        val request =
            DownloadRequest.Builder(url, Uri.parse(url)).setMimeType(MimeTypes.VIDEO_MP4).build()
        coreDownloadManager.addDownload(request)
    }

    private fun addDownload(url: String) {
        val list = coreDownloadManager.currentDownloads
        if (list.isNotEmpty()) {
            val first = list[0]
            if (first.request.uri.toString() == url) return
        }
        val request =
            DownloadRequest.Builder(url, Uri.parse(url)).setMimeType(MimeTypes.VIDEO_MP4).build()
        downloadManager.addDownload(request)
    }

    private fun removeDownload(url: String) {
        downloadManager.removeDownload(url)
    }

    private fun removeCoreDownload(url: String) {
        coreDownloadManager.removeDownload(url)
    }

    private fun getMediaSource(context: Context, uri: Uri): MediaSource {
        val contentType = Util.inferContentType(uri)
        val mediaSource = when (contentType) {
            C.CONTENT_TYPE_HLS -> HlsMediaSource.Factory(cacheDataSource)
                .createMediaSource(MediaItem.fromUri(uri))

            C.CONTENT_TYPE_DASH -> DashMediaSource.Factory(cacheDataSource)
                .createMediaSource(MediaItem.fromUri(uri))

            C.CONTENT_TYPE_SS -> SsMediaSource.Factory(cacheDataSource)
                .createMediaSource(MediaItem.fromUri(uri))

            C.CONTENT_TYPE_RTSP -> RtspMediaSource.Factory()
                .createMediaSource(MediaItem.fromUri(uri))

            else -> ProgressiveMediaSource.Factory(cacheDataSource)
                .createMediaSource(MediaItem.fromUri(uri))
        }
        return mediaSource
    }

    fun addUri(url: String) {
        addDownload(url)
        mediaSources[url] = getMediaSource(context, Uri.parse(url))
        if (videoSizeMap.contains(url)) {
            val videoSize = videoSizeMap[url]
            synchronized(playerListeners) {
                for (playerListener in playerListeners) {
                    playerListener.onVideoSize(url, videoSize!!.width, videoSize.height)
                }
            }
        }
    }


    fun pause() {
        exoPlayer.pause()
    }

    //    DefaultDrmSessionManagerProvider
//    DefaultHlsPlaylistParserFactory
//    DefaultHlsPlaylistTracker.FACTORY
//    HlsExtractorFactory.DEFAULT
//    DefaultLoadErrorHandlingPolicy
//    DefaultCompositeSequenceableLoaderFactory
    fun play(url: String, duration: Long, textureView: PlayerView) {
        val mediaSource = if (mediaSources.contains(url)) {
            mediaSources[url]
        } else {
            val source = getMediaSource(context, Uri.parse(url))
            mediaSources[url] = source
            source
        }
        addCoreDownload(url)
        if (mediaSource != null) {
            exoPlayer.setMediaSource(mediaSource)
        }
        if (playerView == null || textureView != playerView) {
            if (playerView != null) {
                playerView!!.player = null
                exoPlayer.clearVideoSurface()
                exoPlayer.clearVideoSurfaceView(playerView!!.videoSurfaceView as SurfaceView)
            }
            this.playerView = textureView
            textureView.player = exoPlayer
//        exoPlayer.setVideoSurfaceView(curTextureView)
            exoPlayer.seekTo(duration)
            exoPlayer.prepare()
            exoPlayer.play()
        } else {
            exoPlayer.play()
        }

//        if (curTextureView == null || curTextureView != textureView) {
//            if (curTextureView != null) {
//                exoPlayer.clearVideoSurface()
//                exoPlayer.clearVideoSurfaceView(curTextureView)
////                exoPlayer.clearVideoTextureView(curTextureView)
//            }
//            curTextureView = textureView
////            exoPlayer.setVideoTextureView(curTextureView)
//            exoPlayer.setVideoSurfaceView(curTextureView)
//            exoPlayer.seekTo(index, duration)
//            exoPlayer.prepare()
//            exoPlayer.play()
//        } else {
//            exoPlayer.play()
//        }
    }


    fun setListener(listener: PlayerListener) {
        synchronized(playerListeners) {
            playerListeners.add(listener)
        }

    }

    fun removeListener(listener: PlayerListener) {
        synchronized(playerListeners) {
            playerListeners.remove(listener)
        }
    }


    fun releaseResource() {
        videoSizeMap.clear()
        mediaSources.clear()
        coreDownloadManager.removeAllDownloads()
        downloadManager.removeAllDownloads()
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
//        curTextureView = null
        exoPlayer.removeListener(this)
        exoPlayer.release()
    }

    interface PlayerListener {

        fun onLoadingVideo(videoUrl: String, isLoading: Boolean)

        fun onBuffering(videoUrl: String)

        fun onUpdateVideoPosition(videoUrl: String, currentPosition: Long)

        fun onVideoDuration(videoUrl: String, duration: Long)

        fun onVideoSize(videoUrl: String, width: Int, height: Int)

        fun onVideoPause(videoUrl: String)

        fun onVideoPlaying(videoUrl: String)
    }
}