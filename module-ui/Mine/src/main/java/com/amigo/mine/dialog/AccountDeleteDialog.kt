package com.amigo.mine.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.amigo.basic.dialog.BaseCenterDialog
import com.amigo.logic.http.model.ProfileRepository
import com.amigo.mine.databinding.DialogAccountDeleteBinding
import com.amigo.tool.Toaster
import com.amigo.uibase.ActivityStack
import com.amigo.uibase.DefaultLoadingDialog
import kotlinx.coroutines.launch

class AccountDeleteDialog : BaseCenterDialog() {
    private val loadingDialog = DefaultLoadingDialog()
    private lateinit var binding: DialogAccountDeleteBinding
    private val profileRepository = ProfileRepository()

    private var callback: (() -> Unit)? = null
    fun setDeleteAccountCallback(callback: (() -> Unit)?) {
        this.callback = callback
    }

    override fun parseBundle(bundle: Bundle?) {

    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogAccountDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        binding.tvCancel.setOnClickListener {
            dismissDialog()
        }

        binding.tvConfirm.setOnClickListener {
            if (binding.ivCheck.isChecked) {
                deleteAccount()
            }
        }
    }

    override fun initData() {
    }

    private fun deleteAccount() {
        loadingDialog.showDialog(context, null)
        lifecycleScope.launch {
            val response = profileRepository.destroyAccount()
            loadingDialog.dismissDialog()
            if (response.isSuccess) {
                callback?.invoke()
                dismissDialog()
            } else {
                Toaster.showShort(ActivityStack.application, "${response.msg}")
            }
        }
    }
}