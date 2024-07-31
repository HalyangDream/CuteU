package com.cute.mine

import android.view.LayoutInflater
import com.cute.basic.BaseActivity
import com.cute.basic.util.StatusUtils
import com.cute.mine.databinding.ActivityAboutBinding
import com.cute.tool.AppUtil
import com.cute.uibase.databinding.LayoutTitleBarBinding

class AboutActivity : BaseActivity<ActivityAboutBinding>() {

    private lateinit var titleBinding: LayoutTitleBarBinding

    override fun initViewBinding(layout: LayoutInflater): ActivityAboutBinding {
        return ActivityAboutBinding.inflate(layout)
    }

    override fun initView() {
        titleBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        StatusUtils.setImmerseLayout(titleBinding.flTitle, this)
        titleBinding.tvTitle.text = getString(com.cute.uibase.R.string.str_about)
        titleBinding.ivNavBack.setOnClickListener { finish() }
        viewBinding.tvVersion.text = AppUtil.getAppVersion(this)
        val appDrawable = AppUtil.getAppIcon(this)
        if (appDrawable != null) {
            viewBinding.ivLogo.setImageDrawable(appDrawable)
        }
    }
}