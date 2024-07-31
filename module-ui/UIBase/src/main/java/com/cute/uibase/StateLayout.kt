package com.cute.uibase

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.hjq.shape.view.ShapeView
import com.cute.uibase.adapter.BaseRvFooterAdapter


/**
 * author : mac
 * date   : 2022/10/24
 * e-mail : taolei@51cashbox.com
 */
class StateLayout : FrameLayout {

    var emptyView: View? = null
        private set
    var netErrorView: View? = null
        private set
    var loadingView: View? = null
        private set
    private var mCustomView: View? = null

    private var isFindFooter = false

    private val customArrays by lazy { SparseArray<View>() }

    private val footAdapters by lazy { mutableListOf<BaseRvFooterAdapter<*>>() }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        parseAttr(attrs)
    }

    private fun parseAttr(attrs: AttributeSet?) {
        if (attrs == null) return
        val inflater = LayoutInflater.from(context)
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.StateLayout, 0, 0)

        val emptyLayoutId = a.getResourceId(R.styleable.StateLayout_emptyLayoutId, -1)
        val netErrorLayoutId = a.getResourceId(R.styleable.StateLayout_netErrorLayoutId, -1)
        val loadingLayoutId = a.getResourceId(R.styleable.StateLayout_loadingLayoutId, -1)
        if (emptyLayoutId != -1) {
            emptyView = inflater.inflate(emptyLayoutId, this, false)
            addView(emptyView)
        }
        if (netErrorLayoutId != -1) {
            netErrorView = inflater.inflate(netErrorLayoutId, this, false)
            addView(netErrorView)
        }

        if (loadingLayoutId != -1) {
            loadingView = inflater.inflate(loadingLayoutId, this, false)
            addView(loadingView)
        }
        emptyView?.findViewById<TextView>(R.id.btn_retry)?.setOnClickListener {
            emptyView?.hideView()
            onClickRefresh?.invoke()
        }
        netErrorView?.findViewById<TextView>(R.id.btn_retry)?.setOnClickListener {
            netErrorView?.hideView()
            onClickRefresh?.invoke()
        }

        emptyView?.hideView()
        netErrorView?.hideView()
        loadingView?.hideView()
        post {
            findFootAdapter(this)
        }
    }

    private fun findFootAdapter(viewGroup: ViewGroup) {
        for (i in 0..viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            if (view is RecyclerView && view.adapter is BaseRvFooterAdapter<*>) {
                footAdapters.add(view.adapter as BaseRvFooterAdapter<*>)
                continue
            }
            if (view is ViewGroup) {
                findFootAdapter(view)
            }
        }
    }

    private fun hideAdapterFooter() {
        for (footAdapter in footAdapters) {
            footAdapter.showFooter(false)
        }
    }


    private fun View.showView() {
        if (this.visibility != View.VISIBLE) {
            this.elevation = 1f
            this.visibility = View.VISIBLE
        }
    }

    private fun View.hideView() {
        if (this.visibility == View.VISIBLE) {
            this.elevation = 0f
            this.visibility = View.GONE
        }

    }

    fun addCustomView(@LayoutRes vararg layoutId: Int) {
        val inflater = LayoutInflater.from(context)
        for (i in layoutId) {
            val layout = inflater.inflate(i, this, false)
            customArrays.put(i, layout)
        }
    }

    fun getCustomView(layoutId: Int): View? {
        return customArrays.get(layoutId)
    }

    fun showEmptyView(show: Boolean) {
        if (emptyView == null) {
            throw IllegalArgumentException("StateLayout no content view not exist")
        }
        if (show) {
            emptyView?.showView()
            hideAdapterFooter()
        } else {
            emptyView?.hideView()
        }
    }

    fun showNetErrorView(show: Boolean) {
        if (netErrorView == null) {
            throw IllegalArgumentException("StateLayout network view not exist")
        }
        if (show) {
            netErrorView?.showView()
            hideAdapterFooter()
        } else {
            netErrorView?.hideView()
        }
    }

    fun showLoadingView(show: Boolean) {
        if (loadingView == null) {
            throw IllegalArgumentException("StateLayout loading view not exist")
        }
        if (show) {
            loadingView?.showView()
            hideAdapterFooter()
        } else {
            loadingView?.hideView()
        }
    }

    fun showCustomView(@LayoutRes layoutId: Int) {
        val view = getCustomView(layoutId)
            ?: throw IllegalArgumentException("StateLayout custom view not exist")
        if (mCustomView == null || mCustomView != view) {
            removeView(mCustomView)
            mCustomView = view
            addView(view)
        }
        emptyView?.hideView()
        netErrorView?.hideView()
        loadingView?.hideView()
        mCustomView?.showView()
    }


    private fun hideAll() {
        emptyView?.hideView()
        netErrorView?.hideView()
        loadingView?.hideView()
        mCustomView?.hideView()
    }

    private var onClickRefresh: (() -> Unit)? = null

    fun setOnClickRefresh(listener: (() -> Unit)?) {
        onClickRefresh = listener
    }

}