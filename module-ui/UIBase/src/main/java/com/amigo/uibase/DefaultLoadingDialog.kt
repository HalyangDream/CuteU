package com.amigo.uibase

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.basic.dialog.BaseCenterDialog
import com.amigo.uibase.databinding.DialogDefaultLoadingBinding

class DefaultLoadingDialog : BaseCenterDialog() {

    private lateinit var binding: DialogDefaultLoadingBinding

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        val attributes = window?.attributes
        attributes?.dimAmount = 0f
        window?.attributes = attributes
    }

    override fun parseBundle(bundle: Bundle?) {

    }

    override fun setDialogWidthRate(): Float {
        return -2f
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogDefaultLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        setDialogCancelable(false)
        setDialogCanceledOnTouchOutside(false)
    }

    override fun initData() {
    }
}