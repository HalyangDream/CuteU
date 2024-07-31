package com.cute.mine

import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import com.cute.basic.BaseModelActivity
import com.cute.basic.util.StatusUtils
import com.cute.mine.databinding.ActivityEditIntroductionBinding
import com.cute.mine.databinding.ActivityEditNameBinding
import com.cute.mine.intent.ProfileIntent
import com.cute.mine.state.ProfileState
import com.cute.mine.viewmodel.ProfileViewModel
import com.cute.tool.Toaster
import com.cute.uibase.databinding.LayoutTitleBarBinding

class EditIntroductionActivity :
    BaseModelActivity<ActivityEditIntroductionBinding, ProfileViewModel>() {

    private lateinit var titleBinding: LayoutTitleBarBinding

    override fun initViewBinding(layout: LayoutInflater): ActivityEditIntroductionBinding {
        return ActivityEditIntroductionBinding.inflate(layout)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.root, this)
        titleBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        titleBinding.tvTitle.text = getString(com.cute.uibase.R.string.str_introduction)
        titleBinding.ivNavBack.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        viewBinding.etIntro.addTextChangedListener {
            viewBinding.tvCounter.text = "${it?.length}/140"
        }
        intent.getStringExtra("intro").apply {
            viewBinding.etIntro.setText(this)
            viewBinding.etIntro.setSelection(this?.length ?: 0)
        }
        viewBinding.btnSave.setOnClickListener {
            val nickName = viewBinding.etIntro.text.toString()
            if (nickName.isEmpty()) {
                Toaster.showShort(
                    this,
                    getString(com.cute.uibase.R.string.str_please_enter_your_intro)
                )
                return@setOnClickListener
            }

            viewModel.processIntent(ProfileIntent.UpdateIntroduction(nickName))
        }

        viewModel.observerState {
            if (it is ProfileState.UpdateIntroductionState) {
                if (it.state) {
                    Toaster.showShort(this, getString(com.cute.uibase.R.string.str_save_success))
                    finish()
                } else {
                    Toaster.showShort(this, getString(com.cute.uibase.R.string.str_save_failed))
                }
            }
        }
    }
}