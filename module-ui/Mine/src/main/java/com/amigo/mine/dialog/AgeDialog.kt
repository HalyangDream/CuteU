package com.amigo.mine.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.basic.dialog.BaseBottomDialog
import com.amigo.mine.databinding.DialogAgeBinding
import com.amigo.uibase.adapter.ArrayWheelAdapter
import com.amigo.uibase.wheelview.WheelView

class AgeDialog : BaseBottomDialog() {

    private lateinit var binding: DialogAgeBinding

    private var onSelected: ((Int) -> Unit)? = null

    private var currentAge: Int = 0

    private val ageList = mutableListOf<Int>().apply {
        for (i in 18..60) {
            add(i)
        }
    }

    override fun parseBundle(bundle: Bundle?) {
        currentAge = bundle?.getInt("current_age") ?: 0
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogAgeBinding.inflate(inflater, container, false)
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
            wheelAgeLeft.adapter = ArrayWheelAdapter(ageList)
            val currentIndex = ageList.indexOf(currentAge)
            if (currentIndex != -1){
                wheelAgeLeft.currentItem = currentIndex
            }
            btnApply.setOnClickListener {
                val age = ageList[wheelAgeLeft.currentItem]
                onSelected?.invoke(age)
                dismissDialog()
            }
        }
    }

    override fun initData() {

    }

    fun setOnSelectedListener(onSelected: ((Int) -> Unit)?) {
        this.onSelected = onSelected
    }
}