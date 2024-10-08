package com.amigo.mine.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.basic.dialog.BaseBottomDialog
import com.amigo.mine.bean.Language
import com.amigo.uibase.adapter.ArrayWheelAdapter
import com.amigo.mine.databinding.DialogLanguageBinding
import com.amigo.tool.AppUtil
import com.amigo.uibase.wheelview.WheelView

class LanguageDialog : BaseBottomDialog() {

    private lateinit var binding: DialogLanguageBinding

    private var onSelected: ((Int, Language) -> Unit)? = null

    private val languageList = mutableListOf<String>()

    override fun parseBundle(bundle: Bundle?) {
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        val languages = Language.createLanguageList()
        val sysLocale = AppUtil.getSysLocale()

        languages.add(0, Language(sysLocale.language, "Follow System", sysLocale.country, ""))
        languages.forEach {
            languageList.add(it.name)
        }
        binding.apply {
            wheelLanguage.setItemsVisibleCount(3)
            wheelLanguage.setLineSpacingMultiplier(4f)
            wheelLanguage.setCyclic(false)
            wheelLanguage.setDividerType(WheelView.DividerType.WRAP)
            wheelLanguage.setDividerColor(Color.WHITE)
            wheelLanguage.adapter = ArrayWheelAdapter(languageList)

            btnApply.setOnClickListener {
                val language = languages[wheelLanguage.currentItem]
                onSelected?.invoke(wheelLanguage.currentItem, language)
                dismissDialog()
            }
        }
    }

    override fun initData() {
    }

    fun setOnSelectedListener(onSelected: ((Int, Language) -> Unit)?) {
        this.onSelected = onSelected
    }
}