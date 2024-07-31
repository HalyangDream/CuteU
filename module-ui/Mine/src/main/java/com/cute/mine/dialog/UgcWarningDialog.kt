package com.cute.mine.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.basic.dialog.BaseCenterDialog
import com.cute.mine.databinding.DialogUgcWarningBinding

class UgcWarningDialog :BaseCenterDialog() {

    private lateinit var binding:DialogUgcWarningBinding

    private var onConfirmAction: (() -> Unit)? = null

    override fun parseBundle(bundle: Bundle?) {

    }

    override fun setDialogWidthRate(): Float {
        return 0.9f
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogUgcWarningBinding.inflate(inflater)
        return binding.root
    }

    override fun initView(view: View?) {
        binding.llCheck.setOnClickListener {
            binding.ivCheck.isChecked = !binding.ivCheck.isChecked
        }
        binding.tvCancel.setOnClickListener {
            dismissDialog()
        }
        binding.tvConfirm.setOnClickListener {
            if (binding.ivCheck.isChecked){
                onConfirmAction?.invoke()
                dismissDialog()
            }
        }
    }

    fun setOnConfirmAction(action: () -> Unit){
        onConfirmAction = action
    }

    override fun initData() {

    }
}