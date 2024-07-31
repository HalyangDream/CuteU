package com.cute.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.chat.databinding.ItemTextRightBinding
import com.cute.chat.view.CustomChattingAnnotation
import com.cute.chat.view.CustomChattingView
import com.cute.chat.view.DIRECTION_RIGHT
import com.cute.im.bean.MessageStatus
import com.cute.im.bean.Msg
import com.cute.message.custom.msg.TextMessage
import com.cute.uibase.gone
import com.cute.uibase.visible
import kotlinx.coroutines.CoroutineScope

@CustomChattingAnnotation(direction = DIRECTION_RIGHT, targetClass = TextMessage::class)
class TextRightView : CustomChattingView {

    private lateinit var binding: ItemTextRightBinding
    override fun setScope(scope: CoroutineScope) {

    }
    override fun onGenerateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): View {
        binding = ItemTextRightBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onBindChattingData(position: Int, message: Msg) {
        val textMsg = message.message as TextMessage
        binding.tvContent.text = "${textMsg.messageContent}"
        when (message.status) {
            MessageStatus.SUCCESS -> {
                binding.progressCircular.gone()
                binding.ivErrorStatus.gone()
            }

            MessageStatus.FAIL -> {
                binding.progressCircular.gone()
                binding.ivErrorStatus.visible()
            }

            MessageStatus.SENDING -> {
                binding.progressCircular.visible()
                binding.ivErrorStatus.gone()
            }
        }
    }

    override fun isNotifyView(): Boolean {
        return false
    }
}