package com.cute.uibase.media

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.cute.picture.loadImage
import com.cute.picture.loadVideo
import com.cute.uibase.R
import com.cute.uibase.databinding.VideoViewDouyinBinding
import com.cute.uibase.invisible
import com.cute.uibase.visible

@OptIn(UnstableApi::class)
class DouYinVideoView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0
) : RelativeLayout(context, attributeSet, defStyle), DouYinPlayerHelper.PlayerListener {

    private val binding: VideoViewDouyinBinding
    private var currentPosition: Long = 0
    private var totalDuration: Long = 0
    private var url: String = ""

    init {
        val view = View.inflate(context, R.layout.video_view_douyin, this)
        binding = VideoViewDouyinBinding.bind(view)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        DouYinPlayerHelper.getHelper(context).setListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        currentPosition = 0
        totalDuration = 0
        DouYinPlayerHelper.getHelper(context).removeListener(this)
    }

    override fun onLoadingVideo(videoUrl: String, isLoading: Boolean) {
        if (videoUrl.isEmpty() || videoUrl != url) return
        if (isLoading) {
            showLoading()
        }
    }

    override fun onBuffering(videoUrl: String) {
        if (videoUrl.isEmpty() || videoUrl != url) return
        showLoading()
    }

    override fun onUpdateVideoPosition(videoUrl: String, currentPosition: Long) {
        if (videoUrl.isEmpty() || videoUrl != url) return
        this.currentPosition = currentPosition
    }

    override fun onVideoDuration(videoUrl: String, duration: Long) {
        if (videoUrl.isEmpty() || videoUrl != url) return
        this.totalDuration = currentPosition
    }


    override fun onVideoPlaying(videoUrl: String) {
        if (videoUrl.isEmpty() || videoUrl != url) return
        binding.ivCover.invisible()
        hideLoading()
    }

    override fun onVideoPause(videoUrl: String) {
        if (videoUrl.isEmpty() || videoUrl != url) return
    }

    override fun onVideoSize(videoUrl: String, width: Int, height: Int) {
        if (videoUrl.isEmpty() || videoUrl != url) return
        adaptivePlayerSize(width, height)
    }

    @OptIn(UnstableApi::class)
    private fun adaptivePlayerSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth > videoHeight) {
            binding.tvVideoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        } else {
            binding.tvVideoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
    }

    private fun showLoading() {
        if (binding.progressCircular.visibility != View.VISIBLE) {
            binding.progressCircular.visible()
        }

    }

    private fun hideLoading() {
        if (binding.progressCircular.visibility == View.VISIBLE) {
            binding.progressCircular.invisible()
        }
    }


    fun setVideoData(videoUrl: String, videoCover: String?) {
        this.url = videoUrl
        DouYinPlayerHelper.getHelper(context).addUri(videoUrl)
        binding.tvVideoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        binding.ivCover.visible()
        if (!videoCover.isNullOrEmpty()) {
            binding.ivCover.loadImage(videoCover)
        } else {
            binding.ivCover.loadVideo(
                videoUrl, frameMillis = currentPosition
            )
        }
    }

    fun pause() {
        DouYinPlayerHelper.getHelper(context).pause()
    }

    fun play(url: String) {
        showLoading()
        DouYinPlayerHelper.getHelper(context).play(url, currentPosition, binding.tvVideoView)
    }
}