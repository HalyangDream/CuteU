package com.cute.home.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cute.basic.BaseFragment
import com.cute.home.databinding.DialogCountryRegionFragmentBinding
import com.cute.logic.http.model.ListRepository
import com.cute.tool.EventBus
import com.cute.uibase.event.VideoFilterEvent
import kotlinx.coroutines.launch

class DialogCountryRegionFragment : BaseFragment<DialogCountryRegionFragmentBinding>() {

    private val _listRepository = ListRepository()
    private val countryAdapter by lazy { context?.let { VideoFilterAdapter(it) } }
    private val regionAdapter by lazy { context?.let { VideoFilterAdapter(it) } }
    override fun initViewBinding(
        layout: LayoutInflater, container: ViewGroup?
    ): DialogCountryRegionFragmentBinding {

        return DialogCountryRegionFragmentBinding.inflate(layout, container, false)
    }

    override fun initView() {
        viewBinding.rvCountry.apply {
            setHasFixedSize(true)
            countryAdapter?.setHasStableIds(true)
            adapter = countryAdapter
            countryAdapter?.setSelectorListener {
                EventBus.post(VideoFilterEvent.CountryFilterEvent(it))
            }
        }
        viewBinding.rvRegion.apply {
            setHasFixedSize(true)
            regionAdapter?.setHasStableIds(true)
            adapter = regionAdapter
            regionAdapter?.setSelectorListener {
                EventBus.post(VideoFilterEvent.RegionFilterEvent(it))
            }
        }
        lifecycleScope.launch {
            val res = _listRepository.getFilterCondition()
            countryAdapter?.setData(res.data?.country)
            regionAdapter?.setData(res.data?.region)
        }
    }

    override fun firstShowUserVisible() {

    }

}