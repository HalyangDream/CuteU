package com.cute.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import com.cute.baselogic.userDataStore
import com.cute.basic.recycler.BaseRvAdapter
import com.cute.basic.recycler.BaseRvHolder
import com.cute.chat.ChatActivity
import com.cute.chat.databinding.ItemConversationBinding
import com.cute.chat.databinding.ItemConversationHeaderBinding
import com.cute.im.bean.Conversation
import com.cute.picture.loadImage
import com.cute.tool.TimeUtil
import com.cute.uibase.ad.AdPlayService
import com.cute.uibase.gone
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.visible

class ConversationHeaderAdapter(context: Context) :
    BaseRvAdapter<Conversation, ItemConversationHeaderBinding>(context) {

    private val appIcon =
        context.resources.getIdentifier("ic_launcher", "mipmap", context.packageName)
    private val appName = context.resources.getIdentifier("app_name", "string", context.packageName)


    override fun bindViewBinding(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): BaseRvHolder<ItemConversationHeaderBinding> {
        return BaseRvHolder(ItemConversationHeaderBinding.inflate(layoutInflater, parent, false))
    }


    override fun bindData(
        position: Int,
        itemBind: ItemConversationHeaderBinding,
        item: Conversation
    ) {
        itemBind.tvMessage.text = item.lastMessage ?: "Enjoy your time"
        itemBind.tvNewCount.visibility = if (item.unreadCount > 0) View.VISIBLE else View.INVISIBLE
        itemBind.tvNewCount.text = "${item.unreadCount}"
        val time = if (item.timeStamp <= 0) System.currentTimeMillis() else item.timeStamp
        itemBind.tvTime.text = TimeUtil.formatTimestampCarryDate(time)
        itemBind.ivAvatar.loadImage(
            appIcon,
            placeholderRes = com.cute.uibase.R.drawable.img_placehoder_round, isCircle = true
        )
        itemBind.tvName.text = context.getString(appName)
        itemBind.conversationHeaderItem.setOnClickListener {
            if (item.peer.isDigitsOnly()) {
                RouteSdk.navigationChat(item.peer.toLong(), "im_list")
            }
        }
    }


    fun isHeaderInfo(conversation: Conversation): Boolean {
        for (item in items) {
            if (item.channel == conversation.channel) {
                return true
            }
        }
        return false
    }

    fun receiveConversation(conversation: Conversation) {
        setData(mutableListOf(conversation))
    }

}