package com.cute.mine

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.cute.baselogic.userDataStore
import com.cute.basic.BaseModelFragment
import com.cute.mine.databinding.FragmentMineBinding
import com.cute.mine.intent.MineIntent
import com.cute.mine.state.MineState
import com.cute.mine.viewmodel.MineViewModel
import com.cute.picture.loadImage
import com.cute.tool.EventBus
import com.cute.tool.EventBus.subscribe
import com.cute.uibase.event.GetRewardEvent
import com.cute.uibase.event.RemoteNotifyEvent
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.IStoreService
import com.cute.uibase.userbehavior.UserBehavior

@Route(path = RoutePage.MINE.MINE_FRAGMENT)
class MineFragment : BaseModelFragment<FragmentMineBinding, MineViewModel>() {

    override fun initViewBinding(
        layout: LayoutInflater, container: ViewGroup?
    ): FragmentMineBinding {
        return FragmentMineBinding.inflate(layout, container, false)
    }

    override fun initView() {
        viewModel.observerState {
            when (it) {
                is MineState.MeUserState -> bindMineState(it)

                is MineState.VipPowerResult -> {}
            }
        }

        viewBinding.rlVip.setOnClickListener {
            RouteSdk.navigationActivity(RoutePage.STORE.VIP_STORE)
            UserBehavior.setChargeSource("vip_store")
        }

        viewBinding.srlCoin.setOnClickListener {
            RouteSdk.navigationActivity(RoutePage.STORE.COIN_STORE)
            UserBehavior.setChargeSource("coin_store")
        }

        viewBinding.rlMineSetting.setOnClickListener {
            startActivity(Intent(it.context, SettingActivity::class.java))
        }

        viewBinding.rlMinePro.setOnClickListener {
            val iStoreService = RouteSdk.findService(IStoreService::class.java)
            iStoreService.showCodeDialog("20201", null)
        }

        viewBinding.btnEdit.setOnClickListener {
            startActivity(Intent(it.context, ProfileActivity::class.java))
        }

        viewBinding.ivAvatar.setOnClickListener {
            startActivity(Intent(it.context, ProfileActivity::class.java))
        }
        viewModel.processIntent(MineIntent.MeInfo)
        EventBus.event.subscribe<RemoteNotifyEvent>(lifecycleScope) {
            if (it is RemoteNotifyEvent.PaySuccessEvent) {
                viewModel.processIntent(MineIntent.MeInfo)
            }
            if (it is RemoteNotifyEvent.RefreshInfoEvent) {
                viewModel.processIntent(MineIntent.MeInfo)
            }
        }
        EventBus.event.subscribe<GetRewardEvent>(lifecycleScope) {
            viewModel.processIntent(MineIntent.MeInfo)
        }
    }

    override fun firstShowUserVisible() {
        viewModel.processIntent(MineIntent.VipPowerData)
    }

    override fun onResume() {
        super.onResume()
        viewModel.processIntent(MineIntent.MeInfo)
        UserBehavior.setRootPage("me_page")
    }

    private fun bindMineState(state: MineState.MeUserState) {
        val user = state.userInfo ?: return
        viewBinding.ivAvatar.loadImage(
            user.avatar,
            isCircle = true,
            placeholderRes = com.cute.uibase.R.drawable.img_placehoder_round,
            errorRes = com.cute.uibase.R.drawable.img_placehoder_round
        )
        viewBinding.tvName.text = user.name
        viewBinding.tvId.text =
            context?.getString(com.cute.uibase.R.string.str_id_value, "${user.id}")
        viewBinding.tvBalance.text = context?.getString(
            com.cute.uibase.R.string.str_my_balance_value,
            user.balance
        )
        viewBinding.ivVip.setImageResource(if (user.isVip) R.drawable.ic_mine_vip else R.drawable.ic_mine_vip_grey)
        viewBinding.tvVipTime.text = if (user.isVip) user.vipExpiredTime else ""
        context?.apply {
            userDataStore.saveRole(user.role)
            userDataStore.saveUid(user.id)
            userDataStore.saveAvatar(user.avatar)
            userDataStore.saveVip(user.isVip)
            userDataStore.saveCoinMode(user.isCoinMode)
        }
        viewBinding.srlCoin.visibility = if (user.isCoinMode) View.VISIBLE else View.GONE
    }
}