package com.amigo.mine

import android.view.LayoutInflater
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.amigo.basic.BaseModelActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.mine.adapter.TagAdapter
import com.amigo.mine.databinding.ActivityTagBinding
import com.amigo.mine.intent.TagIntent
import com.amigo.mine.state.TagState
import com.amigo.mine.viewmodel.TagViewModel
import com.amigo.tool.Toaster
import com.amigo.uibase.databinding.LayoutTitleBarBinding

class TagActivity : BaseModelActivity<ActivityTagBinding, TagViewModel>() {

    private lateinit var titleBinding: LayoutTitleBarBinding

    private val tagAdapter: TagAdapter by lazy {
        TagAdapter(this)
    }

    override fun initViewBinding(layout: LayoutInflater): ActivityTagBinding {
        return ActivityTagBinding.inflate(layout)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.root, this)
        titleBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        titleBinding.tvTitle.text = getString(com.amigo.uibase.R.string.str_tags)
        titleBinding.ivNavBack.setOnClickListener {
            finish()
        }

        viewBinding.rvTags.layoutManager =
            FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)
        viewBinding.rvTags.adapter = tagAdapter

        viewBinding.btnConfirm.setOnClickListener {
            val selectedTags = tagAdapter.getSelectedTags()
            viewModel.processIntent(TagIntent.UpdateTagList(selectedTags))
        }

        viewModel.processIntent(TagIntent.GetTagList)
        viewModel.observerState {
            when (it) {
                is TagState.TagList -> {
                    tagAdapter.submitList(it.list)
                }

                is TagState.UpdateTagList -> {
                    if (it.state){
                        Toaster.showShort(this, "Apply success")
                        setResult(RESULT_OK)
                        finish()
                    }else{
                        Toaster.showShort(this, "Apply failed")
                    }

                }
            }
        }
    }
}