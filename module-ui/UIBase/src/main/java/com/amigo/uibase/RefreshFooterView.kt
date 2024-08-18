package com.amigo.uibase

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import com.scwang.smart.refresh.layout.simple.SimpleComponent

@SuppressLint("RestrictedApi")
class RefreshFooterView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : SimpleComponent(context, attributeSet, defStyle), RefreshFooter {

    init {
        View.inflate(context, R.layout.layout_refresh_footer, this)
        gone()
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        view.gone()
        return 0
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        super.onStartAnimator(refreshLayout, height, maxDragHeight)
        view.visible()
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }
}