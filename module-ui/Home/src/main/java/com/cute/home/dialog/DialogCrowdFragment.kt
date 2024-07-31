package com.cute.home.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.cute.basic.BaseFragment
import com.cute.home.databinding.DialogCrowdFragmentBinding
import com.cute.logic.http.model.ListRepository
import com.cute.tool.EventBus
import com.cute.uibase.event.VideoFilterEvent
import kotlinx.coroutines.launch

class DialogCrowdFragment : BaseFragment<DialogCrowdFragmentBinding>() {

    private val _listRepository = ListRepository()
    private val feelAdapter by lazy { context?.let { VideoFilterAdapter(it) } }
    private val languageAdapter by lazy { context?.let { VideoFilterAdapter(it) } }

    override fun initViewBinding(
        layout: LayoutInflater,
        container: ViewGroup?
    ): DialogCrowdFragmentBinding {

        return DialogCrowdFragmentBinding.inflate(layout, container, false)
    }

    override fun initView() {
        viewBinding.rvFeeling.apply {
            setHasFixedSize(true)
            feelAdapter?.setHasStableIds(true)
            adapter = feelAdapter
            feelAdapter?.setSelectorListener {
                EventBus.post(VideoFilterEvent.FeelingFilterEvent(it))
            }
        }
        viewBinding.rvLanguage.apply {
            setHasFixedSize(true)
            languageAdapter?.setHasStableIds(true)
            adapter = languageAdapter
            languageAdapter?.setSelectorListener {
                EventBus.post(VideoFilterEvent.LanguageFilterEvent(it))
            }
        }
        lifecycleScope.launch {
            val res = _listRepository.getFilterCondition()
            feelAdapter?.setData(res.data?.feeling)
            languageAdapter?.setData(res.data?.language)
        }
    }

    override fun firstShowUserVisible() {

    }

}