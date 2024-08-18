package com.amigo.mine.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.amigo.logic.http.response.list.BlackUser
import com.amigo.mine.R
import com.amigo.mine.databinding.ItemBlackUserBinding
import com.amigo.picture.loadImage
import com.amigo.uibase.adapter.BaseRvFooterAdapter

class BlackUserAdapter(context: Context) : BaseRvFooterAdapter<BlackUser>(context) {

    override fun createMainHolder(parent: ViewGroup): MultiHolder<out ViewBinding> {
        return BlackUserHolder(mLayoutInflater.inflate(R.layout.item_black_user, parent, false))
    }

    override fun bindMainData(
        position: Int,
        item: BlackUser?,
        holder: MultiHolder<out ViewBinding>
    ) {
        val itemBind = holder.binding as ItemBlackUserBinding
        itemBind.ivAvatar.loadImage(
            item!!.avatar,
            isCircle = true,
            placeholderRes = com.amigo.uibase.R.drawable.img_placehoder_round,
            errorRes = com.amigo.uibase.R.drawable.img_placehoder_round
        )
        itemBind.tvName.text = item.name
        itemBind.ivCountry.loadImage(
            item.countryImg,
            placeholderRes = com.amigo.uibase.R.drawable.img_placehoder,
            errorRes = com.amigo.uibase.R.drawable.img_placehoder
        )
        itemBind.tvCountry.text = item.country
        itemBind.btnRemove.setOnClickListener {
            block?.invoke(item)
        }
    }

    private var block: ((BlackUser) -> Unit)? = null

    fun setRemoveListener(block: (BlackUser) -> Unit) {
        this.block = block
    }

    class BlackUserHolder(val view: View) : MultiHolder<ItemBlackUserBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemBlackUserBinding =
            ItemBlackUserBinding.bind(itemView)
    }
}