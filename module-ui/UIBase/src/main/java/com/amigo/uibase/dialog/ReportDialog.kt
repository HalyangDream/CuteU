package com.amigo.uibase.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amigo.basic.dialog.BaseBottomDialog
import com.amigo.uibase.R
import com.amigo.uibase.adapter.ReportAdapter
import com.amigo.uibase.bean.ReportData
import com.amigo.uibase.databinding.DialogReportBinding

class ReportDialog : BaseBottomDialog() {

    private lateinit var binding: DialogReportBinding
    private val reportAdapter by lazy { context?.let { ReportAdapter(it) } }
    private var onReportData: ((reportData: ReportData) -> Unit)? = null
    private val repostList = arrayListOf<ReportData>()

    fun setReportListener(onReportData: ((reportData: ReportData) -> Unit)?) {
        this.onReportData = onReportData
    }

    override fun parseBundle(bundle: Bundle?) {
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        repostList.add(
            ReportData(
                R.drawable.img_emoji_advertising,
                getString(com.amigo.uibase.R.string.str_advertising)
            )
        )
        repostList.add(
            ReportData(
                R.drawable.img_emoji_harass,
                getString(com.amigo.uibase.R.string.str_harass_me)
            )
        )
        repostList.add(
            ReportData(
                R.drawable.img_emoji_copycat,
                getString(com.amigo.uibase.R.string.str_copycat)
            )
        )
        repostList.add(
            ReportData(
                R.drawable.img_emoji_illegal,
                getString(com.amigo.uibase.R.string.str_illegal)
            )
        )
        repostList.add(
            ReportData(
                R.drawable.img_emoji_sexual,
                getString(com.amigo.uibase.R.string.str_sexual_hint)
            )
        )
        repostList.add(
            ReportData(
                R.drawable.img_emoji_other,
                getString(com.amigo.uibase.R.string.str_other)
            )
        )

        binding.ivClose.setOnClickListener { dismissDialog() }
        binding.rvReport.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvReport.adapter = reportAdapter
        reportAdapter?.setData(repostList)
    }

    override fun initData() {
        reportAdapter?.setItemClickListener {
            if (it != null) {
                onReportData?.invoke(it)
            }
            dismissDialog()
        }
    }
}