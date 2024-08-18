package com.amigo.tool

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.max

object DragViewUtil {


    fun registerDragAction(v: View) {
        registerDragAction(v, 0);
    }

    /**
     * 拖动View方法
     *
     * @param v     view
     * @param delay 延迟
     */
    fun registerDragAction(v: View, delay: Long) {
        v.setOnTouchListener(TouchListener(v.context, delay));
    }

    private class TouchListener(context: Context, val delay: Long = 0) : View.OnTouchListener {
        private var downX: Float = 0f
        private var downY: Float = 0f
        private var downTime: Long = 0
        private var isMove = false;
        private var canDrag = true;


        private var originViewWidth: Int? = null
        private var originViewHeight: Int? = null

        private val maxWidth by lazy(LazyThreadSafetyMode.NONE) {
            context.resources.displayMetrics.widthPixels
        }
        private val maxHeight by lazy(LazyThreadSafetyMode.NONE) {
            context.resources.displayMetrics.heightPixels
        }

        private fun haveDelay(): Boolean {
            return delay > 0
        }

        private fun setViewLocation(view: View) {
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            when (view.layoutParams) {
                is LinearLayout.LayoutParams -> {
                    val llParam = view.layoutParams as LinearLayout.LayoutParams
                    llParam.marginStart = view.left
                    llParam.topMargin = view.top
                    llParam.marginEnd = 0
                    llParam.bottomMargin = 0
                    view.layoutParams = llParam
                }

                is FrameLayout.LayoutParams -> {
                    val flParam = view.layoutParams as FrameLayout.LayoutParams
                    flParam.marginStart = view.left
                    flParam.topMargin = view.top
                    flParam.marginEnd = 0
                    flParam.bottomMargin = 0
                    view.layoutParams = flParam
                }

                is RelativeLayout.LayoutParams -> {
                    val rlParam = view.layoutParams as RelativeLayout.LayoutParams
                    rlParam.removeRule(RelativeLayout.ALIGN_PARENT_START)
                    rlParam.removeRule(RelativeLayout.ALIGN_PARENT_TOP)
                    rlParam.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    rlParam.removeRule(RelativeLayout.ALIGN_PARENT_END)
                    rlParam.removeRule(RelativeLayout.ALIGN_START)
                    rlParam.removeRule(RelativeLayout.ALIGN_TOP)
                    rlParam.removeRule(RelativeLayout.ALIGN_BOTTOM)
                    rlParam.removeRule(RelativeLayout.ALIGN_END)
                    rlParam.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
                    rlParam.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                    rlParam.marginStart = view.left
                    rlParam.topMargin = view.top
                    rlParam.marginEnd = 0
                    rlParam.bottomMargin = 0
                    view.layoutParams = rlParam
                }

                is ConstraintLayout.LayoutParams -> {
                    val clParam = view.layoutParams as ConstraintLayout.LayoutParams
                    clParam.reset()
                    clParam.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    clParam.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    clParam.marginStart = view.left
                    clParam.topMargin = view.top
                    clParam.marginEnd = 0
                    clParam.bottomMargin = 0
                    view.layoutParams = clParam
                }
            }

        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (originViewWidth == null) {
                        originViewWidth = v.width
                    }
                    if (originViewHeight == null) {
                        originViewHeight = v.height
                    }
                    downX = event.x
                    downY = event.y
                    isMove = false
                    downTime = System.currentTimeMillis()
                    canDrag = !haveDelay()
                }

                MotionEvent.ACTION_MOVE -> {
                    if (haveDelay() && !canDrag) {
                        val currMillis = System.currentTimeMillis();
                        if (currMillis - downTime >= delay) {
                            canDrag = true;
                        }
                    }
                    if (canDrag) {
                        val xDistance = event.x - downX
                        val yDistance = event.y - downY
                        if (xDistance != 0f && yDistance != 0f) {
                            var left = (v.left + xDistance).toInt()
                            var right = (left + v.width).toInt()
                            var top = (v.top + yDistance).toInt()
                            var bottom = (top + v.height).toInt()
                            if (left < 0) {
                                right += -left
                                left = 0
                            }
                            if (top < 0) {
                                bottom += -top
                                top = 0
                            }
                            if (right > maxWidth) {
                                left -= right - maxWidth
                                right = maxWidth
                                val size = right - left
                                //修复view缩小的问题
                                if (originViewWidth != null && size < originViewWidth!!) {
                                    left -= originViewWidth!! - size
                                }
                            }
                            if (bottom > maxHeight) {
                                top -= bottom - maxHeight
                                bottom = maxHeight
                            }
                            v.layout(left, top, right, bottom)
                            isMove = true
                        }
                    }

                }

                MotionEvent.ACTION_UP -> {
                    if (canDrag) {
                        setViewLocation(v)
                    }
                }
            }

            return isMove
        }
    }
}