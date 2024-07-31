package com.cute.uibase.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter constructor(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getIndexOfFragment(fragment: Fragment): Int {
        return fragments.indexOf(fragment)
    }

    fun getFragmentOfIndex(index: Int): Fragment? {
        if (index < 0 || index >= fragments.size) return null
        return fragments[index]
    }
}