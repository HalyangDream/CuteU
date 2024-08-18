package com.amigo.chat.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.viewbinding.ViewBinding
import com.amigo.chat.ChatActivity
import com.amigo.chat.R
import com.amigo.chat.databinding.ItemConversationBinding
import com.amigo.im.IMCore
import com.amigo.im.bean.Conversation
import com.amigo.im.bean.Msg
import com.amigo.im.service.UserService
import com.amigo.picture.loadImage
import com.amigo.tool.TimeUtil
import com.amigo.uibase.adapter.BaseRvFooterAdapter
import com.amigo.uibase.route.RouteSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ConversationAdapter(context: Context, private val lifeScope: CoroutineScope) :
    BaseRvFooterAdapter<Conversation>(context) {

    private val _userService = IMCore.getService(UserService::class.java)
    private var longClickListener: ((Int, String, Conversation) -> Unit)? = null

    init {
        setData(mutableListOf())
    }

    override fun createMainHolder(parent: ViewGroup): MultiHolder<out ViewBinding> {
        return ConversationHolder(
            mLayoutInflater.inflate(
                R.layout.item_conversation, parent, false
            )
        )
    }

    override fun bindMainData(
        position: Int, item: Conversation?, holder: MultiHolder<out ViewBinding>
    ) {
        val itemBind = holder.binding as ItemConversationBinding
        itemBind.tvMessage.text = item!!.lastMessage ?: ""
        itemBind.tvNewCount.visibility = if (item.unreadCount > 0) View.VISIBLE else View.INVISIBLE
        itemBind.tvNewCount.text = if (item.unreadCount > 99) "99+" else "${item.unreadCount}"
        itemBind.tvTime.text = TimeUtil.formatTimestampCarryDate(item.timeStamp)
        lifeScope.launch {
            val user = _userService.getUserInfo(item.peer)
            if (user != null) {
                itemBind.ivAvatar.loadImage(
                    user.avatar!!,
                    isCircle = true,
                    placeholderRes = com.amigo.uibase.R.drawable.img_placehoder_round
                )
                itemBind.tvName.text = "${user.name}"
            }
        }
    }


    override fun onMainItemClick(position: Int, view: View) {
        super.onMainItemClick(position, view)
        val item = getItem(position)
        if (item != null && item.peer.isDigitsOnly()) {
            RouteSdk.navigationChat(item.peer.toLong(), "im_list")
        }
    }

    override fun onMainItemLongClick(position: Int, view: View) {
        super.onMainItemLongClick(position, view)
        val item = getItem(position)
        if (item != null) {
            val itemBind = ItemConversationBinding.bind(view)
            val name = itemBind.tvName.text.toString()
            longClickListener?.invoke(position, name, item)
        }
    }


    fun setLongClickListener(listener: ((Int, String, Conversation) -> Unit)?) {
        this.longClickListener = listener
    }

    fun lastConversation(): Conversation? {
        return items.lastOrNull()
    }


    fun receiveConversation(conversation: Conversation) {
        synchronized(items) {
            val index = getIndex(conversation)
            if (index == -1) {
                add(0, conversation)
                notifyItemRangeChanged(0, itemCount)
            } else {
                val cur = getItem(index)!!
                when (index) {
                    0 -> {
                        set(0, conversation)
                        notifyItemRangeChanged(0, 1)
                    }

                    else -> {
                        if (cur.timeStamp >= conversation.timeStamp) {
                            set(index, conversation)
                            notifyItemRangeChanged(index, 1)
                        } else {
                            set(index, conversation)
                            move(index, 0)
                            notifyItemRangeChanged(0, itemCount)
                        }
                    }
                }
            }
        }
    }


    fun removeConversation(list: MutableList<Conversation>?) {
        if (list.isNullOrEmpty()) return
        synchronized(items) {
            val deleteList = list.filter { isContains(it) }.toList()
            if (deleteList.isNotEmpty()) {
                for (conversation in deleteList) {
                    removeFromData(conversation)
                }
            }
        }
    }


    private fun getIndex(conversation: Conversation): Int {
        synchronized(items) {
            val size = items.size
            for (i in 0 until size) {
                val item = items[i]
                if (item.channel == conversation.channel) {
                    return i
                }
            }
        }
        return -1
    }


    private fun removeFromData(conversation: Conversation) {
        synchronized(items) {
            val size = items.size - 1
            for (i in size downTo 0) {
                val item = items[i]
                if (item.channel == conversation.channel) {
                    removeAt(i)
                    notifyItemRangeChanged(i, size - i)
                    break
                }
            }
        }
    }


    private fun isContains(conversation: Conversation): Boolean {
        synchronized(items) {
            for (datum in items) {
                if (datum.channel == conversation.channel) return true
            }
            return false
        }
    }


    private class ConversationHolder(val view: View) : MultiHolder<ItemConversationBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemConversationBinding =
            ItemConversationBinding.bind(itemView)
    }


}