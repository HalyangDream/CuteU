package com.amigo.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.amigo.analysis.Analysis
import com.amigo.baselogic.storage.UserDataStore
import com.amigo.basic.BaseModelActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.home.adapter.AnchorAlbumAdapter
import com.amigo.home.adapter.AnchorInfoAdapter
import com.amigo.home.databinding.ActivityAnchorDetailBinding
import com.amigo.home.databinding.LayoutAnchorTitleBinding
import com.amigo.home.intent.AnchorDetailIntent
import com.amigo.home.state.AnchorDetailState
import com.amigo.home.viewmodel.AnchorDetailViewModel
import com.amigo.logic.http.response.user.UserDetail
import com.amigo.picture.loadImage
import com.amigo.tool.EventBus
import com.amigo.tool.EventBus.subscribe
import com.amigo.tool.Toaster
import com.amigo.uibase.ReportBehavior
import com.amigo.uibase.ad.AdPlayService
import com.amigo.home.adapter.UserTagAdapter
import com.amigo.uibase.dialog.MoreDialog
import com.amigo.uibase.dialog.ReportDialog
import com.amigo.uibase.event.RemoteNotifyEvent
import com.amigo.uibase.gone
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.ITelephoneService
import com.amigo.uibase.setOnlineLabelImage
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.visible
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlin.properties.Delegates

@Route(path = RoutePage.HOME.ANCHOR_DETAIL_ACTIVITY)
class AnchorDetailActivity :
    BaseModelActivity<ActivityAnchorDetailBinding, AnchorDetailViewModel>() {

    private lateinit var titleBarBinding: LayoutAnchorTitleBinding

    private var albumAdapter: AnchorAlbumAdapter? = null
    private var anchorInfoAdapter: AnchorInfoAdapter? = null
    private var userTagAdapter: UserTagAdapter? = null
    private var anchorId by Delegates.notNull<Long>()
    private var source: String = ""
    private var isFollow = false
    private var isBlock = false


    override fun initViewBinding(layout: LayoutInflater): ActivityAnchorDetailBinding {
        return ActivityAnchorDetailBinding.inflate(layout)
    }

    override fun initView() {
        anchorId = intent.getLongExtra("anchorId", 0)
        source = intent.getStringExtra("source") ?: "details"
        UserBehavior.setRootPage(source)
        ReportBehavior.reportEvent("anchor_profile_page", mutableMapOf<String, Any>().apply {
            put("anchor_id", "$anchorId")
            put("source", source)
        })
        Analysis.track("anchor_profile_page")
        titleBarBinding = LayoutAnchorTitleBinding.bind(viewBinding.root)
        StatusUtils.setImmerseLayout(titleBarBinding.flTitle, this)
        initTitleBar()
        initRv()
        viewBinding.ivMsg.setOnClickListener {
            RouteSdk.navigationChat(anchorId, "anchor_profile")
        }
        viewBinding.ivCall.setOnClickListener {
            RouteSdk.findService(ITelephoneService::class.java).sendCallInvited(
                this, UserDataStore.get(this).getUid(), anchorId, "anchor_profile"
            )
            UserBehavior.setChargeSource("anchor_profile_call")
        }

        viewBinding.ivFollow.setOnClickListener {
            if (isFollow) {
                viewModel.processIntent(AnchorDetailIntent.UnFollow(anchorId))
            } else {
                viewModel.processIntent(AnchorDetailIntent.Follow(anchorId))
            }

        }
        EventBus.event.subscribe<RemoteNotifyEvent>(lifecycleScope) {
            if (it is RemoteNotifyEvent.PaySuccessEvent) {
                viewModel.processIntent(AnchorDetailIntent.GetAnchorInfo(anchorId))
            }
        }
        viewModel.observerState {
            when (it) {
                is AnchorDetailState.AnchorInfo -> {
                    bindAnchorInfo(it.anchorInfo)
                }

                is AnchorDetailState.FollowState -> {
                    this.isFollow = it.isFollow
                    if (it.isFollow) {
                        viewBinding.ivFollow.setImageResource(R.drawable.ic_feed_liked)
                        Toaster.showShort(this, com.amigo.uibase.R.string.str_liked)

                    } else {
                        viewBinding.ivFollow.setImageResource(R.drawable.ic_feed_unliked)
                        Toaster.showShort(this, com.amigo.uibase.R.string.str_unliked)
                    }
                }

                is AnchorDetailState.BlockUserResult -> {
                    if (it.result) {
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

                is AnchorDetailState.UnBlockUserResult -> {
                    if (it.result) {
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

                is AnchorDetailState.ReportUserResult -> {
                    if (it.result) {
                        Toaster.showShort(this, "Report success")
                    } else {
                        Toaster.showShort(
                            this, getString(com.amigo.uibase.R.string.str_report_failed)
                        )
                    }
                }
            }
        }

        viewModel.processIntent(AnchorDetailIntent.GetAnchorInfo(anchorId))
    }


    @SuppressLint("SetTextI18n")
    private fun bindAnchorInfo(anchorInfo: UserDetail?) {
        if (anchorInfo == null) return
        isBlock = anchorInfo.isBlock
        isFollow = anchorInfo.isFollow
        viewBinding.ivAvatar.loadImage(
            anchorInfo.avatar, placeholderRes = com.amigo.uibase.R.drawable.img_placehoder_grey
        )
        viewBinding.tvId.text = "@${anchorInfo.id}"
        viewBinding.tvName.text = "${anchorInfo.name},${anchorInfo.age}"
        viewBinding.ivOnline.setOnlineLabelImage(anchorInfo.online)
        viewBinding.tvSign.text = anchorInfo.sign

        if (anchorInfo.sign.isEmpty()) {
            viewBinding.tvSign.gone()
        } else {
            viewBinding.tvSign.visible()
        }

        if (anchorInfo.callPrice.isNullOrEmpty()) {
            viewBinding.tvCallPrice.gone()
        } else {
            viewBinding.tvCallPrice.visible()
            viewBinding.tvCallPrice.text = "${anchorInfo.callPrice}"
        }

        if (anchorInfo.isFollow) {
            viewBinding.ivFollow.setImageResource(R.drawable.ic_feed_liked)

        } else {
            viewBinding.ivFollow.setImageResource(R.drawable.ic_feed_unliked)
        }
        anchorInfoAdapter?.setData(anchorInfo.info)
        userTagAdapter?.setData(anchorInfo.tag)
        albumAdapter?.setData(anchorInfo.album)
    }

    private fun initRv() {
//        viewBinding.rvInfo.apply {
//            layoutManager =
//                FlexboxLayoutManager(this@AnchorDetailActivity, FlexDirection.ROW, FlexWrap.WRAP)
//            anchorInfoAdapter = AnchorInfoAdapter(this@AnchorDetailActivity)
//            adapter = anchorInfoAdapter
//        }
        viewBinding.rvTag.apply {
            userTagAdapter = UserTagAdapter(this@AnchorDetailActivity)
            layoutManager =
                FlexboxLayoutManager(this@AnchorDetailActivity, FlexDirection.ROW, FlexWrap.WRAP)
            adapter = userTagAdapter
        }

        viewBinding.rvAlbum.apply {
            albumAdapter = AnchorAlbumAdapter(this@AnchorDetailActivity)
            layoutManager = LinearLayoutManager(this@AnchorDetailActivity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = albumAdapter
        }
    }


    private fun initTitleBar() {
        titleBarBinding.rlTitle.setBackgroundColor(
            ContextCompat.getColor(
                this, android.R.color.transparent
            )
        )
        titleBarBinding.ivNavBack.setImageResource(com.amigo.uibase.R.drawable.ic_nav_back_black)
        titleBarBinding.ivNavMore.setImageResource(com.amigo.uibase.R.drawable.ic_nav_more_black)
        titleBarBinding.ivNavMore.setOnClickListener {
            showMoreDialog()
        }
        titleBarBinding.ivNavBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdPlayService.reportPlayAdScenes("profile_page")
                finish()
            }
        })
    }

    private fun showMoreDialog() {
        val moreDialog = MoreDialog()
        moreDialog.hideDelete()
        moreDialog.setMoreClickAction(addBlackAction = {
            if (isBlock) {
                viewModel.processIntent(AnchorDetailIntent.UnBlockUser(anchorId))
            } else {
                viewModel.processIntent(AnchorDetailIntent.BlockUser(anchorId))
            }
        }, deleteAction = {}, reportAction = {
            showReportDialog()
        })
        moreDialog.showDialog(this, Bundle().apply {
            putBoolean("isBlock", isBlock)
        })
    }

    private fun showReportDialog() {
        val reportDialog = ReportDialog()
        reportDialog.setReportListener {
            viewModel.processIntent(
                AnchorDetailIntent.ReportUser(
                    anchorId, it.content
                )
            )
        }
        reportDialog.showDialog(this, null)
    }

}