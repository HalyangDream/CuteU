package com.amigo.call

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.amigo.analysis.Analysis
import com.amigo.baselogic.statusDataStore
import com.amigo.baselogic.userDataStore
import com.amigo.basic.BaseModelActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.call.adapter.VideoChatAdapter
import com.amigo.call.adapter.VideoMsg
import com.amigo.call.databinding.ActivityCallBinding
import com.amigo.call.databinding.LayoutCallingBinding
import com.amigo.call.databinding.LayoutMakeCallBinding
import com.amigo.call.databinding.LayoutMakeCallMatchBinding
import com.amigo.call.dialog.UnLockDeviceFunctionDialog
import com.amigo.call.intent.CallIntent
import com.amigo.call.intent.TelephoneIntent
import com.amigo.call.state.CallState
import com.amigo.call.state.TelephoneCallerState
import com.amigo.call.viewmodel.CallViewModel
import com.amigo.im.IMCore
import com.amigo.im.service.MessageService
import com.amigo.logic.http.Gender
import com.amigo.logic.http.response.call.CallInfoResponse
import com.amigo.logic.http.response.call.DeviceFunctionEnum
import com.amigo.logic.http.response.call.DeviceFunctionInfo
import com.amigo.logic.http.response.user.ChatUserInfo
import com.amigo.message.custom.msg.CallRecordMessage
import com.amigo.picture.loadImage
import com.amigo.picture.transformation.RoundRadius
import com.amigo.tool.EventBus
import com.amigo.tool.EventBus.subscribe
import com.amigo.tool.KeyboardUtil
import com.amigo.tool.TimeUtil
import com.amigo.tool.Toaster
import com.amigo.tool.dpToPx
import com.amigo.uibase.ReportBehavior
import com.amigo.uibase.event.RemoteNotifyEvent
import com.amigo.uibase.event.StoreDialogCloseEvent
import com.amigo.uibase.gone
import com.amigo.uibase.invisible
import com.amigo.uibase.media.VideoPlayerAdapterListener
import com.amigo.uibase.setThrottleListener
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.media.VideoPlayerManager
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.IStoreService
import com.amigo.uibase.visible
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/**
 * author : mac
 * date   : 2021/10/26
 *
 */
class CallActivity : BaseModelActivity<ActivityCallBinding, CallViewModel>() {

    companion object {
        fun startLaunchCall(context: Context, bundle: Bundle) {
            val intent = Intent(context, CallActivity::class.java)
            intent.putExtra("callBundle", bundle)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    private val audioPlayer by lazy { VideoPlayerManager.getAudioPlayer(this) }
    private val videoPlayer by lazy { VideoPlayerManager.getVideoPlayer(this) }


    /**
     * 匹配通话的内容
     */
    private var isMatchCall = false
    private var matchId: Long? = null
    private var source: String? = null

    /**
     * 普通通话内容
     */
    private var callerInfo: Caller? = null
    private var isStrategyCall = false

    private var makeCallHolder: MakeCallHolder? = null
    private var makeMatchCallHolder: MakeCallMatchHolder? = null
    private var callingHolder: CallingHolder? = null

    /**
     * 当前是否可以播放视频
     */
    private var canPlayVideo = false

    /**
     * 当前视频是否准备好可以播放
     */
    private var isVideoPrepare = false

    /**
     * 视频播放多久结束
     */
    private var videoPlayEndTime: Int? = null

    override fun initViewBinding(layout: LayoutInflater): ActivityCallBinding {

        return ActivityCallBinding.inflate(layout)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.vTitle, this)
        observer()
        val bundle = intent.getBundleExtra("callBundle")
        if (bundle == null) {
            TelephoneService.processIntent(
                TelephoneIntent.FinishCommunication("无法获取到基本通话信息")
            )
            return
        }

        val caller = bundle.getLong("caller")

        source = bundle.getString("source")
        isMatchCall = bundle.getBoolean("isMatchCall", false)
        isStrategyCall = bundle.getBoolean("isStrategyCall", false)
        if (isMatchCall) {
            //加载匹配的页面
            matchId = bundle.getLong("matchId")
            callerInfo = Caller(callee = matchId!!, source = source!!)
            val view = layoutInflater.inflate(R.layout.layout_make_call_match, null)
            makeMatchCallHolder = MakeCallMatchHolder(view)
            viewBinding.root.addView(view, ConstraintLayout.LayoutParams(-1, -1))

        } else {
            val callee = bundle.getLong("callee")
            callerInfo = Caller(callee = callee, source = source!!)
            val view = layoutInflater.inflate(R.layout.layout_make_call, null)
            makeCallHolder = MakeCallHolder(view)
            viewBinding.root.addView(view, ConstraintLayout.LayoutParams(-1, -1))
            if (isStrategyCall) {
                makeCallHolder?.binding?.btnPickup?.visible()
                makeCallHolder?.binding?.btnDecline?.visible()
            } else {
                makeCallHolder?.binding?.btnPickup?.gone()
                makeCallHolder?.binding?.btnDecline?.visible()
            }
        }
    }

    
    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.release()
        videoPlayer.release()
    }

    override fun onBackPressed() {
    }


    private fun observer() {
        //收集状态
        lifecycleScope.launch(Dispatchers.Main) {
            TelephoneService.telephoneStateFlow.collect {
                if (it is TelephoneCallerState) {
                    handleCallerState(it)
                }
            }
        }

        viewModel.observerState {
            handleRtcState(it)
        }

        EventBus.event.subscribe<StoreDialogCloseEvent>(lifecycleScope) {
            if (isMatchCall && TelephoneService.stateIsMakeCall()) {
                TelephoneService.processIntent(
                    TelephoneIntent.FinishCommunication("Match Store Dialog Close")
                )
            }
        }

        EventBus.event.subscribe<RemoteNotifyEvent>(lifecycleScope) {
            if (it is RemoteNotifyEvent.PaySuccessEvent && TelephoneService.stateIsCalling()) {
                viewModel.processIntent(CallIntent.RequestDeviceFunction)
            }
        }
    }

    private fun prepareFakeVideo(videoUrl: String) {
        videoPlayer.addListener(object : VideoPlayerAdapterListener() {
            override fun onVideoPrepare() {
                super.onVideoPrepare()
                isVideoPrepare = true
                if (canPlayVideo && TelephoneService.stateIsMakeCall()) {
                    val callId = callerInfo?.callId
                    val callee = callerInfo?.callee
                    if (callId.isNullOrEmpty() || callee == null) {
                        TelephoneService.processIntent(TelephoneIntent.FinishCommunication("onVideoPrepare 视频播放出错"))
                    } else {
                        viewModel.processIntent(CallIntent.StartCall(callId, callee))
                    }
                }
            }

            override fun onVideoPlayDuration(duration: Long) {
                super.onVideoPlayDuration(duration)
                if (!TelephoneService.stateIsCalling()) return

                if (videoPlayEndTime != null && duration / 1000 >= videoPlayEndTime!!) {
                    videoPlayer.pause()
                    val callId = callerInfo?.callId
                    if (!callId.isNullOrEmpty()) {
                        viewModel.processIntent(
                            CallIntent.FinishCall(
                                callId, "视频播放达到指定时间"
                            )
                        )
                    }
                }
            }

            override fun onVideoPlayComplete() {
                super.onVideoPlayComplete()
                if (!TelephoneService.stateIsCalling()) return
                val callId = callerInfo?.callId
                if (!callId.isNullOrEmpty()) {
                    viewModel.processIntent(CallIntent.FinishCall(callId, "视频播放结束"))
                } else {
                    //处理异常
                    handleCallerState(TelephoneCallerState.FinishCallException("视频播放结束，通话频道是空"))
                }
            }

            override fun onVideoError(errorCode: Int, errorName: String) {
                super.onVideoError(errorCode, errorName)
                val callId = callerInfo?.callId
                if (callingHolder == null) {
                    TelephoneService.processIntent(TelephoneIntent.FinishCommunication("视频播放出错"))
                } else {
                    if (!callId.isNullOrEmpty()) {
                        viewModel.processIntent(CallIntent.FinishCall(callId, "视频播放出错"))
                    } else {
                        //处理异常
                        handleCallerState(TelephoneCallerState.FinishCallException("视频播放出错，通话频道是空"))
                    }
                }
            }
        })
        videoPlayer.prepare(Uri.parse(videoUrl), false)
    }

    private fun handleRtcState(state: CallState) {
        when (state) {

            is CallState.DeviceFunctionInfoResult -> callingHolder?.bindDeviceInfo(
                state.cameraClose, state.cameraSwitch, state.voiceMute
            )

            is CallState.UnlockDeviceFunctionResult -> callingHolder?.handleUnlockDeviceFunctionResult(
                state.result, state.info
            )

            is CallState.UserInfoResult -> {
                makeCallHolder?.bindUiInfo(state.userDetail)
            }

            is CallState.FollowState -> {
                callingHolder?.bindFollowState(state.isFollowState)
            }


            is CallState.StartCall -> {
                viewBinding.root.removeAllViews()
                callerInfo = callerInfo?.copy(callee = state.uid.toLong())
                TelephoneService.processIntent(TelephoneIntent.ProcessCall)
                val view = layoutInflater.inflate(R.layout.layout_calling, null)
                callingHolder = CallingHolder(view, state.callId)
                viewBinding.root.addView(view, ConstraintLayout.LayoutParams(-1, -1))
                callingHolder?.setupRemoteView(state.uid)

                val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    mutableListOf(
                        android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO
                    )
                } else {
                    mutableListOf(
                        android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO
                    )
                }
                requestMultiplePermission(*permissions.toTypedArray(), onGranted = {
                    if (it == android.Manifest.permission.RECORD_AUDIO) {
                        callingHolder?.setupLocalVoice()
                    }
                    if (it == android.Manifest.permission.CAMERA) {
                        viewBinding.root.postDelayed({
                            callingHolder?.setupLocalView()
                        }, 1000)
                    }
                }, onDenied = {
                    //提示用户需要给权限
                    Toaster.showShort(
                        this@CallActivity, com.amigo.uibase.R.string.str_please_grant_permission
                    )
                })
            }

            is CallState.UpdateCallDuration -> callingHolder?.updateDuration(state.durationSecond)

            is CallState.CallInfoResult -> callingHolder?.updateCallInfo(state.callInfoResponse)

            is CallState.FinishCallSuccess -> {
                TelephoneService.processIntent(
                    TelephoneIntent.FinishCall(state.callId, state.reason)
                )
                callingHolder?.binding?.ivCallClose?.isEnabled = true
            }

            is CallState.FinishCallFailure -> {
                //提示关闭错误
                callingHolder?.binding?.ivCallClose?.isEnabled = true
            }


            is CallState.HeartFinish -> {
                TelephoneService.processIntent(
                    TelephoneIntent.FinishCall(state.callId, "心跳结束")
                )
            }
        }
    }


    private fun handleCallerState(state: TelephoneCallerState) {
        when (state) {

            is TelephoneCallerState.OperateInvitedSuccess -> {
                if (isMatchCall) {
                    makeMatchCallHolder?.buttonEnable(true)
                } else {
                    if (isStrategyCall) {
                        makeCallHolder?.binding?.btnPickup?.gone()
                    }
                    makeCallHolder?.buttonEnable(true)
                }
                callerInfo = callerInfo?.copy(callee = state.callee, callId = state.callId)
                ReportBehavior.reportEvent("make_call", mutableMapOf<String, Any>().apply {
                    put("anchor_id", "${state.callee}")
                    put("source", "${source}")
                    put("call_channel_id", state.callId)
                })
            }

            is TelephoneCallerState.OperateCalleeBusy -> {
                if (isMatchCall) {
                    TelephoneService.processIntent(
                        TelephoneIntent.FinishCommunication("Match User Busy")
                    )
                } else {
                    if (isStrategyCall) {
                        makeCallHolder?.binding?.btnPickup?.gone()
                    }
                    makeCallHolder?.buttonEnable(true)
                    makeCallHolder?.handleErrorScenes(ErrorScenes.BUSY)
                }
            }

            is TelephoneCallerState.OperateCalleeOffline -> {
                if (isMatchCall) {
                    TelephoneService.processIntent(
                        TelephoneIntent.FinishCommunication("Match User Offline")
                    )
                } else {
                    if (isStrategyCall) {
                        makeCallHolder?.binding?.btnPickup?.gone()
                    }
                    makeCallHolder?.buttonEnable(true)
                    makeCallHolder?.handleErrorScenes(ErrorScenes.OFFLINE)
                }
            }

            is TelephoneCallerState.OperateInvitedFailure -> {
                if (isMatchCall) {
                    Log.i("TelephoneService", "OperateInvitedFailure code:${state.code}")
                    val storeService = RouteSdk.findService(IStoreService::class.java)
                    val result = storeService.hasStoreCode(state.code)
                    if (!result) {
                        TelephoneService.processIntent(
                            TelephoneIntent.FinishCommunication("Match Invited Failure")
                        )
                    }
                } else {
                    if (isStrategyCall) {
                        makeCallHolder?.binding?.btnPickup?.gone()
                    }
                    makeCallHolder?.buttonEnable(true)
                    makeCallHolder?.handleErrorScenes(ErrorScenes.FAILURE)
                }
            }

            is TelephoneCallerState.OperateCancelInvitedSuccess -> {
                TelephoneService.processIntent(
                    TelephoneIntent.FinishCommunication("CancelInvitedSuccess")
                )
            }

            is TelephoneCallerState.OperateCancelInvitedFailure -> {
                makeCallHolder?.buttonEnable(true)
                TelephoneService.processIntent(
                    TelephoneIntent.FinishCommunication("OperateCancelInvitedFailure")
                )
            }

            is TelephoneCallerState.CanPlayVideo -> {
                this.canPlayVideo = state.canPlay
                lifecycleScope.launch {
                    delay(1500)
                    if (canPlayVideo && isVideoPrepare && TelephoneService.stateIsMakeCall()) {
                        val callId = callerInfo?.callId!!
                        val callee = callerInfo?.callee!!
                        viewModel.processIntent(CallIntent.StartCall(callId, callee))
                        Analysis.track("start_call")
                    }
                }
            }

            is TelephoneCallerState.Connecting -> {
                if (isMatchCall) {
                    makeMatchCallHolder?.buttonEnable(false)
                    makeMatchCallHolder?.changeCallStateContent(com.amigo.uibase.R.string.str_connecting)
                } else {
                    makeCallHolder?.buttonEnable(false)
                    makeCallHolder?.changeCallStateContent(com.amigo.uibase.R.string.str_connecting)
                }
            }

            is TelephoneCallerState.StartLoadVideo -> {
                videoPlayEndTime = state.videoEndTime
                prepareFakeVideo(state.videoUrl)
            }


            is TelephoneCallerState.FinishCommunication -> {
                callFinish(
                    reason = state.reason,
                    "${callerInfo?.callee}",
                    "${callerInfo?.callId}",
                    0, TelephoneService.stateIsCalling()
                )
            }

            is TelephoneCallerState.FinishCall -> {
                insertFinishCallRecord(
                    getString(com.amigo.uibase.R.string.str_call_end),
                    callingHolder?.calcDuration?.toLong() ?: 0
                )
                callFinish(
                    reason = state.reason,
                    "${callerInfo?.callee}",
                    state.channel,
                    callingHolder?.calcDuration ?: 0, true
                )
            }

            is TelephoneCallerState.FinishCallException -> {
                callFinish(
                    reason = state.reason,
                    "${callerInfo?.callee}",
                    "${callerInfo?.callId}",
                    0, false
                )
            }
        }
    }

    inner class MakeCallMatchHolder(view: View) {
        val binding: LayoutMakeCallMatchBinding = LayoutMakeCallMatchBinding.bind(view)

        init {
            val caller = userDataStore.getUid()
            TelephoneService.processIntent(
                TelephoneIntent.MakeMatchCall(caller, callerInfo!!.callee, callerInfo!!.source)
            )
            binding.ivAvatar.loadImage(userDataStore.readAvatar(), isCircle = true)
            buttonEnable(true)
            binding.btnDecline.setThrottleListener {
                buttonEnable(false)
                TelephoneService.processIntent(TelephoneIntent.CancelCall(callerInfo?.callId))
            }
            ReportBehavior.reportEvent("click_call", mutableMapOf<String, Any>().apply {
                put("anchor_id", "${callerInfo?.callee}")
                put("source", "${callerInfo?.source}")
            })
        }

        fun changeCallStateContent(@StringRes strings: Int) {
            runOnUiThread {
                binding.tvCallState.text = getString(strings)
            }
        }

        fun buttonEnable(enable: Boolean) {
            runOnUiThread {
                if (enable) {
                    binding.btnDecline.isEnabled = true
                    binding.btnDecline.alpha = 1f
                } else {
                    binding.btnDecline.isEnabled = false
                    binding.btnDecline.alpha = 0.3f
                }
            }

        }

    }

    inner class MakeCallHolder(view: View) {
        val binding: LayoutMakeCallBinding = LayoutMakeCallBinding.bind(view)

        init {
            initView()
        }

        private fun initView() {
            if (isStrategyCall) {
                changeCallStateContent(com.amigo.uibase.R.string.str_waiting_for_response)
                buttonEnable(true)
            } else {
                changeCallStateContent(com.amigo.uibase.R.string.str_calling)
                buttonEnable(false)
                val caller = userDataStore.getUid()
                val callee = callerInfo!!.callee
                val source = callerInfo!!.source
                TelephoneService.processIntent(
                    TelephoneIntent.MakeCall(caller, callee, source, false)
                )
                ReportBehavior.reportEvent("click_call", mutableMapOf<String, Any>().apply {
                    put("anchor_id", "$callee")
                    put("source", source)
                })
            }

            binding.btnPickup.setThrottleListener {
                if (isStrategyCall) {
                    val caller = userDataStore.getUid()
                    val callee = callerInfo!!.callee
                    val source = callerInfo!!.source
                    TelephoneService.processIntent(
                        TelephoneIntent.MakeCall(
                            caller, callee, source, true
                        )
                    )
                    ReportBehavior.reportEvent("click_call", mutableMapOf<String, Any>().apply {
                        put("anchor_id", "$callee")
                        put("source", source)
                    })
                }
                buttonEnable(false)
            }

            binding.btnDecline.setThrottleListener {
                buttonEnable(false)
                TelephoneService.processIntent(TelephoneIntent.CancelCall(callerInfo?.callId))
            }
            viewModel.processIntent(CallIntent.GetAnchorInfo(callerInfo!!.callee))
        }

        fun buttonEnable(enable: Boolean) {
            runOnUiThread {
                if (enable) {
                    binding.btnPickup.isEnabled = true
                    binding.btnDecline.isEnabled = true
                    binding.btnPickup.alpha = 1f
                    binding.btnDecline.alpha = 1f
                } else {
                    binding.btnPickup.isEnabled = false
                    binding.btnDecline.isEnabled = false
                    binding.btnPickup.alpha = 0.3f
                    binding.btnDecline.alpha = 0.3f
                }
            }

        }

        fun bindUiInfo(userDetail: ChatUserInfo) {
            val roundRadius = RoundRadius(
                topLeft = 40f.dpToPx(this@CallActivity),
                topRight = 40f.dpToPx(this@CallActivity),
                bottomLeft = 20f.dpToPx(this@CallActivity),
                bottomRight = 20f.dpToPx(this@CallActivity),
            )
            val albums =
                userDetail.album?.filter { !it.isVideo }?.map { it.resUrl }?.toMutableList()
                    ?: mutableListOf()
            if (albums.isEmpty()) {
                albums.add(userDetail.avatar)
            }
            binding.bannerView.addBannerLifecycleObserver(this@CallActivity)
                .setAdapter(object : BannerImageAdapter<String>(albums) {
                    override fun onBindView(
                        holder: BannerImageHolder, data: String, position: Int, size: Int
                    ) {
                        holder.imageView.loadImage(data, roundRadius = roundRadius)
                    }
                }).start()
            binding.ivAvatar.loadImage(userDetail.avatar, isCircle = true)
            binding.tvRemoteName.text = userDetail.name
            binding.tvAge.text = "${userDetail.age}"
            binding.ivCountry.loadImage(
                "${userDetail.countryImg}",
                placeholderRes = com.amigo.uibase.R.drawable.img_placehoder_grey,
                errorRes = com.amigo.uibase.R.drawable.img_placehoder_grey
            )
            if (userDetail.gender == Gender.FEMALE.value) {
                binding.ivGender.setImageResource(R.drawable.ic_makecall_female)
                binding.tvAge.setTextColor(Color.parseColor("#F858D4"))
                binding.sllGender.shapeDrawableBuilder.setSolidColor(Color.parseColor("#FEEAED"))
                    .intoBackground()
            } else {
                binding.ivGender.setImageResource(R.drawable.ic_makecall_male)
                binding.tvAge.setTextColor(Color.parseColor("#000000"))
                binding.sllGender.shapeDrawableBuilder.setSolidColor(Color.parseColor("#E7F9FE"))
                    .intoBackground()
            }
        }

        fun changeCallStateContent(@StringRes strings: Int) {
            runOnUiThread {
                binding.tvCallState.text = getString(strings)
            }
        }

        fun handleErrorScenes(scenes: ErrorScenes) {
            runOnUiThread {
                TelephoneService.processIntent(TelephoneIntent.StopRing)
                lifecycleScope.launch {
                    delay(25 * 1000)
                    TelephoneService.processIntent(
                        TelephoneIntent.FinishCommunication("handleErrorScenes")
                    )
                }
                binding.srlCallError.visible()
                when (scenes) {

                    ErrorScenes.BUSY -> {
                        audioPlayer.play(R.raw.call_ring_busy)
                        binding.tvCallErrorTitle.text =
                            getString(com.amigo.uibase.R.string.str_call_buys)
                        binding.tvCallErrorContent.text =
                            Html.fromHtml(getString(com.amigo.uibase.R.string.str_user_busy_tip))
                        binding.ivCallError.setImageResource(R.drawable.ic_call_error_busy)
                    }

                    ErrorScenes.OFFLINE -> {
                        audioPlayer.play(R.raw.call_ring_offline)
                        binding.tvCallErrorTitle.text =
                            getString(com.amigo.uibase.R.string.str_call_offline)
                        binding.tvCallErrorContent.text =
                            Html.fromHtml(getString(com.amigo.uibase.R.string.str_user_offline_tip))
                        binding.ivCallError.setImageResource(R.drawable.ic_call_error_offline)
                    }

                    ErrorScenes.FAILURE -> {
                        audioPlayer.play(R.raw.call_ring_failed)
                        binding.tvCallErrorTitle.text =
                            getString(com.amigo.uibase.R.string.str_call_failure)
                        binding.tvCallErrorContent.text =
                            Html.fromHtml(getString(com.amigo.uibase.R.string.str_user_fail_tip))
                        binding.ivCallError.setImageResource(R.drawable.ic_call_error_failed)
                    }
                }
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    inner class CallingHolder(view: View, private val callId: String) {
        val binding: LayoutCallingBinding = LayoutCallingBinding.bind(view)

        val chatAdapter by lazy {
            VideoChatAdapter(viewBinding.root.context, lifecycleScope)
        }

        //通话时长
        var calcDuration: Int = 0

        //对方ID
        private var anchorId: Long = -1
        private var isFollow = false

        //设备功能信息
        private var cameraCloseInfo: DeviceFunctionInfo? = null
        private var cameraSwitchInfo: DeviceFunctionInfo? = null
        private var voiceMuteInfo: DeviceFunctionInfo? = null

        //通话显示的信息
        private var showAutoEndTime = false
        private var autoEndTime: Int = 0
        private var strategyMessageList: MutableList<String>? = null

        init {
            TelephoneService.processIntent(TelephoneIntent.StopRing)
            anchorId = callerInfo?.callee ?: -1
            if (anchorId != -1L) {
                viewModel.processIntent(CallIntent.RequestDeviceFunction)
                viewModel.processIntent(CallIntent.GetAnchorInfo(anchorId))
                viewModel.processIntent(CallIntent.CallInfo(callId))
                binding.rvChat.layoutManager = LinearLayoutManager(viewBinding.root.context)
                binding.rvChat.adapter = chatAdapter
                binding.ivCallClose.isEnabled = true
                binding.flCloseCamera.isSelected = statusDataStore.hasCloseCamera()
                binding.flMike.isSelected = statusDataStore.hasMuteVoice()

                binding.flRemoteView.setOnTouchListener { v, event ->
                    KeyboardUtil.hide(v.context, v)
                    false
                }
                binding.rvChat.setOnTouchListener { v, event ->
                    KeyboardUtil.hide(v.context, v)
                    false
                }
                binding.ivCallClose.setOnClickListener {
                    it.isEnabled = false
                    viewModel.processIntent(CallIntent.FinishCall(callId, "主动挂断"))
                }

                binding.etInput.addTextChangedListener(afterTextChanged = {
                    val content = it.toString()
                    if (content.isEmpty()) {
                        binding.ivSend.isEnabled = false
                        binding.ivSend.setImageResource(R.drawable.ic_call_send_disable)
                    } else {
                        binding.ivSend.isEnabled = true
                        binding.ivSend.setImageResource(R.drawable.ic_call_send_enable)
                    }
                })

                binding.ivSend.setOnClickListener {
                    val content = binding.etInput.text.toString()
                    binding.etInput.setText("")
                    chatAdapter.add(VideoMsg(content, false))
                    binding.rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
                }

                binding.flSwitchCamera.setOnClickListener {
                    changeCameraDirection()
                }

                binding.flMike.setOnClickListener {
                    changeVoiceStatus()
                }

                binding.flCloseCamera.setOnClickListener {
                    changeCameraStatus()
                }

                binding.ivFollow.setOnClickListener {
                    if (isFollow) {
                        viewModel.processIntent(CallIntent.UnLike(anchorId))
                    } else {
                        viewModel.processIntent(CallIntent.Like(anchorId))
                    }
                }

                binding.etInput.isFocusableInTouchMode = true
                binding.etInput.setOnClickListener { showMessageChatBox() }
            } else {
                viewModel.processIntent(CallIntent.FinishCall(callId, "主播信息异常"))
            }

        }

        /**
         * 切换摄像头方向
         */
        @SuppressLint("MissingPermission")
        private fun changeCameraDirection() {
            if (cameraSwitchInfo == null) return
            if (cameraSwitchInfo!!.enable) {
                //执行关闭摄像头或者开启摄像头
                val useFrontCamera = statusDataStore.hasUseFrontCamera()
                val nextUseFrontCamera = !useFrontCamera
                statusDataStore.saveUseFrontCamera(nextUseFrontCamera)
                val isCloseCamera = statusDataStore.hasCloseCamera()
                if (!isCloseCamera) {
                    binding.cpvLocal.launchCamera(this@CallActivity, nextUseFrontCamera)
                }
                val resId =
                    if (nextUseFrontCamera) com.amigo.uibase.R.string.str_front_camera_switched else com.amigo.uibase.R.string.str_rear_camera_switched
                Toaster.showShort(this@CallActivity, resId)
            } else {
                //显示付费
                showUnlockDeviceFunctionDialog(DeviceFunctionEnum.CAMERA_SWITCH, cameraSwitchInfo!!)
            }
        }

        @SuppressLint("MissingPermission")
        private fun changeCameraStatus() {
            if (cameraCloseInfo == null) return
            if (cameraCloseInfo!!.enable) {
                //执行关闭摄像头或者开启摄像头
                val isCloseCamera = statusDataStore.hasCloseCamera()
                val nextCloseCamera = !isCloseCamera
                statusDataStore.saveCloseCamera(nextCloseCamera)
                binding.flCloseCamera.isSelected = nextCloseCamera
                if (nextCloseCamera) {
                    binding.tvCameraOff.visibility = View.VISIBLE
                    binding.cpvLocal.release()
                    Toaster.showShort(
                        this@CallActivity, com.amigo.uibase.R.string.str_camera_is_turned_off
                    )
                } else {
                    //重新设置本地视图
                    requestMultiplePermission(android.Manifest.permission.CAMERA, onGranted = {
                        binding.tvCameraOff.visibility = View.INVISIBLE
                        val isUseFrontCamera = statusDataStore.hasUseFrontCamera()
                        binding.cpvLocal.launchCamera(this@CallActivity, isUseFrontCamera)
                        Toaster.showShort(
                            this@CallActivity, com.amigo.uibase.R.string.str_camera_is_turned_on
                        )
                    }, onDenied = {
                        Toaster.showShort(
                            this@CallActivity,
                            com.amigo.uibase.R.string.str_please_grant_permission
                        )
                    })

                }
            } else {
                //显示付费
                showUnlockDeviceFunctionDialog(DeviceFunctionEnum.CAMERA_CLOSE, cameraCloseInfo!!)
            }
        }

        private fun changeVoiceStatus() {
            if (voiceMuteInfo == null) return
            if (voiceMuteInfo!!.enable) {
                val isMuteVoice = statusDataStore.hasMuteVoice()
                val nextMuteVoice = !isMuteVoice
                statusDataStore.saveMuteVoice(nextMuteVoice)
                binding.flMike.isSelected = nextMuteVoice
                val resId =
                    if (nextMuteVoice) com.amigo.uibase.R.string.str_micrphone_is_off else com.amigo.uibase.R.string.str_micrphone_is_on
                Toaster.showShort(this@CallActivity, resId)
            } else {
                //显示付费
                showUnlockDeviceFunctionDialog(DeviceFunctionEnum.VOICE_MUTE, voiceMuteInfo!!)
            }
        }

        private fun showUnlockDeviceFunctionDialog(
            enum: DeviceFunctionEnum, deviceFunctionInfo: DeviceFunctionInfo
        ) {

            val dialog = UnLockDeviceFunctionDialog()
            dialog.setData(enum, deviceFunctionInfo)
            dialog.setClickCoinListener {
                viewModel.processIntent(CallIntent.UnlockDeviceFunction(it))
            }

            dialog.setClickVipListener {
                val iStoreService = RouteSdk.findService(IStoreService::class.java)
                iStoreService.showCodeDialog("20200", null)
            }
            dialog.showDialog(this@CallActivity, null)

        }

        /**
         * 绑定设备功能的状态
         */
        fun bindDeviceInfo(
            cameraCloseInfo: DeviceFunctionInfo?,
            cameraSwitchInfo: DeviceFunctionInfo?,
            voiceMuteInfo: DeviceFunctionInfo?
        ) {
            this.cameraCloseInfo = cameraCloseInfo
            this.cameraSwitchInfo = cameraSwitchInfo
            this.voiceMuteInfo = voiceMuteInfo
        }

        /**
         * 处理设备解锁的状态
         */
        fun handleUnlockDeviceFunctionResult(result: Boolean, unlockItem: DeviceFunctionInfo) {
            val resId =
                if (result) com.amigo.uibase.R.string.str_unlock_success else com.amigo.uibase.R.string.str_unlock_failure
            Toaster.showShort(this@CallActivity, resId)
            if (result) {
                if (cameraCloseInfo?.id == unlockItem.id) {
                    cameraCloseInfo = cameraCloseInfo?.copy(enable = true)
                    changeCameraStatus()
                    return
                }
                if (cameraSwitchInfo?.id == unlockItem.id) {
                    cameraSwitchInfo = cameraSwitchInfo?.copy(enable = true)
                    changeCameraDirection()
                    return
                }
                if (voiceMuteInfo?.id == unlockItem.id) {
                    voiceMuteInfo = voiceMuteInfo?.copy(enable = true)
                    changeVoiceStatus()
                    return
                }
            }
        }


        fun bindFollowState(isFollow: Boolean) {
            this.isFollow = isFollow
            if (isFollow) {
                binding.ivFollow.setImageResource(R.drawable.ic_call_liked)
            } else {
                binding.ivFollow.setImageResource(R.drawable.ic_call_unlike)
            }
        }

        /**
         * 设置本地视图
         */
        @SuppressLint("MissingPermission")
        fun setupLocalView() {
            val isCloseCamera = statusDataStore.hasCloseCamera()
            if (isCloseCamera) {
                binding.cpvLocal.release()
                binding.tvCameraOff.visibility = View.VISIBLE
            } else {
                binding.tvCameraOff.visibility = View.INVISIBLE
                val uid = userDataStore.getUid().toInt()
                val isUseFrontCamera = statusDataStore.hasUseFrontCamera()
                binding.cpvLocal.launchCamera(this@CallActivity, isUseFrontCamera)
            }
        }

        fun setupLocalVoice() {
        }

        /**
         * 设置远端视图
         */
        @OptIn(UnstableApi::class)
        fun setupRemoteView(uid: Int) {
            // todo 设置远端视图，其实是播放视频
            val playerView = PlayerView(this@CallActivity)
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            playerView.useController = false
            playerView.controllerAutoShow = false
            binding.flRemoteView.addView(
                playerView, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )
            )
            videoPlayer.play(playerView)
        }


        /**
         * 更新通话时间
         */
        fun updateDuration(duration: Int) {
            calcDuration = duration
            binding.callTimeTv.text = TimeUtil.formatSeconds(duration)
            val first =
                if (!strategyMessageList.isNullOrEmpty()) strategyMessageList?.get(0) else null
            if (duration % 10 == 0 && first != null) {
                //取一条消息展示在页面上
                chatAdapter.add(VideoMsg(first, true))
                binding.rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
                strategyMessageList?.remove(first)

            }
            val leftDuration = autoEndTime - duration
            if (showAutoEndTime && leftDuration >= 0) {
                //展示倒计时
                binding.tvCountDown.text = "$leftDuration"
                if (leftDuration <= 0) {
                    binding.sllCountDown.invisible()
                }
            }
        }

        fun updateCallInfo(callInfoResponse: CallInfoResponse) {
            showAutoEndTime = callInfoResponse.showAutoEndTime
            autoEndTime = callInfoResponse.autoEndTime ?: 0
            strategyMessageList = callInfoResponse.strategyMessageList
            if (showAutoEndTime && autoEndTime > 0) {
                binding.sllCountDown.visible()
                binding.callTimeTv.invisible()
            } else {
                binding.sllCountDown.invisible()
                binding.callTimeTv.visible()
            }
        }


        /**
         * 隐藏聊天框和软键盘
         */
        fun hideMessageChatBox() {
            KeyboardUtil.hide(this@CallActivity, binding.etInput)
        }

        /**
         * 显示聊天框和软键盘
         */
        fun showMessageChatBox() {
            runOnUiThread {
                binding.etInput.requestFocus()
                KeyboardUtil.show(this@CallActivity, binding.etInput)
            }
        }
    }


    private data class Caller(val callee: Long, val source: String, val callId: String? = null)

    private fun insertFinishCallRecord(text: String, duration: Long) {
        val callRecordMessage = CallRecordMessage()
        callRecordMessage.message_content = text
        callRecordMessage.duration = duration
        val senderId = if (isStrategyCall) "${callerInfo?.callee}" else "${userDataStore.getUid()}"
        val peerId = if (isStrategyCall) "${userDataStore.getUid()}" else "${callerInfo?.callee}"
        if (senderId != peerId) {
            runBlocking {
                IMCore.getService(MessageService::class.java)
                    .insertLocalMessage(senderId, peerId, callRecordMessage, true)
            }
        }
    }

    private fun callFinish(
        reason: String,
        anchorId: String?,
        channel: String?,
        duration: Int,
        isCalling: Boolean
    ) {
        if (isCalling) {
            ReportBehavior.reportEvent("finish_call", mutableMapOf<String, Any>().apply {
                put("anchor_id", "$anchorId")
                put("call_channel_id", "$channel")
                put("call_type_new", "video")
                put("call_duration", duration)
                put("source", "$source")
                put("reason", reason)
            })
        }

        finish()
    }

}