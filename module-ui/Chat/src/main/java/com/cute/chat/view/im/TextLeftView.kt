package com.cute.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.chat.R
import com.cute.chat.databinding.ItemTextLeftBinding
import com.cute.chat.view.CustomChattingAnnotation
import com.cute.chat.view.CustomChattingView
import com.cute.chat.view.DIRECTION_LEFT
import com.cute.im.IMCore
import com.cute.im.bean.Msg
import com.cute.im.service.MessageService
import com.cute.logic.http.model.ToolRepository
import com.cute.message.custom.msg.TextMessage
import com.cute.tool.AppUtil
import com.cute.uibase.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@CustomChattingAnnotation(direction = DIRECTION_LEFT, targetClass = TextMessage::class)
class TextLeftView : CustomChattingView {

    private var scope: CoroutineScope? = null

    private val toolRepository by lazy { ToolRepository() }

    private lateinit var binding: ItemTextLeftBinding

    override fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }

    override fun onGenerateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): View {
        binding = ItemTextLeftBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onBindChattingData(position: Int, message: Msg) {
        val textMsg = message.message as TextMessage
        binding.tvContent.text = "${textMsg.messageContent}"
        if (!textMsg.messageTranslate.isNullOrEmpty()) {
            binding.translateLine.visible()
            binding.tvTranslateContent.visible()
            binding.ivTranslate.setImageResource(R.drawable.ic_translate_sel)
            binding.tvTranslateContent.text = textMsg.messageTranslate
        }
        binding.ivTranslate.setOnClickListener {
            scope?.launch {
                val translate =
                    toolRepository.translate(textMsg.messageContent!!, AppUtil.getSysLocale())
                if (translate.isSuccess) {
                    binding.translateLine.visible()
                    binding.ivTranslate.setImageResource(R.drawable.ic_translate_sel)
                    binding.tvTranslateContent.visible()
                    binding.tvTranslateContent.text = translate.data?.translateContent
                    textMsg.messageTranslate = translate.data?.translateContent
                    IMCore.getService(MessageService::class.java).updateMessage(message)
                }
            }
        }
    }

}