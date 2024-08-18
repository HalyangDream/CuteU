package com.amigo.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.chat.databinding.ItemTextRightBinding
import com.amigo.chat.view.CustomChattingAnnotation
import com.amigo.chat.view.CustomChattingView
import com.amigo.chat.view.DIRECTION_RIGHT
import com.amigo.im.bean.MessageStatus
import com.amigo.im.bean.Msg
import com.amigo.message.custom.msg.TextMessage
import com.amigo.uibase.gone
import com.amigo.uibase.visible
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