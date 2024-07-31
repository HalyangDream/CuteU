package com.cute.mine.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.basic.dialog.BaseBottomDialog
import com.cute.mine.databinding.DialogAgeBinding
import com.cute.mine.databinding.DialogHeightBinding
import com.cute.uibase.adapter.ArrayWheelAdapter
import com.cute.uibase.wheelview.WheelView

class HeightDialog : BaseBottomDialog() {

    private lateinit var binding: DialogHeightBinding

    private var onSelected: ((String) -> Unit)? = null

    private var currentHeight: String = ""
    private val heightList = mutableListOf<String>().apply {
        for (i in 165..185) {
            add("$i cm")
        }
    }

    override fun parseBundle(bundle: Bundle?) {
        currentHeight = bundle?.getString("current_height") ?: ""
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogHeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        binding.apply {


            wheelAgeLeft.setItemsVisibleCount(3)
            wheelAgeLeft.setLineSpacingMultiplier(4f)
            wheelAgeLeft.setCyclic(false)
            wheelAgeLeft.setGravity(Gravity.CENTER)
            wheelAgeLeft.setDividerType(WheelView.DividerType.WRAP)
            wheelAgeLeft.setDividerColor(Color.WHITE)
            wheelAgeLeft.adapter = ArrayWheelAdapter(heightList)
            val currentIndex = heightList.indexOf(currentHeight)
            if (currentIndex != -1) {
                wheelAgeLeft.currentItem = currentIndex
            }
            btnApply.setOnClickListener {
                val age = heightList[wheelAgeLeft.currentItem]
                onSelected?.invoke(age)
                dismissDialog()
            }
        }
    }

    override fun initData() {

    }

    fun setOnSelectedListener(onSelected: ((String) -> Unit)?) {
        this.onSelected = onSelected
    }
}