package com.amigo.rtc

import android.content.Context
import android.util.Log
import android.view.SurfaceView
import android.view.TextureView
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import io.agora.rtc.audio.AudioRecordingConfiguration
import io.agora.rtc.video.CameraCapturerConfiguration
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import java.io.File

class RtcManager private constructor() {

    private var rtcEngine: RtcEngine? = null
    private var rtcListener: RtcListener? = null
    private var isDownload = false
    private var context: Context? = null
    private var appId: String? = null
    private var version: String? = null

    var isInit: Boolean = false
        private set(value) {
            field = value
        }

    companion object {

        private const val TAG = "[ RtcManager ]"
        private val ins by lazy { RtcManager() }
        fun getInstance(): RtcManager {
            return ins
        }
    }

    private fun initializeRtc(context: Context, appId: String, version: String) {
        val isInitSuccess = try {
            rtcListener = null
//            val path =
//                "${Environment.getDataDirectory().path}/data/${context.packageName}/rtc/$version"
//            RtcEngine.setAgoraLibPath(path)
            rtcListener = RtcListener()
            rtcEngine = RtcEngine.create(context, appId, rtcListener)
            setVideoEncoderConfiguration()
            rtcEngine?.enableVideo()
            rtcEngine?.enableAudio()
            rtcEngine?.enableDualStreamMode(true)
            rtcEngine?.enableFaceDetection(true)
            rtcEngine?.enableAudioVolumeIndication(1000, 5, true)
            rtcEngine?.adjustRecordingSignalVolume(200)
            rtcEngine?.adjustPlaybackSignalVolume(200)
            rtcEngine?.setEnableSpeakerphone(true)
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
        isInit = isInitSuccess && rtcEngine != null
    }

    private fun setVideoEncoderConfiguration() {
        val level = DeviceLevelUtils.CURRENT_DEVICE_LEVEL
        val configuration = VideoEncoderConfiguration()
        configuration.dimensions = when (level) {
            DeviceLevelUtils.DEVICE_LEVEL_HIGH -> VideoEncoderConfiguration.VD_1280x720
            DeviceLevelUtils.DEVICE_LEVEL_MID -> VideoEncoderConfiguration.VD_840x480
            DeviceLevelUtils.DEVICE_LEVEL_LOW -> VideoEncoderConfiguration.VD_640x360
            else -> VideoEncoderConfiguration.VD_640x360
        }
        configuration.frameRate =
            if (level >= DeviceLevelUtils.DEVICE_LEVEL_MID) VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_24.value else VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.value
        configuration.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE
        configuration.orientationMode =
            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        rtcEngine?.setVideoEncoderConfiguration(configuration)
    }

    /**
     * 初始化RTC
     * @param appId 声网
     * @param version 声网SDK版本
     */
    fun initRtc(context: Context, appId: String, version: String) {
        if (isInit) {
            return
        }
        initializeRtc(context, appId, version)
//        this.context = context.applicationContext
//        this.appId = appId
//        this.version = version
//        val hasLibrary = RtcTool.hasDynamicLibrary(context, version)
//        if (hasLibrary) {
//            initializeRtc(context, appId, version)
//            return
//        }
//        val hasZip = RtcTool.hasDynamicLibraryZip(context, version)
//        if (hasZip) {
//            ThreadPool.postRunnableToSingleThread {
//                val result = RtcTool.unZipLibrary(context, version)
//                if (result) {
//                    initializeRtc(context, appId, version)
//                } else {
//                    downloadLibrary(context, appId, version)
//                }
//            }
//            return
//        }
//        //执行下载
//        downloadLibrary(context, appId, version)
    }

    /**
     * 添加Rtc的监听器
     */
    fun addRtcListener(rtcListener: IRtcListener) {
        this.rtcListener?.addRtcListener(rtcListener)
    }

    /**
     * 移除Rtc的监听器
     */
    fun removeRtcListener(rtcListener: IRtcListener) {
        this.rtcListener?.removeRtcListener(rtcListener)
    }

    /**
     * 加入频道
     */
    fun joinChannel(token: String, channel: String, uid: Int): Int {
        if (isInit) {
            val resultCode = rtcEngine?.joinChannel(token, channel, "", uid)
            if (resultCode == 0) {
                this.rtcListener?.currentChannel = channel
            }
            "joinChannel resultCode: $resultCode".printLog()
            return resultCode ?: -999
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
        return -999
    }

    /**
     * 设置身份
     * CLIENT_ROLE_BROADCASTER = 1;
     * CLIENT_ROLE_AUDIENCE = 2;
     */
    fun setClinetRole(role: Int) {
        if (role == 1 || role == 2) {
            rtcEngine?.setClientRole(role)
        }
    }

    /**
     * 设置频道的场景
     * CHANNEL_PROFILE_COMMUNICATION = 0; 通信
     * CHANNEL_PROFILE_LIVE_BROADCASTING = 1; 直播
     * CHANNEL_PROFILE_GAME = 2; 游戏
     */
    fun setChannelProfile(channelProfile: Int) {
        if (channelProfile in 0..2) {
            rtcEngine?.setChannelProfile(channelProfile)
        }
    }

    fun enableLocalAudio() {
        if (isInit) {
            rtcEngine?.enableLocalAudio(false)
            rtcEngine?.enableLocalAudio(true)
            rtcEngine?.muteLocalAudioStream(false)
        }
    }

    /**
     * 创建视图画面
     */
    fun createTextureView(context: Context?): TextureView? {
        if (!isInit) return context?.let { TextureView(it) }
        return context?.let {
            RtcEngine.CreateTextureView(it)
        }
    }

    /**
     * 创建视图画面
     */
    fun createRendererView(context: Context?): SurfaceView? {
        if (!isInit) return context?.let { SurfaceView(it) }
        return context?.let {
            RtcEngine.CreateRendererView(it)
        }
    }

    /**
     * 设置本地视频流
     */
    fun setupLocalVideo(uid: Int, textureView: TextureView?) {
        if (isInit) {
            rtcEngine?.enableLocalVideo(true)
            "setupLocalVideo $rtcEngine".printLog(TAG)
            val canvas = VideoCanvas(textureView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
            rtcEngine?.setupLocalVideo(canvas)
            rtcEngine?.setLocalRenderMode(
                Constants.RENDER_MODE_HIDDEN,
                Constants.VIDEO_MIRROR_MODE_AUTO
            )
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 设置本地视频流
     */
    fun setupLocalVideo(uid: Int, surfaceView: SurfaceView?) {
        if (isInit) {
            "setupLocalVideo $rtcEngine".printLog(TAG)
            val canvas = VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
            rtcEngine?.setupLocalVideo(canvas)
            rtcEngine?.setLocalRenderMode(
                Constants.RENDER_MODE_HIDDEN,
                Constants.VIDEO_MIRROR_MODE_AUTO
            )
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 设置远端视频流
     */
    fun setupRemoteVideo(uid: Int, textureView: TextureView?) {
        if (isInit) {
            "setupRemoteVideo $rtcEngine".printLog(TAG)
            val canvas = VideoCanvas(textureView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
            rtcEngine?.setupRemoteVideo(canvas)
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 切换摄像头
     */
    fun switchCamera() {
        if (isInit) {
            rtcEngine?.switchCamera()
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 使用前置摄像头
     */
    fun useFrontCamera(isFrontCamera: Boolean) {
        val direction =
            if (isFrontCamera) CameraCapturerConfiguration.CAMERA_DIRECTION.CAMERA_FRONT else CameraCapturerConfiguration.CAMERA_DIRECTION.CAMERA_REAR
        rtcEngine?.setCameraCapturerConfiguration(
            CameraCapturerConfiguration(
                CameraCapturerConfiguration.CAPTURER_OUTPUT_PREFERENCE.CAPTURER_OUTPUT_PREFERENCE_MANUAL,
                direction
            )
        )
        tryInitRtc()
    }

    /**
     * 开启预览
     */
    fun startPreview() {
        if (isInit) {
            rtcEngine?.startPreview()
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 停止预览
     */
    fun stopPreview() {
        if (isInit) {
            rtcEngine?.stopPreview()
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 获取本次通信ID
     */
    fun getCallId(): String? {
        if (isInit) {
            return rtcEngine?.callId
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
        return null
    }

    /**
     * 启用或禁用发送本地音频流
     * @param mute true 禁用 false 启用
     */
    fun muteLocalAudioStream(mute: Boolean) {
        if (isInit) {
            rtcEngine?.muteLocalAudioStream(mute)
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 启用或禁用发送远端音频流
     * @param mute true 禁用 false 启用
     * @param uid 远端用户ID
     */
    fun muteRemoteAudioStream(uid: Int, mute: Boolean) {
        if (isInit) {
            rtcEngine?.muteRemoteAudioStream(uid, mute)
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 启用或禁用发送本地视频流
     * @param mute true 禁用 false 启用
     */
    fun muteLocalVideoStream(mute: Boolean) {
        if (isInit) {
            rtcEngine?.muteLocalVideoStream(mute)
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 启用或禁用发送远端视频流
     * @param mute true 禁用 false 启用
     * @param uid 远端用户ID
     */
    fun muteRemoteVideoStream(uid: Int, mute: Boolean) {
        if (isInit) {
            rtcEngine?.muteRemoteVideoStream(uid, mute)
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 启用或禁用远端所有音频流
     */
    fun muteAllRemoteAudioStreams(mute: Boolean) {
        if (isInit) {
            rtcEngine?.muteAllRemoteAudioStreams(mute)
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 启用或禁用远端所有视频流
     */
    fun muteAllRemoteVideoStreams(mute: Boolean) {
        if (isInit) {
            rtcEngine?.muteAllRemoteVideoStreams(mute)
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 切换频道
     */
    fun switchChannel(token: String, channel: String) {
        if (isInit) {
            rtcEngine?.switchChannel(token, channel)
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    /**
     * 退出频道
     */
    fun leaveChannel() {
        if (isInit) {
            rtcEngine?.setupLocalVideo(null)
            rtcEngine?.leaveChannel()
            this.rtcListener?.currentChannel = ""
            return
        }
        tryInitRtc()
        "RtcManager not init".printLog(TAG)
    }

    fun destroy() {
        isInit = false
        RtcEngine.destroy()
    }

    private fun tryInitRtc() {
        if (isInit || isDownload) return
        if (context != null && !appId.isNullOrEmpty() && !version.isNullOrEmpty()) {
            initRtc(context!!, appId!!, version!!)
        }
    }


    private fun String.printLog(tag: String = TAG) {
        Log.e(tag, this)
    }

}