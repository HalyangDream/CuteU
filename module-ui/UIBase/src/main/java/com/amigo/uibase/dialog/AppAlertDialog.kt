package com.amigo.uibase.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.basic.dialog.BaseCenterDialog
import com.amigo.uibase.databinding.DialogAppAlertBinding

class AppAlertDialog private constructor() : BaseCenterDialog() {

    private lateinit var binding: DialogAppAlertBinding
    private var title: String = ""
    private var content: String = ""
    private var positive: String = ""
    private var negative: String = ""
    private var listener: (() -> Unit)? = null

    override fun parseBundle(bundle: Bundle?) {
        title = bundle?.getString("title") ?: ""
        content = bundle?.getString("content") ?: ""
        positive = bundle?.getString("positive") ?: ""
        negative = bundle?.getString("negative") ?: ""

    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogAppAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {

        binding.tvPositive.setOnClickListener {
            listener?.invoke()
            dismissDialog()
        }
        binding.tvNegative.setOnClickListener {
            dismissDialog()
        }
    }

    override fun initData() {
        binding.tvTitle.text = title
        binding.tvContent.text = content
        binding.tvPositive.text = positive
        binding.tvNegative.text = negative
    }

    fun setPositiveListener(listener: (() -> Unit)?) {
        this.listener = listener
    }

    class Builder {

        private val bundle = Bundle()

        fun setTitle(title: String): Builder {
            bundle.putString("title", title)
            return this
        }

        fun setContent(content: String): Builder {
            bundle.putString("content", content)
            return this
        }

        fun setBtnPositiveContent(positive: String): Builder {
            bundle.putString("positive", positive)
            return this
        }

        fun setBtnNegativeContent(negative: String): Builder {
            bundle.putString("negative", negative)
            return this
        }

        fun create(context: Context): AppAlertDialog {
            val dialog = AppAlertDialog()
            dialog.showDialog(context, bundle)
            return dialog
        }

    }
}