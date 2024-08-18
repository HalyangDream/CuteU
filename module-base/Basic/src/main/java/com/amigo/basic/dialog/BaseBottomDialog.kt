package com.amigo.basic.dialog

import android.view.Gravity
import androidx.fragment.app.FragmentManager
import com.amigo.basic.R

/**
 * author : mac
 * date   : 2021/9/1
 *
 * 不允许在构造方法内，传递任何参数
 * 传递参数通过Bundle传递，在使用 @see{showDialog()}
 */
abstract class BaseBottomDialog : BaseDialog() {


    override fun onStart() {
        super.onStart()
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setWindowAnimations(R.style.ActionDialogAnimation)
    }

    override fun setDialogWidthRate(): Float {
        return -1f
    }

    override fun setDialogHeightRate(): Float {

        return -2f
    }


    override fun show(manager: FragmentManager, tag: String?) {

    }

}