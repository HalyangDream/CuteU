package com.cute.uibase

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.content.ContextCompat

class PositiveButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatButton(context, attrs, defStyleAttr) {

    init {
        setBackgroundResource(R.drawable.selector_positive_btn)
        setTextColor(ContextCompat.getColor(context, R.color.btn_positive_text_color))
        gravity =Gravity.CENTER
    }
}