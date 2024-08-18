package com.amigo.uibase.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.basic.dialog.BaseBottomDialog
import com.amigo.logic.http.Gender
import com.amigo.uibase.databinding.DialogGenderBinding
import com.amigo.uibase.gone
import com.amigo.uibase.visible

class GenderDialog : BaseBottomDialog() {

    private lateinit var binding: DialogGenderBinding

    private var onSelectGender: ((Gender) -> Unit)? = null
    private var isShowBoth: Boolean = true

    private var gender: Gender = Gender.MALE
    override fun parseBundle(bundle: Bundle?) {

    }


    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogGenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {

        if (isShowBoth) {
            binding.llEveryoneSelect.visible()
            binding.tvSubTitle.visible()
        } else {
            binding.llEveryoneSelect.gone()
            binding.tvSubTitle.gone()
        }

        when (gender) {
            Gender.MALE -> {
                checkMale()
            }

            Gender.FEMALE -> {
                checkFemale()
            }

            else -> {
                checkBoth()
            }
        }

        binding.llMaleSelect.setOnClickListener {
            checkMale()
            onSelectGender?.invoke(Gender.MALE)
            slowDismissDialog()
        }
        binding.llFemaleSelect.setOnClickListener {
            checkFemale()
            onSelectGender?.invoke(Gender.FEMALE)
            slowDismissDialog()
        }
        binding.llEveryoneSelect.setOnClickListener {
            checkBoth()
            onSelectGender?.invoke(Gender.UNKNOWN)
            slowDismissDialog()
        }
    }

    override fun initData() {
    }


    fun setGender(gender: Gender) {
        this.gender = gender
    }

    fun hideBoth() {
        isShowBoth = false
    }

    fun setOnSelectGenderListener(onSelectGender: ((Gender) -> Unit)?) {
        this.onSelectGender = onSelectGender
    }

    private fun checkMale() {
        binding.cbMaleSelect.isChecked = true
        binding.cbFemaleSelect.isChecked = false
        binding.cbEverySelect.isChecked = false
    }

    private fun checkFemale() {
        binding.cbFemaleSelect.isChecked = true
        binding.cbMaleSelect.isChecked = false
        binding.cbEverySelect.isChecked = false
    }

    private fun checkBoth() {
        binding.cbEverySelect.isChecked = true
        binding.cbMaleSelect.isChecked = false
        binding.cbFemaleSelect.isChecked = false
    }

    private fun slowDismissDialog() {
        binding.root.postDelayed({
            dismissDialog()
        }, 500)
    }
}