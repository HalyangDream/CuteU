package com.amigo.call.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.basic.dialog.BaseBottomDialog
import com.amigo.call.R
import com.amigo.call.databinding.DialogUnlockDeviceFunctionBinding
import com.amigo.logic.http.response.call.DeviceFunctionEnum
import com.amigo.logic.http.response.call.DeviceFunctionInfo

class UnLockDeviceFunctionDialog : BaseBottomDialog() {


    private lateinit var binding: DialogUnlockDeviceFunctionBinding

    private var vipUnlock: ((DeviceFunctionInfo) -> Unit)? = null
    private var coinUnlock: ((DeviceFunctionInfo) -> Unit)? = null
    private var info: DeviceFunctionInfo? = null
    private var enum: DeviceFunctionEnum = DeviceFunctionEnum.CAMERA_CLOSE

    override fun parseBundle(bundle: Bundle?) {

    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogUnlockDeviceFunctionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        setDialogCancelable(false)
        setDialogCanceledOnTouchOutside(false)
        when (enum) {
            DeviceFunctionEnum.CAMERA_CLOSE -> {
                binding.ivDeviceFunction.setImageResource(R.drawable.ic_dialog_unlock_camera_close)
                binding.tvDeviceFunction.text =
                    context?.getString(com.amigo.uibase.R.string.str_camera_turned_off)
                binding.tvDeviceFunctionTip.text =
                    context?.getString(com.amigo.uibase.R.string.str_camera_turned_off_tip)
            }

            DeviceFunctionEnum.CAMERA_SWITCH -> {
                binding.ivDeviceFunction.setImageResource(R.drawable.ic_dialog_unlock_camera_switch)
                binding.tvDeviceFunction.text =
                    context?.getString(com.amigo.uibase.R.string.str_using_rear_camera)
                binding.tvDeviceFunctionTip.text =
                    context?.getString(com.amigo.uibase.R.string.str_using_rear_camer_tip)
            }

            DeviceFunctionEnum.VOICE_MUTE -> {
                binding.ivDeviceFunction.setImageResource(R.drawable.ic_dialog_unlock_voice_mute)
                binding.tvDeviceFunction.text =
                    context?.getString(com.amigo.uibase.R.string.str_microphone_turned_off)
                binding.tvDeviceFunctionTip.text =
                    context?.getString(com.amigo.uibase.R.string.str_microphone_turned_off_tip)
            }
        }
        binding.ivClose.setOnClickListener {
            dismissDialog()
        }
        binding.tvUnlockPrice.text = "${info?.unlockPrice}"
        binding.btnVipFree.setOnClickListener {
            if (info != null) {
                vipUnlock?.invoke(info!!)
            }
            dismissDialog()
        }
        binding.tvUnlockPrice.setOnClickListener {
            if (info != null) {
                coinUnlock?.invoke(info!!)
            }
            dismissDialog()
        }
    }

    override fun initData() {
    }

    fun setData(enum: DeviceFunctionEnum, info: DeviceFunctionInfo) {
        this.enum = enum
        this.info = info
    }

    fun setClickVipListener(vipUnlock: ((DeviceFunctionInfo) -> Unit)?) {
        this.vipUnlock = vipUnlock
    }

    fun setClickCoinListener(coinUnlock: ((DeviceFunctionInfo) -> Unit)?) {
        this.coinUnlock = coinUnlock
    }
}