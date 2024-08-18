package com.amigo.mine

import android.view.LayoutInflater
import com.amigo.basic.BaseActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.mine.databinding.ActivityAboutBinding
import com.amigo.tool.AppUtil
import com.amigo.uibase.databinding.LayoutTitleBarBinding

class AboutActivity : BaseActivity<ActivityAboutBinding>() {

    private lateinit var titleBinding: LayoutTitleBarBinding

    override fun initViewBinding(layout: LayoutInflater): ActivityAboutBinding {
        return ActivityAboutBinding.inflate(layout)
    }

    override fun initView() {
        titleBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        StatusUtils.setImmerseLayout(titleBinding.flTitle, this)
        titleBinding.tvTitle.text = getString(com.amigo.uibase.R.string.str_about)
        titleBinding.ivNavBack.setOnClickListener { finish() }
        viewBinding.tvVersion.text = AppUtil.getAppVersion(this)
        val appDrawable = AppUtil.getAppIcon(this)
        if (appDrawable != null) {
            viewBinding.ivLogo.setImageDrawable(appDrawable)
        }
    }
}