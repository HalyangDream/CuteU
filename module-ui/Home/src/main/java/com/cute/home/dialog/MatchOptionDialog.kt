package com.cute.home.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.text.isDigitsOnly
import com.cute.baselogic.userDataStore
import com.cute.basic.dialog.BaseCenterDialog
import com.cute.basic.recycler.BaseRvAdapter
import com.cute.basic.recycler.BaseRvHolder
import com.cute.basic.util.StatusUtils
import com.cute.home.databinding.DialogMatchOptionBinding
import com.cute.home.databinding.ItemMatchOptionBinding
import com.cute.logic.http.response.list.MatchOption
import com.cute.picture.loadImage
import com.cute.uibase.gone
import com.cute.uibase.visible


class MatchOptionDialog : BaseCenterDialog() {

    private lateinit var binding: DialogMatchOptionBinding
    private var matchAdapter: MatchOptionAdapter? = null
    override fun parseBundle(bundle: Bundle?) {

    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        val attributes = window?.attributes
        attributes?.dimAmount = 0f
        window?.attributes = attributes
    }

    override fun setDialogWidthRate(): Float {
        return -1f
    }

    override fun setDialogHeightRate(): Float {
        return -1f
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogMatchOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        dialog?.window?.let {
            StatusUtils.setStatusBarColor(Color.WHITE, it)
        }
        binding.flRoot.setOnClickListener {
            dismissDialog()
        }
        binding.rvMatchType.apply {
            matchAdapter = MatchOptionAdapter(context)
            adapter = matchAdapter
            matchAdapter?.setItemClickListener {
                listener?.invoke(it)
                dismissDialog()
            }
        }
        val params = binding.llContent.layoutParams as FrameLayout.LayoutParams
        params.topMargin += offset
    }

    override fun initData() {
        matchAdapter?.setData(selectors)
        matchAdapter?.setSelector(selector)
    }

    private var listener: ((item: MatchOption) -> Unit)? = null
    private var selectors: MutableList<MatchOption>? = null
    private var selector: MatchOption? = null
    private var offset: Int = 0

    fun setOffsetTop(offset: Int) {
        this.offset = offset
    }

    fun setSelector(selector: MatchOption) {
        this.selector = selector
        matchAdapter?.setSelector(selector)
    }

    fun setSelectors(selectors: MutableList<MatchOption>) {
        this.selectors = selectors
    }

    fun setSelectorListener(listener: ((item: MatchOption) -> Unit)?) {
        this.listener = listener
    }

    private class MatchOptionAdapter(context: Context) :
        BaseRvAdapter<MatchOption, ItemMatchOptionBinding>(context) {


        private var selector: MatchOption? = null

        override fun bindViewBinding(
            context: Context, parent: ViewGroup, viewType: Int, layoutInflater: LayoutInflater
        ): BaseRvHolder<ItemMatchOptionBinding> {

            return BaseRvHolder(ItemMatchOptionBinding.inflate(layoutInflater, parent, false))
        }

        override fun bindData(position: Int, binding: ItemMatchOptionBinding, item: MatchOption) {
            binding.cbMatchSelect.isChecked = selector != null && selector!!.id == item.id
//            binding.ivMatchType.loadImage(item.icon)
            binding.tvMatchType.text = item.name
            if (!item.price.isDigitsOnly() || item.price == "0") {
                binding.ivPrice.gone()
                binding.tvPrice.text =
                    if (item.price.isDigitsOnly()) context.getString(com.cute.uibase.R.string.str_free) else "${item.price}"
            } else {
                binding.ivPrice.visible()
                binding.tvPrice.text = item.price
            }

            if (mContext.userDataStore.hasCoinMode()) {
                binding.llPrice.visible()
            } else {
                binding.llPrice.gone()
            }

            binding.root.setOnClickListener {
                listener?.invoke(item)
            }
        }

        private var listener: ((item: MatchOption) -> Unit)? = null

        fun setSelector(selector: MatchOption?) {
            this.selector = selector
            notifyItemRangeChanged(0, itemCount)
        }

        fun setItemClickListener(listener: ((item: MatchOption) -> Unit)?) {
            this.listener = listener
        }
    }
}