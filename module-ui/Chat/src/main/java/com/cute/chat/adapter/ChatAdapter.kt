package com.cute.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import com.cute.baselogic.userDataStore
import com.cute.basic.recycler.BaseRvAdapter
import com.cute.basic.recycler.BaseRvHolder
import com.cute.chat.databinding.ItemChatMessageBinding
import com.cute.chat.view.CustomChattingAnnotation
import com.cute.chat.view.CustomChattingView
import com.cute.chat.view.DIRECTION_CENTER
import com.cute.chat.view.DIRECTION_LEFT
import com.cute.chat.view.DIRECTION_RIGHT
import com.cute.im.IMCore
import com.cute.im.bean.Msg
import com.cute.im.service.UserService
import com.cute.picture.loadImage
import com.cute.tool.TimeUtil
import com.cute.uibase.gone
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ChatAdapter(context: Context) : BaseRvAdapter<Msg, ItemChatMessageBinding>(context) {

    private val uid = context.userDataStore.getUid()

    private lateinit var scope: CoroutineScope

    fun setCoroutineScope(scope: CoroutineScope) {
        this.scope = scope
    }

//    private fun isDirectionCenter(message: CustomMessage?): Boolean {
//        if (message is AnchorReceivedGiftMessage) return true
//        return false
//    }

    override fun bindViewBinding(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): BaseRvHolder<ItemChatMessageBinding> {
        return BaseRvHolder(ItemChatMessageBinding.inflate(layoutInflater, parent, false))
    }


    override fun bindData(position: Int, binding: ItemChatMessageBinding, item: Msg) {

        val time = calcTime(position, item)
        binding.tvTime.text = "$time"
        binding.flNotify.removeAllViews()
        binding.flSendLayout.removeAllViews()
        binding.flReceiveLayout.removeAllViews()
        val ccv = CustomChattingView.findView(context, item)
        val direction = (ccv::class.annotations[0] as CustomChattingAnnotation).direction
        ccv.setScope(scope)
        when (direction) {
            DIRECTION_CENTER -> {
                binding.ivSendAvatar.gone()
                binding.ivReceiveAvatar.gone()
                binding.clContent.visibility = View.GONE
                binding.flNotify.visibility = View.VISIBLE
                binding.tvTime.visibility = View.GONE
                val view =
                    ccv.onGenerateView(
                        context,
                        LayoutInflater.from(context),
                        binding.flNotify
                    )
                ccv.onBindChattingData(position, item)
                binding.flNotify.addView(view)
            }

            DIRECTION_LEFT -> {
                binding.ivSendAvatar.gone()
                binding.ivReceiveAvatar.visible()
                loadAvatar(
                    userId = if ("$uid" == item.sendId) item.receiveId else item.sendId,
                    binding.ivReceiveAvatar
                )
                binding.clContent.visibility = View.VISIBLE
                binding.flNotify.visibility = View.GONE
                val view =
                    ccv.onGenerateView(
                        context,
                        LayoutInflater.from(context),
                        binding.flReceiveLayout
                    )
                ccv.onBindChattingData(position, item)
                binding.flReceiveLayout.addView(view)
                binding.tvTime.visibility = View.VISIBLE
                setTimeViewLocation(DIRECTION_LEFT, binding.tvTime, binding.flReceiveLayout)
                binding.ivReceiveAvatar.setOnClickListener {
                    val userId = if ("$uid" == item.sendId) item.receiveId else item.sendId
                    if (userId.isNotEmpty() && userId.isDigitsOnly()) {
                        RouteSdk.navigationUserDetail(userId.toLong(), "msg_page")
                    }
                }
            }

            DIRECTION_RIGHT -> {
                binding.ivSendAvatar.visible()
                binding.ivReceiveAvatar.gone()
                binding.ivSendAvatar.loadImage(
                    mContext.userDataStore.readAvatar(),
                    isCircle = true,
                    placeholderRes = com.cute.uibase.R.drawable.img_placehoder_round_grey
                )
                binding.clContent.visibility = View.VISIBLE
                binding.flNotify.visibility = View.GONE
                val view =
                    ccv.onGenerateView(
                        context,
                        LayoutInflater.from(context),
                        binding.flSendLayout
                    )
                ccv.onBindChattingData(position, item)
                binding.flSendLayout.addView(view)
                binding.tvTime.visibility = View.VISIBLE
                setTimeViewLocation(DIRECTION_RIGHT, binding.tvTime, binding.flSendLayout)
            }
        }
    }

    private fun loadAvatar(userId: String, imageView: ImageView) {
        scope.launch {
            val user = IMCore.getService(UserService::class.java).getUserInfo(userId)
            if (user != null) {
                imageView.loadImage(user.avatar!!, isCircle = true)
            }
        }
    }

    /**
     * 设置time的位置
     * @param direction 方向
     */
    private fun setTimeViewLocation(direction: Int, timeView: View, parent: ViewGroup) {
        val param = timeView.layoutParams as ConstraintLayout.LayoutParams
        when (direction) {
            DIRECTION_LEFT -> {
                param.topToBottom = parent.id
                param.startToStart = parent.id
                param.endToEnd = ConstraintLayout.LayoutParams.UNSET
            }

            DIRECTION_RIGHT -> {
                param.topToBottom = parent.id
                param.endToEnd = parent.id
                param.startToStart = ConstraintLayout.LayoutParams.UNSET
            }
        }
        timeView.layoutParams = param

    }

    private fun calcTime(position: Int, item: Msg): String? {
        return TimeUtil.formatTimestampCarryDate(item.timeStamp)
    }


    fun receiveMsg(msg: Msg) {
        synchronized(items) {
            val result = getItemExist(msg)
            if (result) {
                updateMsgState(msg)
            } else {
                add(msg)
            }
        }

    }

    private fun updateMsgState(msg: Msg) {
        val size = items.size - 1
        for (i in size downTo 0) {
            val item = items[i]
            if (item.messageId == msg.messageId) {
                set(i, msg)
                break
            }
        }
    }


    private fun getItemExist(message: Msg): Boolean {
        if (items.isEmpty()) return false
        val size = items.size - 1
        for (i in size downTo 0) {
            val item = items[i]
            if (item.messageId == message.messageId) return true
        }
        return false
    }
}