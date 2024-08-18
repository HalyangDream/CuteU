package com.amigo.chat

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.luck.picture.lib.config.PictureMimeType
import com.amigo.analysis.Analysis
import com.amigo.baselogic.userDataStore
import com.amigo.basic.BaseModelActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.chat.adapter.ChatAdapter
import com.amigo.chat.adapter.ChatHeaderAdapter
import com.amigo.chat.databinding.ActivityChatBinding
import com.amigo.chat.databinding.LayoutChatTopBinding
import com.amigo.uibase.dialog.MoreDialog
import com.amigo.uibase.dialog.ReportDialog
import com.amigo.chat.intent.ChatIntent
import com.amigo.chat.listener.IChattingAction
import com.amigo.chat.state.ChatState
import com.amigo.chat.viewmodel.ChatViewModel
import com.amigo.im.IMCore
import com.amigo.im.bean.Msg
import com.amigo.im.listener.IMMessageListener
import com.amigo.im.service.ConversationService
import com.amigo.im.service.MsgServiceObserver
import com.amigo.picture.LocalAlbumManager
import com.amigo.picture.PictureMimeEnum
import com.amigo.picture.loadDrawable
import com.amigo.tool.EventBus
import com.amigo.tool.EventBus.subscribe
import com.amigo.tool.KeyboardUtil
import com.amigo.tool.Toaster
import com.amigo.uibase.ad.AdPlayService
import com.amigo.uibase.event.RemoteNotifyEvent
import com.amigo.uibase.gone
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.IStoreService
import com.amigo.uibase.route.provider.ITelephoneService
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.visible
import kotlinx.coroutines.launch
import java.io.File
import kotlin.properties.Delegates

@Route(path = RoutePage.CHAT.CHAT_ACTIVITY)
class ChatActivity : BaseModelActivity<ActivityChatBinding, ChatViewModel>(), IMMessageListener,
    IChattingAction {


    private lateinit var titleBarBinding: LayoutChatTopBinding
    private var mPeerId by Delegates.notNull<Long>()
    private var mSource: String = ""
    private var mUid by Delegates.notNull<Long>()
    private var isBlock: Boolean = false
    private lateinit var chatHeaderAdapter: ChatHeaderAdapter
    private lateinit var chatAdapter: ChatAdapter
    private val telephoneService = RouteSdk.findService(ITelephoneService::class.java)

    override fun initViewBinding(layout: LayoutInflater): ActivityChatBinding {
        return ActivityChatBinding.inflate(layout)
    }

    override fun initView() {
        mUid = this.userDataStore.getUid()
        mPeerId = intent.getLongExtra("peerId", 0)
        mSource = intent.getStringExtra("source")!!
        titleBarBinding = LayoutChatTopBinding.bind(viewBinding.root)
        StatusUtils.setStatusBarColor(Color.WHITE, this.window)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdPlayService.reportPlayAdScenes("chat_page")
                finish()
            }
        })
        titleBarBinding.ivNavBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        titleBarBinding.ivNavMore.setOnClickListener {
            showMoreDialog()
        }
        checkIsOfficial(isOfficial = {
            titleBarBinding.ivNavMore.gone()
            viewBinding.ivCall.gone()
        })
        viewBinding.chatInputView.setActivity(this)
        viewBinding.ivCall.loadDrawable(R.drawable.ic_chat_call)
        viewBinding.ivCall.setOnClickListener {
            telephoneService.sendCallInvited(this, mUid, mPeerId, "msg_page")
            UserBehavior.setChargeSource("msg_page_call")
        }
        viewBinding.srlLayout.setOnRefreshListener {
            if (chatAdapter.items.isEmpty()) {
                viewModel.processIntent(ChatIntent.MessageList("$mUid", "$mPeerId", false))
            } else {
                val anchor = chatAdapter.items.first()
                viewModel.processIntent(ChatIntent.MessageListForAnchor(anchor))
            }
        }
        viewBinding.srlLayout.apply {
            setEnableLoadMore(false)
            setEnableRefresh(true)
        }
        viewBinding.rvMessage.apply {
            chatHeaderAdapter = ChatHeaderAdapter(this@ChatActivity)
            chatAdapter = ChatAdapter(this@ChatActivity)
            chatAdapter.setCoroutineScope(lifecycleScope)
            val contactAdapter = ConcatAdapter(chatHeaderAdapter, chatAdapter)
            adapter = contactAdapter
        }
        viewBinding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            viewBinding.root.getWindowVisibleDisplayFrame(r)

            val screenHeight = viewBinding.root.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > 0) { // 键盘可能被打开
                scrollToBottom()
            }
        }
        viewBinding.rlUnlockVip.setOnClickListener {
            RouteSdk.findService(IStoreService::class.java).showCodeDialog("20200", null)
            //todo 显示VIP弹窗
            UserBehavior.setChargeSource("unlock_chat")
        }

        viewBinding.rvMessage.setOnTouchListener(View.OnTouchListener { v, event ->
            KeyboardUtil.hide(v.context, v)
            false
        })
        viewModel.observerState {
            bindChatState(it)
        }
        EventBus.event.subscribe<RemoteNotifyEvent>(lifecycleScope) {
            if (it is RemoteNotifyEvent.PaySuccessEvent) {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    viewModel.processIntent(ChatIntent.GetAnchorInfo(mPeerId, false))
                }
            }
        }
        IMCore.getService(MsgServiceObserver::class.java).observerReceiveMessage(this, true)
        viewModel.processIntent(ChatIntent.GetAnchorInfo(mPeerId, true))
    }

    private fun checkIsOfficial(
        isOfficial: (() -> Unit)? = null, isNotOfficial: (() -> Unit)? = null
    ) {
        val officialAccount = userDataStore.getOfficialAccount()
        if (!TextUtils.isEmpty(officialAccount) && officialAccount == "$mPeerId") {
            isOfficial?.invoke()
        } else {
            isNotOfficial?.invoke()
        }
    }

    private fun bindChatState(state: ChatState) {
        when (state) {
            is ChatState.MessageListResult -> {
                viewBinding.srlLayout.finishRefresh()
                if (!state.data.isNullOrEmpty()) {
                    chatAdapter.addAll(0, state.data)
                    if (state.isFirstLoad) {
                        scrollToBottom()
                    }
                }
            }

            is ChatState.AnchorInfo -> updateAnchorInfo(state)


            is ChatState.BlockUserResult -> {
                if (state.result) {
                    Toaster.showShort(
                        this, getString(com.amigo.uibase.R.string.str_block_success)
                    )
                    finish()
                } else {
                    Toaster.showShort(
                        this, getString(com.amigo.uibase.R.string.str_block_failed)
                    )
                }
            }

            is ChatState.UnBlockUserResult -> {
                if (state.result) {
                    isBlock = false
                    Toaster.showShort(
                        this, getString(com.amigo.uibase.R.string.str_unblock_success)
                    )
                } else {
                    Toaster.showShort(
                        this, getString(com.amigo.uibase.R.string.str_unblock_failed)
                    )
                }
            }

            is ChatState.ReportUserResult -> {
                if (state.result) {
                    Toaster.showShort(
                        this, getString(com.amigo.uibase.R.string.str_report_success)
                    )
                } else {
                    Toaster.showShort(
                        this, getString(com.amigo.uibase.R.string.str_report_failed)
                    )
                }
            }

        }
    }


    private fun showMoreDialog() {
        val moreDialog = MoreDialog()
        moreDialog.setMoreClickAction(addBlackAction = {
            if (isBlock) {
                viewModel.processIntent(ChatIntent.UnBlockUser(mPeerId))
            } else {
                viewModel.processIntent(ChatIntent.BlockUser(mPeerId))
            }

        }, deleteAction = {
            deleteConversation("$mPeerId")
        }, reportAction = {
            showReportDialog()
        })
        moreDialog.showDialog(this, Bundle().apply {
            putBoolean("isBlock", isBlock)
        })
    }

    private fun deleteConversation(mPeerId: String) {
        lifecycleScope.launch {
            IMCore.getService(ConversationService::class.java).deleteConversation("$mUid", mPeerId)
            Toaster.showShort(this@ChatActivity, "Delete Success")
            finish()
        }

    }

    private fun showReportDialog() {
        val reportDialog = ReportDialog()
        reportDialog.setReportListener {
            viewModel.processIntent(ChatIntent.ReportUser(mPeerId, it.content))
        }
        reportDialog.showDialog(this, null)
    }

    private fun scrollToBottom() {
        viewBinding.rvMessage.post {
            try {
                //不减一个的原因是有一个headerAdapter
                val adapter = viewBinding.rvMessage.adapter as ConcatAdapter
                val lastIndex = adapter.itemCount
                viewBinding.rvMessage.scrollToPosition(lastIndex - 1)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun updateAnchorInfo(
        it: ChatState.AnchorInfo,
    ) {
        if (it.isFirst) {
            viewModel.processIntent(ChatIntent.MessageList("$mUid", "$mPeerId", true))
        }
        val anchorInfo = it.anchorInfo ?: return
        isBlock = anchorInfo.isBlock
        if (chatHeaderAdapter.itemCount > 0) {
            chatHeaderAdapter.removeAt(0)
        }
        chatHeaderAdapter.add(anchorInfo)
        titleBarBinding.tvName.text = anchorInfo.name
        val cityAndCountry = if (anchorInfo.city.isNotEmpty() && anchorInfo.country.isNotEmpty()) {
            "${anchorInfo.city},${anchorInfo.country}"
        } else {
            "${anchorInfo.city}${anchorInfo.country}"
        }
        titleBarBinding.tvCity.text = cityAndCountry
        if (!userDataStore.readVip()) {
            checkIsOfficial(isOfficial = {
                viewBinding.rlUnlockVip.gone()
            }, isNotOfficial = {
                viewBinding.rlUnlockVip.visible()
                KeyboardUtil.hide(this, viewBinding.rlUnlockVip)
            })
        } else {
            viewBinding.rlUnlockVip.gone()
        }
    }

    private fun clearUnReadCount() {
        lifecycleScope.launch {
            IMCore.getService(ConversationService::class.java)
                .clearUnReadCountByPeer("$mUid", "$mPeerId")
        }
    }


    override fun onReceiveMsg(message: Msg) {
        runOnUiThread {
            val channel =
                IMCore.getService(ConversationService::class.java).getChannelId("$mUid", "$mPeerId")
            if (channel != message.channel) return@runOnUiThread
            chatAdapter.receiveMsg(message)
            scrollToBottom()
        }
    }

    override fun onResume() {
        super.onResume()
        IMCore.getService(ConversationService::class.java).setChatUser("$mPeerId")
    }

    override fun onPause() {
        super.onPause()
        IMCore.getService(ConversationService::class.java).removeChatUser("$mPeerId")
        clearUnReadCount()
    }

    override fun onDestroy() {
        super.onDestroy()
        IMCore.getService(MsgServiceObserver::class.java).observerReceiveMessage(this, false)
    }


    override fun onSendTextMessage(message: String) {
        viewModel.processIntent(ChatIntent.SendTextMessage(mUid, mPeerId, message))
        UserBehavior.setChargeSource("seng_msg")
    }

    override fun onOpenAlbum() {
        LocalAlbumManager.Builder().setActivity(this).setMaxSelectNum(1).setEnableCamera(true)
            .setEnableCompress(false).setMimeType(PictureMimeEnum.ALL).openGallery()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val list = LocalAlbumManager.paresData(requestCode, resultCode, data)
        if (!list.isNullOrEmpty()) {
            val localMedia = list[0]

            val isVideo = PictureMimeType.isHasVideo(localMedia.mimeType)
            if (isVideo) {
                // 发送视频
                val videoFile = File(localMedia.realPath)
                viewModel.processIntent(ChatIntent.SendVideoMessage(mUid, mPeerId, videoFile))
                UserBehavior.setChargeSource("seng_msg")

            } else {
                // 发送图片
                val imgPath = localMedia.realPath
                val imgFile = File(imgPath)
                viewModel.processIntent(ChatIntent.SendImageMessage(mUid, mPeerId, imgFile))
                UserBehavior.setChargeSource("seng_msg")
            }
        }
    }
}