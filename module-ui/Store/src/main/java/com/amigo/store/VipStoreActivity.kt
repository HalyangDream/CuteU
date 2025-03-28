package com.amigo.store

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
import com.amigo.analysis.Analysis
import com.amigo.basic.BaseModelActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.logic.http.response.product.VipPowerInfoData
import com.amigo.store.adapter.VipStoreAdapter
import com.amigo.store.adapter.VipStoreBannerAdapter
import com.amigo.store.adapter.VipStoreBannerPowerAdapter
import com.amigo.store.databinding.ActivityVipStoreBinding
import com.amigo.store.intent.VipStoreIntent
import com.amigo.store.state.VipStoreState
import com.amigo.tool.Toaster
import com.amigo.uibase.Constant
import com.amigo.uibase.ReportBehavior
import com.amigo.uibase.WebViewActivity
import com.amigo.uibase.ad.AdPlayService
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.setThrottleListener
import com.amigo.uibase.userbehavior.UserBehavior


@Route(path = RoutePage.STORE.VIP_STORE)
class VipStoreActivity : BaseModelActivity<ActivityVipStoreBinding, VipStoreViewModel>() {

    private lateinit var vipAdapter: VipStoreAdapter
    private var isFromCode = false
    override fun initViewBinding(layout: LayoutInflater): ActivityVipStoreBinding {

        return ActivityVipStoreBinding.inflate(layout)
    }

    override fun initView() {
        Analysis.track("view_vip_store")
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
        Analysis.track("pop_recharge_20200")
    }

    override fun onDestroy() {
        super.onDestroy()
        ReportBehavior.reportCloseCoinLessWindow("20200")
    }

    private fun setupVipTipsContent() {
        val originContent = getString(com.amigo.uibase.R.string.str_vip_subscription_tips)
        val index = originContent.indexOf("%s")

        val content = getString(com.amigo.uibase.R.string.str_cancel_subscription)
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
        val originContent = getString(com.amigo.uibase.R.string.str_vip_subscription_agreement)
        val firstIndex = originContent.indexOf("%s")
        val lastIndex = originContent.lastIndexOf("%s") - 2

        val oneContent = getString(com.amigo.uibase.R.string.str_terms_service)
        val twoContent = getString(com.amigo.uibase.R.string.str_privacy_policy)
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