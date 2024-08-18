package com.amigo.uibase

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.amigo.uibase.databinding.LayoutRefreshRecylcerBinding

class RefreshRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    val binding: LayoutRefreshRecylcerBinding

    init {
        val mView = LayoutInflater.from(context).inflate(R.layout.layout_refresh_recylcer, null)
        binding = LayoutRefreshRecylcerBinding.bind(mView)
    }

    fun SmartRefreshLayout.applySetting(block: SmartRefreshLayout.() -> Unit): SmartRefreshLayout {
        block()
        return this
    }

    fun RecyclerView.applySetting(block: RecyclerView.() -> Unit): RecyclerView {
        block()
        return this
    }
}