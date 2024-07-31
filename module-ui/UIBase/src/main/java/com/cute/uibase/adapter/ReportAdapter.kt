package com.cute.uibase.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.cute.uibase.R
import com.cute.uibase.bean.ReportData
import com.cute.uibase.databinding.ItemReportBinding

class ReportAdapter(context: Context) : BaseRvFooterAdapter<ReportData>(context) {

    private var onItemClick: ((reportData: ReportData?) -> Unit)? = null

    fun setItemClickListener(onItemClick: ((reportData: ReportData?) -> Unit)?) {
        this.onItemClick = onItemClick
    }

    override fun createMainHolder(parent: ViewGroup): MultiHolder<out ViewBinding> {
        return ReportHolder(
            mLayoutInflater.inflate(
                R.layout.item_report,
                parent,
                false
            )
        )
    }

    override fun bindMainData(
        position: Int,
        item: ReportData?,
        holder: MultiHolder<out ViewBinding>
    ) {
        val itemBind = holder.binding as ItemReportBinding
        item?.apply {
            itemBind.ivEmoji.setImageResource(item.resId)
            itemBind.tvReportReason.text = content
            itemBind.cbCheck.isChecked = isChecked
        }
    }

    class ReportHolder(private val view: View) : MultiHolder<ViewBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemReportBinding =
            ItemReportBinding.bind(view)
    }

    override fun onMainItemClick(position: Int, view: View) {
        super.onMainItemClick(position, view)
        updateCheck(position)
        onItemClick?.invoke(getItem(position))
    }

    private fun updateCheck(position: Int) {
        items.forEach { it.isChecked = false }
        items[position].isChecked = true
        notifyDataSetChanged()
    }
}