package com.cute.store

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.cute.analysis.Analysis
import com.cute.basic.BaseModelActivity
import com.cute.basic.util.StatusUtils
import com.cute.logic.http.response.product.VipPowerInfoData
import com.cute.store.adapter.VipStoreAdapter
import com.cute.store.adapter.VipStoreBannerAdapter
import com.cute.store.adapter.VipStoreBannerPowerAdapter
import com.cute.store.databinding.ActivityVipStoreBinding
import com.cute.store.intent.VipStoreIntent
import com.cute.store.state.VipStoreState
import com.cute.tool.Toaster
import com.cute.uibase.Constant
import com.cute.uibase.ReportBehavior
import com.cute.uibase.WebViewActivity
import com.cute.uibase.ad.AdPlayService
import com.cute.uibase.route.RoutePage
import com.cute.uibase.setThrottleListener
import com.cute.uibase.userbehavior.UserBehavior


@Route(path = RoutePage.STORE.VIP_STORE)
class VipStoreActivity : BaseModelActivity<ActivityVipStoreBinding, VipStoreViewModel>() {

    private lateinit var vipAdapter: VipStoreAdapter
    private var isFromCode = false
    override fun initViewBinding(layout: LayoutInflater): ActivityVipStoreBinding {

        return ActivityVipStoreBinding.inflate(layout)
    }

    override fun initView() {
        isFromCode = intent.getBooleanExtra("isFromCode", false)
        StatusUtils.setImmerseLayout(viewBinding.circleIndicator, this)
        viewBinding.rvVipProduct.apply {
            val manager = LinearLayoutManager(context)
            manager.orientation = LinearLayoutManager.HORIZONTAL
            layoutManager = manager
            vipAdapter = VipStoreAdapter(context)
            adapter = vipAdapter
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdPlayService.reportPlayAdScenes("close_vip")
                finish()
            }
        })
        viewBinding.ivNavBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewBinding.btnContinue.setThrottleListener {
            startPay()
        }

        viewModel.observerState {
            when (it) {
                is VipStoreState.VipStoreProduct -> {
                    if (!it.list.isNullOrEmpty()) {
                        vipAdapter.addAll(it.list)
                    }
                }

                is VipStoreState.VipPublicityData -> {
                    if (!it.list.isNullOrEmpty()) {
                        bindBannerData(it.list)
                    }
                }
            }
        }
        viewModel.processIntent(VipStoreIntent.VipPublicityData)
        viewModel.processIntent(VipStoreIntent.VipProductData)
        setupVipTipsContent()
        setupVipAgreementContent()
        ReportBehavior.reportEvent("pop_recharge", mutableMapOf<String, Any>().apply {
            put("pop_type", "20200")
            put("source", UserBehavior.chargeSource)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        ReportBehavior.reportCloseCoinLessWindow("20200")
    }

    private fun setupVipTipsContent() {
        val originContent = getString(com.cute.uibase.R.string.str_vip_subscription_tips)
        val index = originContent.indexOf("%s")

        val content = getString(com.cute.uibase.R.string.str_cancel_subscription)
        val displayContent = originContent.format(content)

        val clickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openGooglePlaySubscriptions()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.parseColor("#32E1F0")
            }
        }

        val spannable = SpannableString(displayContent)
        spannable.setSpan(
            clickSpan,
            index,
            index + content.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        viewBinding.tvVipTip.text = spannable
        viewBinding.tvVipTip.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupVipAgreementContent() {
        val originContent = getString(com.cute.uibase.R.string.str_vip_subscription_agreement)
        val firstIndex = originContent.indexOf("%s")
        val lastIndex = originContent.lastIndexOf("%s") - 2

        val oneContent = getString(com.cute.uibase.R.string.str_terms_service)
        val twoContent = getString(com.cute.uibase.R.string.str_privacy_policy)
        val displayContent = originContent.format(oneContent, twoContent)
        val oneClickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                WebViewActivity.startWebView(this@VipStoreActivity, Constant.USER_AGREEMENT)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.parseColor("#32E1F0")
            }
        }

        val twoClickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                WebViewActivity.startWebView(this@VipStoreActivity, Constant.PRIVACY_AGREEMENT)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.parseColor("#32E1F0")
            }
        }

        val spannable = SpannableString(displayContent)
        spannable.setSpan(
            oneClickSpan,
            firstIndex,
            firstIndex + oneContent.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            twoClickSpan,
            lastIndex + oneContent.length,
            displayContent.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        viewBinding.tvVipAgreement.text = spannable
        viewBinding.tvVipAgreement.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun bindBannerData(data: MutableList<VipPowerInfoData>) {
        val bannerAdapter = VipStoreBannerAdapter(this, data)
        viewBinding.banner.viewPager2.offscreenPageLimit = data.size
        viewBinding.banner.setAdapter(bannerAdapter)
            .addBannerLifecycleObserver(this)
            .setIndicator(viewBinding.circleIndicator, false).start()

        val bannerPowerAdapter = VipStoreBannerPowerAdapter(this, data)
        viewBinding.bannerPower.viewPager2.offscreenPageLimit = data.size
        viewBinding.bannerPower.viewPager2.isUserInputEnabled = false
        viewBinding.bannerPower.setAdapter(bannerPowerAdapter)
            .addBannerLifecycleObserver(this).start()
    }

    private fun startPay() {
        val product = vipAdapter.getSelectProduct() ?: return
        PayViewModel.launchSettlementStore(this, product,"20200") { result, msg ->
            if (result) {
                Toaster.showShort(this, "Pay Success")
            } else {
                Toaster.showShort(this, msg)
            }
        }
        ReportBehavior.reportEvent("pop_recharge_continue", mutableMapOf<String, Any>().apply {
            put("pop_type", "20200")
            put("source", UserBehavior.chargeSource)
            put("item_name", product.name)
            put("item_money_amount", product.displayPrice)
        })
    }

    private fun openGooglePlaySubscriptions() {
        try {
            val uri = Uri.parse("https://play.google.com/store/account/subscriptions")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}