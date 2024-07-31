package com.cute.uibase.media

interface VideoPlayerListener {

    fun onVideoPrepare()

    fun onBuffering()

    fun onVideoPlaying()

    fun onVideoPause()

    fun onVideoPlayDuration(duration: Long)

    fun onVideoTotalDuration(duration: Long)

    fun onVideoSize(width: Int, height: Int)
    fun onVideoPlayComplete()

    fun onVideoError(errorCode: Int, errorName: String)
}