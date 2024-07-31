package com.cute.chat.view.im

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.chat.R
import com.cute.chat.databinding.ItemImageRightBinding
import com.cute.chat.view.CustomChattingAnnotation
import com.cute.chat.view.CustomChattingView
import com.cute.chat.view.DIRECTION_RIGHT
import com.cute.im.bean.MessageStatus
import com.cute.im.bean.Msg
import com.cute.message.custom.msg.ImageMessage
import com.cute.picture.loadImage
import com.cute.tool.dpToPx
import com.cute.uibase.gone
import com.cute.uibase.media.preview.PicturePreviewActivity
import com.cute.uibase.visible
import kotlinx.coroutines.CoroutineScope

@CustomChattingAnnotation(direction = DIRECTION_RIGHT, targetClass = ImageMessage::class)
class ImageRightView : CustomChattingView {

    private lateinit var binding: ItemImageRightBinding
    override fun setScope(scope: CoroutineScope) {

    }
    override fun onGenerateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): View {
        binding = ItemImageRightBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onBindChattingData(position: Int, message: Msg) {
        val imageMsg = message.message as ImageMessage
        val context = binding.ivContent.context
        binding.ivContent.loadImage(
            imageMsg.url!!,
            roundedCorners = 12f.dpToPx(context),
            errorRes = R.drawable.img_chat_right_placeholder,
            placeholderRes = R.drawable.img_chat_right_placeholder
        )
        binding.root.setOnClickListener {
            PicturePreviewActivity.startPreview(it.context, arrayOf(imageMsg.url!!))
        }
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
}