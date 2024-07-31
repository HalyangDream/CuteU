package com.cute.mine.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.cute.basic.dialog.BaseCenterDialog
import com.cute.logic.http.model.ProfileRepository
import com.cute.mine.databinding.DialogAccountInfoBinding
import com.cute.tool.Toaster
import com.cute.uibase.ActivityStack
import kotlinx.coroutines.launch

class AccountInfoDialog : BaseCenterDialog() {

    private lateinit var binding: DialogAccountInfoBinding
    private val profileRepository = ProfileRepository()
    private var userName: String = ""
    private var pwd: String = ""

    private var callback: (() -> Unit)? = null
    fun setDeleteAccountCallback(callback: (() -> Unit)?) {
        this.callback = callback
    }

    override fun parseBundle(bundle: Bundle?) {

    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogAccountInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        binding.btnClose.setOnClickListener {
            dismissDialog()
        }

        binding.tvCopyUid.setOnClickListener {
            copyAccountInfo(userName)
        }

        binding.tvCopyPwd.setOnClickListener {
            copyAccountInfo(pwd)
        }

        binding.tvDeleteAccount.setOnClickListener {
            val deleteDialog = AccountDeleteDialog()
            deleteDialog.showDialog(context, null)
            deleteDialog.setDeleteAccountCallback(callback)
        }
    }

    override fun initData() {
        lifecycleScope.launch {
            val response = profileRepository.getAccountInfo()
            if (response.data != null) {
                userName = "${response.data!!.userName}"
                pwd = "${response.data!!.password}"
                binding.tvUserId.text = "User ID:$userName"
                binding.tvUserPwd.text = "Password: $pwd"
            }
        }
    }

    private fun copyAccountInfo(content: CharSequence) {
        if (content.isEmpty()) return
        val clipData = ClipData.newPlainText("", content)
        val manager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(clipData)
        Toaster.showShort(ActivityStack.application, "Copied")
    }
}