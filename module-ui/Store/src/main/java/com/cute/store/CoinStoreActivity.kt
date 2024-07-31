package com.cute.store

import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.cute.analysis.Analysis
import com.cute.baselogic.userDataStore
import com.cute.basic.BaseModelActivity
import com.cute.basic.util.StatusUtils
import com.cute.logic.http.response.product.Product
import com.cute.store.adapter.CoinStoreAdapter
import com.cute.store.adapter.CoinStoreExtraAdapter
import com.cute.store.databinding.ActivityCoinStoreBinding
import com.cute.store.intent.CoinStoreIntent
import com.cute.store.state.CoinStoreState
import com.cute.tool.EventBus
import com.cute.tool.EventBus.subscribe
import com.cute.tool.Toaster
import com.cute.uibase.Constant
import com.cute.uibase.ReportBehavior
import com.cute.uibase.WebViewActivity
import com.cute.uibase.ad.AdPlayService
import com.cute.uibase.event.PayResultEvent
import com.cute.uibase.event.RemoteNotifyEvent
import com.cute.uibase.gone
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.IStoreService
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible


@Route(path = RoutePage.STORE.COIN_STORE)
class CoinStoreActivity : BaseModelActivity<ActivityCoinStoreBinding, CoinStoreViewModel>() {

    private lateinit var extraAdapter: CoinStoreExtraAdapter
    private lateinit var coinAdapter: CoinStoreAdapter


    override fun initViewBinding(layout: LayoutInflater): ActivityCoinStoreBinding {
        return ActivityCoinStoreBinding.inflate(layout)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.flTitle, this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdPlayService.reportPlayAdScenes("close_coin_mall")
                finish()
            }
        })

        viewBinding.tvTitle.text = getString(com.cute.uibase.R.string.str_coin_store)

        viewBinding.ivNavBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewBinding.rvExtraProduct.apply {
            extraAdapter = CoinStoreExtraAdapter(context)
            adapter = extraAdapter
            extraAdapter.setOnClickItemProductListener {
                startPay(it)
            }
        }

        viewBinding.rvProduct.apply {
            coinAdapter = CoinStoreAdapter(context)
            adapter = coinAdapter
            coinAdapter.setOnClickItemProductListener {
                startPay(it)
            }
        }
        viewModel.observerState {
            when (it) {
                is CoinStoreState.Balance -> {
                    viewBinding.tvBalance.text = "${it.balance}"
                }

                is CoinStoreState.CoinStoreProduct -> {
                    if (it.extraList.isNullOrEmpty()) {
                        viewBinding.vSplit.gone()
                        viewBinding.tvExtraTitle.gone()
                    } else {
                        viewBinding.vSplit.visible()
                        viewBinding.tvExtraTitle.visible()
                    }
                    extraAdapter.submitList(it.extraList)
                    coinAdapter.submitList(it.list)
                }
            }
        }

        EventBus.event.subscribe<RemoteNotifyEvent>(lifecycleScope) {
            if (it is RemoteNotifyEvent.PaySuccessEvent) {
                viewModel.processIntent(CoinStoreIntent.GetBalance)
                viewModel.processIntent(CoinStoreIntent.CoinProductData)
            }
        }
        viewModel.processIntent(CoinStoreIntent.GetBalance)
        viewModel.processIntent(CoinStoreIntent.CoinProductData)
        ReportBehavior.reportEvent("pop_recharge", mutableMapOf<String, Any>().apply {
            put("pop_type", "20100")
            put("source", UserBehavior.chargeSource)
        })
    }


    private fun startPay(product: Product) {
        PayViewModel.launchSettlementStore(this, product, "20100") { result, msg ->
            if (result) {
                Toaster.showShort(this, "Pay Success")
            } else {
                Toaster.showShort(this, msg)
            }
        }
        ReportBehavior.reportEvent("pop_recharge_continue", mutableMapOf<String, Any>().apply {
            put("pop_type", "20100")
            put("source", UserBehavior.chargeSource)
            put("item_name", product.name)
            put("item_money_amount", product.displayPrice)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        ReportBehavior.reportCloseCoinLessWindow("20100")
    }

}