package com.cute.uibase.media

abstract class VideoPlayerAdapterListener : VideoPlayerListener {

//     fun videoPrepare()
//     fun buffering()
//    abstract fun videoPlaying()
//    abstract fun videoPause()
//    abstract fun videoPlayDuration(duration: Long)
//    abstract fun videoTotalDuration(duration: Long)
//    abstract fun videoSize(width: Int, height: Int)
//    abstract fun videoPlayComplete()
//    abstract fun videoError(errorCode: Int, errorName: String)
    override fun onVideoPrepare() {

    }

    override fun onBuffering() {
    }

    override fun onVideoPlaying() {
    }

    override fun onVideoPause() {
    }

    override fun onVideoPlayDuration(duration: Long) {
    }

    override fun onVideoTotalDuration(duration: Long) {
    }

    override fun onVideoSize(width: Int, height: Int) {
    }

    override fun onVideoPlayComplete() {
    }

    override fun onVideoError(errorCode: Int, errorName: String) {

    }
}