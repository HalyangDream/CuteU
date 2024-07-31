package com.cute.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.cute.baselogic.storage.UserDataStore
import com.cute.basic.BaseModelActivity
import com.cute.basic.util.StatusUtils
import com.cute.home.adapter.AnchorAlbumAdapter
import com.cute.home.adapter.AnchorInfoAdapter
import com.cute.home.databinding.ActivityAnchorDetailBinding
import com.cute.home.databinding.LayoutAnchorTitleBinding
import com.cute.home.intent.AnchorDetailIntent
import com.cute.home.state.AnchorDetailState
import com.cute.home.viewmodel.AnchorDetailViewModel
import com.cute.logic.http.response.user.UserDetail
import com.cute.picture.loadImage
import com.cute.tool.EventBus
import com.cute.tool.EventBus.subscribe
import com.cute.tool.Toaster
import com.cute.uibase.ReportBehavior
import com.cute.uibase.ad.AdPlayService
import com.cute.home.adapter.UserTagAdapter
import com.cute.uibase.dialog.MoreDialog
import com.cute.uibase.dialog.ReportDialog
import com.cute.uibase.event.RemoteNotifyEvent
import com.cute.uibase.gone
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.ITelephoneService
import com.cute.uibase.setOnlineLabelImage
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible
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
        source = intent.getStringExtra("source") ?: ""
        ReportBehavior.reportEvent("anchor_profile_page", mutableMapOf<String, Any>().apply {
            put("anchor_id", "$anchorId")
            put("source", source)
        })
        titleBarBinding = LayoutAnchorTitleBinding.bind(viewBinding.root)
        StatusUtils.setImmerseLayout(titleBarBinding.flTitle, this)
        initTitleBar()
        initRv()
        viewBinding.ivMsg.setOnClickListener {
            RouteSdk.navigationChat(anchorId, "anchor_profile")
        }
        viewBinding.rlCall.setOnClickListener {
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
                    this.isFollow = isFollow
                    if (it.isFollow) {
                        viewBinding.ivFollow.setImageResource(R.drawable.ic_feed_liked)
                        Toaster.showShort(this, com.cute.uibase.R.string.str_liked)

                    } else {
                        viewBinding.ivFollow.setImageResource(R.drawable.ic_feed_unliked)
                        Toaster.showShort(this, com.cute.uibase.R.string.str_unliked)
                    }
                }

                is AnchorDetailState.BlockUserResult -> {
                    if (it.result) {
                        Toaster.showShort(
                            this, getString(com.cute.uibase.R.string.str_block_success)
                        )
                        finish()
                    } else {
                        Toaster.showShort(
                            this, getString(com.cute.uibase.R.string.str_block_failed)
                        )
                    }
                }

                is AnchorDetailState.UnBlockUserResult -> {
                    if (it.result) {
                        isBlock = false
                        Toaster.showShort(
                            this, getString(com.cute.uibase.R.string.str_unblock_success)
                        )
                    } else {
                        Toaster.showShort(
                            this, getString(com.cute.uibase.R.string.str_unblock_failed)
                        )
                    }
                }

                is AnchorDetailState.ReportUserResult -> {
                    if (it.result) {
                        Toaster.showShort(this, "Report success")
                    } else {
                        Toaster.showShort(
                            this, getString(com.cute.uibase.R.string.str_report_failed)
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
            anchorInfo.avatar,
            isCircle = true,
            placeholderRes = com.cute.uibase.R.drawable.img_placehoder_round_grey
        )
        viewBinding.tvId.text = "@${anchorInfo.id}"
        viewBinding.tvName.text = "${anchorInfo.name},${anchorInfo.age}"
        titleBarBinding.ivOnline.setOnlineLabelImage(anchorInfo.online)
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
        viewBinding.rvInfo.apply {
            layoutManager =
                FlexboxLayoutManager(this@AnchorDetailActivity, FlexDirection.ROW, FlexWrap.WRAP)
            anchorInfoAdapter = AnchorInfoAdapter(this@AnchorDetailActivity)
            adapter = anchorInfoAdapter
        }
        viewBinding.rvTag.apply {
            userTagAdapter = UserTagAdapter(this@AnchorDetailActivity)
            val linearLayoutManager = LinearLayoutManager(this@AnchorDetailActivity)
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            layoutManager = linearLayoutManager
            adapter = userTagAdapter
        }

        viewBinding.rvAlbum.apply {
            albumAdapter = AnchorAlbumAdapter(this@AnchorDetailActivity)
            layoutManager = GridLayoutManager(this@AnchorDetailActivity,3)
            adapter = albumAdapter
        }
    }


    private fun initTitleBar() {
        titleBarBinding.rlTitle.setBackgroundColor(
            ContextCompat.getColor(
                this, android.R.color.transparent
            )
        )
        titleBarBinding.ivNavBack.setImageResource(com.cute.uibase.R.drawable.ic_nav_back_black)
        titleBarBinding.ivNavMore.setImageResource(com.cute.uibase.R.drawable.ic_nav_more_black)
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