package com.amigo.basic.dialog

import android.view.Gravity

/**
 * author : mac
 * date   : 2021/10/12
 * 
 * 不允许在构造方法内，传递任何参数
 * 传递参数通过Bundle传递，在使用 @see{showDialog()}
 */
abstract class BaseCenterDialog : BaseDialog() {

    var widthRate = 0.85f
    var heighRate = -2f

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.setGravity(Gravity.CENTER)
        val attributes = window?.attributes
        attributes?.dimAmount = 0.9f
        window?.attributes = attributes
    }

    override fun setDialogHeightRate(): Float {
        return heighRate
    }

    override fun setDialogWidthRate(): Float {
        return widthRate
    }

    fun initWidthRate(widthRate: Float) {
        this.widthRate = widthRate
    }

    fun initHeightRate(heighRate: Float) {
        this.heighRate = heighRate
    }

}