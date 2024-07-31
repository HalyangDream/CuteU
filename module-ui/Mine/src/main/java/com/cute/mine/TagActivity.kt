package com.cute.mine

import android.view.LayoutInflater
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.cute.basic.BaseModelActivity
import com.cute.basic.util.StatusUtils
import com.cute.mine.adapter.TagAdapter
import com.cute.mine.databinding.ActivityTagBinding
import com.cute.mine.intent.TagIntent
import com.cute.mine.state.TagState
import com.cute.mine.viewmodel.TagViewModel
import com.cute.tool.Toaster
import com.cute.uibase.databinding.LayoutTitleBarBinding

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
        titleBinding.tvTitle.text = getString(com.cute.uibase.R.string.str_tags)
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