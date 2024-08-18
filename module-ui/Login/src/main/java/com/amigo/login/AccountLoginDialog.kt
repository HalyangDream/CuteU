package com.amigo.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.basic.dialog.BaseCenterDialog
import com.amigo.login.databinding.DialogAccountLoginBinding
import com.amigo.uibase.setThrottleListener

class AccountLoginDialog : BaseCenterDialog() {

    private lateinit var binding: DialogAccountLoginBinding
    private var listener: ((userName: String, password: String) -> Unit)? = null

    override fun parseBundle(bundle: Bundle?) {

    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogAccountLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        binding.ivClose.setOnClickListener {
            dismissDialog()
        }
        binding.btnLogin.setThrottleListener {
            val userName = binding.setUserId.text.toString()
            val password = binding.setPassword.text.toString()
            if (userName.trim().isNotEmpty() && password.trim().isNotEmpty()) {
                listener?.invoke(userName, password)
                dismissDialog()
            }
        }
    }

    override fun initData() {

    }

    fun setAccountLoginListener(listener: (userId: String, password: String) -> Unit) {
        this.listener = listener
    }
}