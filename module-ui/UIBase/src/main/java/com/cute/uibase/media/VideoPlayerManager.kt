package com.cute.uibase.media

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import java.io.File
import java.util.concurrent.Executor

@OptIn(UnstableApi::class)
object VideoPlayerManager {

    // Create a factory for reading the data from the network.
    private val dataSourceFactory = DefaultHttpDataSource.Factory()
    private lateinit var downloadManager: DownloadManager
    private var cacheDataSource: CacheDataSource.Factory? = null

    private var databaseProvider: DatabaseProvider? = null
    private var simpleCache: SimpleCache? = null


    fun init(context: Context) {
        val databaseProvider = getDatabaseProvider(context)
        // A download cache should not evict media, so should use a NoopCacheEvictor.
        downloadManager = DownloadManager(
            context,
            databaseProvider,
            getSimpleCache(context),
            dataSourceFactory,
            Executor(Runnable::run)
        )
        downloadManager.maxParallelDownloads = 2
    }


    private fun getDatabaseProvider(context: Context): DatabaseProvider {
        if (databaseProvider == null) {
            databaseProvider = StandaloneDatabaseProvider(context)
        }
        return databaseProvider!!
    }

    private fun getSimpleCache(context: Context): SimpleCache {
        if (simpleCache == null) {
            val databaseProvider = getDatabaseProvider(context)
            simpleCache = SimpleCache(
                File(context.cacheDir, "cache_video"),
                NoOpCacheEvictor(),
                databaseProvider
            )
        }
        return simpleCache!!
    }

    internal fun getCacheDataSource(context: Context): DataSource.Factory {
        if (cacheDataSource == null) {
            cacheDataSource = CacheDataSource.Factory().setCache(getSimpleCache(context))
                .setUpstreamDataSourceFactory(dataSourceFactory)
                .setCacheWriteDataSinkFactory(null)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }

        return cacheDataSource!!
    }


    fun getAudioPlayer(context: Context): AudioPlayer {
        return AudioPlayer.build(context)
    }

    fun getVideoPlayer(context: Context): VideoPlayer {
        return VideoPlayer.build(context)
    }

    /**
     * 释放抖音helper资源
     */
    fun releaseDouYinHelperResource(context: Context) {
        DouYinPlayerHelper.getHelper(context).releaseResource()
    }
    /**
     * 释放抖音helper的播放器
     */
    fun releaseDouYinPlayer(context: Context){
        DouYinPlayerHelper.getHelper(context).release()
    }

    internal fun downloadCache(url: String) {
        val request = DownloadRequest.Builder(url, Uri.parse(url))
            .setMimeType(MimeTypes.BASE_TYPE_VIDEO)
            .build()
        downloadManager.addDownload(request)
    }

    internal fun stopDownloadCache(url: String) {
        downloadManager.removeDownload(url)
    }

}