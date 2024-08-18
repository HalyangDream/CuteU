package com.amigo.mine

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.amigo.baselogic.userDataStore
import com.amigo.basic.BaseModelFragment
import com.amigo.basic.util.StatusUtils
import com.amigo.logic.http.Gender
import com.amigo.mine.databinding.FragmentMineBinding
import com.amigo.mine.intent.MineIntent
import com.amigo.mine.state.MineState
import com.amigo.mine.viewmodel.MineViewModel
import com.amigo.picture.loadImage
import com.amigo.tool.EventBus
import com.amigo.tool.EventBus.subscribe
import com.amigo.uibase.event.GetRewardEvent
import com.amigo.uibase.event.RemoteNotifyEvent
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.IStoreService
import com.amigo.uibase.userbehavior.UserBehavior

@Route(path = RoutePage.MINE.MINE_FRAGMENT)
class MineFragment : BaseModelFragment<FragmentMineBinding, MineViewModel>() {

    override fun initViewBinding(
        layout: LayoutInflater, container: ViewGroup?
    ): FragmentMineBinding {
        return FragmentMineBinding.inflate(layout, container, false)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.rlTitle, this)
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
            placeholderRes = com.amigo.uibase.R.drawable.img_placehoder_round,
            errorRes = com.amigo.uibase.R.drawable.img_placehoder_round
        )
        viewBinding.tvName.text = user.name
        viewBinding.tvId.text =
            context?.getString(com.amigo.uibase.R.string.str_id_value, "${user.id}")
        viewBinding.tvBalance.text = context?.getString(
            com.amigo.uibase.R.string.str_my_balance_value,
            user.balance
        )
        viewBinding.ivVip.setImageResource(if (user.isVip) R.drawable.ic_mine_vip else R.drawable.ic_mine_vip_grey)
        viewBinding.tvVipTime.text = if (user.isVip) user.vipExpiredTime else ""
       if(user.gender ==Gender.FEMALE.value){
           viewBinding.ivGender.setImageResource(com.amigo.uibase.R.drawable.ic_female)
       }else{
           viewBinding.ivGender.setImageResource(com.amigo.uibase.R.drawable.ic_male)
       }

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