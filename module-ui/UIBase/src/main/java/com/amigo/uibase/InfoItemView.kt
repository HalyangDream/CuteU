package com.amigo.uibase

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.amigo.picture.loadImage
import com.amigo.uibase.databinding.ViewInfoItemBinding

class InfoItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var infoBinding: ViewInfoItemBinding? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_info_item, this)
        infoBinding = ViewInfoItemBinding.bind(view)
        context.obtainStyledAttributes(attrs, R.styleable.InfoItemView).apply {
            val leftContent = getString(R.styleable.InfoItemView_leftContent)
            val hideSplitLine = this.getBoolean(R.styleable.InfoItemView_hideSplitLine, false)
            recycle()
            infoBinding?.apply {
                tvLeftContent.text = leftContent
                this.vDivider.visibility = if (hideSplitLine) View.GONE else View.VISIBLE
            }

        }
    }

    fun setRightContent(content: String?) {
        infoBinding?.tvRightContent?.text = content ?: ""
    }

    fun getRightContent(): String {
        return infoBinding?.tvRightContent?.text?.toString() ?: ""
    }

    fun setRightImage(imgUrl: String) {
        infoBinding?.ivRightImg?.visible()
        infoBinding?.ivRightImg?.loadImage(
            imgUrl,
            isCircle = true,
            placeholderRes = R.drawable.img_placehoder_round
        )
    }

}