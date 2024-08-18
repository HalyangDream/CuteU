package com.amigo.uibase.media.preview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.amigo.basic.BaseActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.uibase.R
import com.amigo.uibase.databinding.ActivityVideoPreviewBinding
import com.amigo.uibase.databinding.LayoutTitleBarBinding
import com.amigo.uibase.invisible
import com.amigo.uibase.media.VideoPlayer
import com.amigo.uibase.media.VideoPlayerListener
import com.amigo.uibase.screenHeight
import com.amigo.uibase.screenWidth
import com.amigo.uibase.visible

class VideoPreviewActivity : BaseActivity<ActivityVideoPreviewBinding>(), VideoPlayerListener {

    private lateinit var titleBarBinding: LayoutTitleBarBinding
    private val videoPlayer by lazy { VideoPlayer.build(this) }
    private var isComplete = false
    private var isError = false

    companion object {
        fun startPreview(context: Context, url: String) {
            val intent = Intent(context, VideoPreviewActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    override fun initViewBinding(layout: LayoutInflater): ActivityVideoPreviewBinding {
        return ActivityVideoPreviewBinding.inflate(layout)
    }

    @OptIn(UnstableApi::class)
    override fun initView() {
        StatusUtils.setStatusMode(false, this.window)
        val url = intent.getStringExtra("url")!!
        titleBarBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        StatusUtils.setImmerseLayout(titleBarBinding.flTitle, this)
        titleBarBinding.ivNavBack.setImageResource(R.drawable.ic_nav_back_white)
        titleBarBinding.tvTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
        videoPlayer.addListener(this)
        videoPlayer.prepare(Uri.parse(url), false)
        videoPlayer.play(viewBinding.videoView)
        titleBarBinding.ivNavBack.setOnClickListener {
            finish()
        }

        viewBinding.videoView.setOnClickListener {
            if (videoPlayer.isPlaying()) {
                videoPlayer.pause()
            } else {
                videoPlayer.resume()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        videoPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoPlayer.release()

    }

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

    override fun onVideoPlayComplete() {
        isComplete = true
    }

    override fun onVideoError(errorCode: Int, errorName: String) {
        isError = true
    }

    override fun onVideoSize(width: Int, height: Int) {
    }

}