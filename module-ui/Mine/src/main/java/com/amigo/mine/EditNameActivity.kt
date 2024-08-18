package com.amigo.mine

import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import com.amigo.basic.BaseModelActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.mine.databinding.ActivityEditNameBinding
import com.amigo.mine.intent.ProfileIntent
import com.amigo.mine.state.ProfileState
import com.amigo.mine.viewmodel.ProfileViewModel
import com.amigo.tool.Toaster
import com.amigo.uibase.databinding.LayoutTitleBarBinding

class EditNameActivity : BaseModelActivity<ActivityEditNameBinding, ProfileViewModel>() {

    private lateinit var titleBinding: LayoutTitleBarBinding

    override fun initViewBinding(layout: LayoutInflater): ActivityEditNameBinding {
        return ActivityEditNameBinding.inflate(layout)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.root, this)
        titleBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        titleBinding.tvTitle.text = getString(com.amigo.uibase.R.string.str_nick_name)
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
                    getString(com.amigo.uibase.R.string.str_please_enter_your_name)
                )
                return@setOnClickListener
            }

            viewModel.processIntent(ProfileIntent.UpdateName(nickName))
        }

        viewModel.observerState {
            if (it is ProfileState.UpdateNickNameState) {
                if (it.state) {
                    Toaster.showShort(this, getString(com.amigo.uibase.R.string.str_save_success))
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toaster.showShort(this, getString(com.amigo.uibase.R.string.str_save_failed))
                }
            }
        }
    }
}