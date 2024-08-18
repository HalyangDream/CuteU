package com.amigo.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.chat.databinding.ItemVideoCallRightBinding
import com.amigo.chat.view.CustomChattingAnnotation
import com.amigo.chat.view.CustomChattingView
import com.amigo.chat.view.DIRECTION_RIGHT
import com.amigo.im.bean.Msg
import com.amigo.message.custom.msg.CallRecordMessage
import com.amigo.tool.TimeUtil
import kotlinx.coroutines.CoroutineScope

@CustomChattingAnnotation(direction = DIRECTION_RIGHT, targetClass = CallRecordMessage::class)
class CallRecordRightView : CustomChattingView {

    private lateinit var binding: ItemVideoCallRightBinding
    override fun setScope(scope: CoroutineScope) {

    }
    override fun onGenerateView(
        context: Context, inflater: LayoutInflater, parent: ViewGroup
    ): View {
        binding = ItemVideoCallRightBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onBindChattingData(position: Int, message: Msg) {
        val textMsg = message.message as CallRecordMessage
        val displayContent =
            if (textMsg.duration > 0) "${textMsg.message_content} ${TimeUtil.formatSeconds(textMsg.duration.toInt())}" else "${textMsg.message_content}"
        binding.tvVideoCallStatus.text = displayContent
    }

    override fun isNotifyView(): Boolean {
        return false
    }
}