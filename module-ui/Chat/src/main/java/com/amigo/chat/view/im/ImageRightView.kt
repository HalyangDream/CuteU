package com.amigo.chat.view.im

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.chat.R
import com.amigo.chat.databinding.ItemImageRightBinding
import com.amigo.chat.view.CustomChattingAnnotation
import com.amigo.chat.view.CustomChattingView
import com.amigo.chat.view.DIRECTION_RIGHT
import com.amigo.im.bean.MessageStatus
import com.amigo.im.bean.Msg
import com.amigo.message.custom.msg.ImageMessage
import com.amigo.picture.loadImage
import com.amigo.tool.dpToPx
import com.amigo.uibase.gone
import com.amigo.uibase.media.preview.PicturePreviewActivity
import com.amigo.uibase.visible
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