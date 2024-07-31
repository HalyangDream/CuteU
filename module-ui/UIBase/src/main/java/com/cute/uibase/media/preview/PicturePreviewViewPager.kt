package com.cute.uibase.media.preview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView


class PicturePreviewViewPager @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attributeSet, defStyle) {


    private var xDistance: Float = 0f
    private var yDistance: Float = 0f
    private var lastX: Float = 0f
    private var lastY: Float = 0f

    private val touchSlop: Int

    init {
        val vc = ViewConfiguration.get(context)
        touchSlop = vc.scaledTouchSlop
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (e.pointerCount > 1) {
            return false
        }
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                xDistance = 0f
                yDistance = 0f
                lastX = e.x
                lastY = e.y
            }

            MotionEvent.ACTION_MOVE -> {
                val curX = e.x
                val curY = e.y
                xDistance += kotlin.math.abs(curX - lastX)
                yDistance += kotlin.math.abs(curY - lastY)
                lastX = curX
                lastY = curY
                if (xDistance > touchSlop && xDistance > yDistance)
                    return true // 横向滑动，拦截事件
            }
        }
        return super.onInterceptTouchEvent(e)
    }
}