package com.amigo.store

import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.amigo.analysis.Analysis
import com.amigo.baselogic.userDataStore
import com.amigo.basic.BaseModelActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.logic.http.response.product.Product
import com.amigo.store.adapter.CoinStoreAdapter
import com.amigo.store.adapter.CoinStoreExtraAdapter
import com.amigo.store.databinding.ActivityCoinStoreBinding
import com.amigo.store.intent.CoinStoreIntent
import com.amigo.store.state.CoinStoreState
import com.amigo.tool.EventBus
import com.amigo.tool.EventBus.subscribe
import com.amigo.tool.Toaster
import com.amigo.uibase.Constant
import com.amigo.uibase.ReportBehavior
import com.amigo.uibase.WebViewActivity
import com.amigo.uibase.ad.AdPlayService
import com.amigo.uibase.event.PayResultEvent
import com.amigo.uibase.event.RemoteNotifyEvent
import com.amigo.uibase.gone
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.IStoreService
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.visible


@Route(path = RoutePage.STORE.COIN_STORE)
class CoinStoreActivity : BaseModelActivity<ActivityCoinStoreBinding, CoinStoreViewModel>() {

    private lateinit var extraAdapter: CoinStoreExtraAdapter
    private lateinit var coinAdapter: CoinStoreAdapter


    override fun initViewBinding(layout: LayoutInflater): ActivityCoinStoreBinding {
        return ActivityCoinStoreBinding.inflate(layout)
    }

    override fun initView() {
        Analysis.track("view_coin_store")
        StatusUtils.setImmerseLayout(viewBinding.flTitle, this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdPlayService.reportPlayAdScenes("close_coin_mall")
                finish()
            }
        })

        viewBinding.tvTitle.text = getString(com.amigo.uibase.R.string.str_coin_store)

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
        Analysis.track("pop_recharge_20100")
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