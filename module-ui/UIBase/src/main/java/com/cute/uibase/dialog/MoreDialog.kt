package com.cute.uibase.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.basic.dialog.BaseBottomDialog
import com.cute.uibase.R
import com.cute.uibase.databinding.DialogMoreBinding

class MoreDialog : BaseBottomDialog() {

    private lateinit var binding: DialogMoreBinding

    private var addBlackAction: () -> Unit = {}
    private var deleteAction: () -> Unit = {}
    private var reportAction: () -> Unit = {}

    private var hideDelete: Boolean = false
    private var isBlock: Boolean = false

    override fun parseBundle(bundle: Bundle?) {
        isBlock = bundle?.getBoolean("isBlock", false) ?: false
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        binding.tvAddBlack.text = if (isBlock) context?.getString(R.string.str_remove_to_blacklist)
        else context?.getString(R.string.str_add_to_blacklist)
        if (hideDelete) {
            binding.tvDelete.visibility = View.GONE
        }
        binding.tvAddBlack.setOnClickListener {
            addBlackAction.invoke()
            dismissDialog()
        }
        binding.tvDelete.setOnClickListener {
            deleteAction.invoke()
            dismissDialog()
        }
        binding.tvCancle.setOnClickListener {
            dismissDialog()
        }
        binding.tvReport.setOnClickListener {
            reportAction.invoke()
            dismissDialog()
        }
    }

    fun hideDelete() {
        hideDelete = true
    }


    fun setMoreClickAction(
        addBlackAction: () -> Unit, deleteAction: () -> Unit, reportAction: () -> Unit
    ) {
        this.addBlackAction = addBlackAction
        this.deleteAction = deleteAction
        this.reportAction = reportAction
    }

    override fun initData() {

    }
}