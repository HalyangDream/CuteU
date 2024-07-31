package com.cute.mine

import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import com.cute.basic.BaseModelActivity
import com.cute.basic.util.StatusUtils
import com.cute.mine.databinding.ActivityEditNameBinding
import com.cute.mine.intent.ProfileIntent
import com.cute.mine.state.ProfileState
import com.cute.mine.viewmodel.ProfileViewModel
import com.cute.tool.Toaster
import com.cute.uibase.databinding.LayoutTitleBarBinding

class EditNameActivity : BaseModelActivity<ActivityEditNameBinding, ProfileViewModel>() {

    private lateinit var titleBinding: LayoutTitleBarBinding

    override fun initViewBinding(layout: LayoutInflater): ActivityEditNameBinding {
        return ActivityEditNameBinding.inflate(layout)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.root, this)
        titleBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        titleBinding.tvTitle.text = getString(com.cute.uibase.R.string.str_nick_name)
        viewBinding.etNick.addTextChangedListener {
            viewBinding.tvCounter.text = "${it?.length}/30"
        }
        intent.getStringExtra("nick_name").apply {
            viewBinding.etNick.setText(this)
            viewBinding.etNick.setSelection(this?.length ?: 0)
        }
        titleBinding.ivNavBack.setOnClickListener {
            finish()
        }
        viewBinding.btnSave.setOnClickListener {
            val nickName = viewBinding.etNick.text.toString()
            if (nickName.isEmpty()) {
                Toaster.showShort(
                    this,
                    getString(com.cute.uibase.R.string.str_please_enter_your_name)
                )
                return@setOnClickListener
            }

            viewModel.processIntent(ProfileIntent.UpdateName(nickName))
        }

        viewModel.observerState {
            if (it is ProfileState.UpdateNickNameState) {
                if (it.state) {
                    Toaster.showShort(this, getString(com.cute.uibase.R.string.str_save_success))
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toaster.showShort(this, getString(com.cute.uibase.R.string.str_save_failed))
                }
            }
        }
    }
}