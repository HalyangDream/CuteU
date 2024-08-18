package com.amigo.uibase

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.content.ContextCompat

class NegativeButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatButton(context, attrs, defStyleAttr) {

    init {
        setBackgroundResource(R.drawable.selector_negative_btn)
        gravity = Gravity.CENTER
    }
}