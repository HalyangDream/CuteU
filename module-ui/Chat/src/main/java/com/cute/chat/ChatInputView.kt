package com.cute.chat

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import com.cute.chat.databinding.LayoutChatBottomBinding
import com.cute.tool.Toaster
import com.cute.uibase.gone

class ChatInputView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {


    private val binding: LayoutChatBottomBinding
    private lateinit var activity: ChatActivity
    private var isEditPressDown = false

    init {
        val mView = View.inflate(context, R.layout.layout_chat_bottom, this)
        binding = LayoutChatBottomBinding.bind(mView)
        initView()
    }

    fun setActivity(activity: ChatActivity) {
        this.activity = activity
    }


    private fun initView() {
        binding.ivSend.isEnabled = false
        binding.etInput.addTextChangedListener(afterTextChanged = {
            val content = it.toString()
            if (content.isEmpty()) {
                binding.ivSend.isEnabled = false
                binding.ivSend.setImageResource(R.drawable.ic_chat_send_disable)
            } else {
                binding.ivSend.isEnabled = true
                binding.ivSend.setImageResource(R.drawable.ic_chat_send_enable)
            }
        })
        binding.ivSend.setOnClickListener {
            val content = binding.etInput.text.toString()
            if (content.isNotEmpty()) {
                //发送消息
                activity.onSendTextMessage(content)
                binding.etInput.text?.clear()
            }
        }

        binding.ivPicture.setOnClickListener {
            //打开相册
            activity.requestMediaPermission(onDenied = {
                Toaster.showShort(context, com.cute.uibase.R.string.str_please_grant_permission)
            }, onAllGranted = {
                activity.onOpenAlbum()
            })
        }
    }
}