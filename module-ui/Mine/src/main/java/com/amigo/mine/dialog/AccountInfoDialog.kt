package com.amigo.mine.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.amigo.basic.dialog.BaseCenterDialog
import com.amigo.logic.http.model.ProfileRepository
import com.amigo.mine.databinding.DialogAccountInfoBinding
import com.amigo.tool.Toaster
import com.amigo.uibase.ActivityStack
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