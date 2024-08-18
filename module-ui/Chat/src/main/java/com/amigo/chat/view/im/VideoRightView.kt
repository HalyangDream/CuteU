package com.amigo.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.chat.databinding.ItemVideoRightBinding
import com.amigo.chat.view.CustomChattingAnnotation
import com.amigo.chat.view.CustomChattingView
import com.amigo.chat.view.DIRECTION_RIGHT
import com.amigo.im.bean.MessageStatus
import com.amigo.im.bean.Msg
import com.amigo.message.custom.msg.VideoMessage
import com.amigo.picture.loadImage
import com.amigo.picture.loadVideo
import com.amigo.tool.dpToPx
import com.amigo.uibase.gone
import com.amigo.uibase.media.preview.VideoPreviewActivity
import com.amigo.uibase.visible
import kotlinx.coroutines.CoroutineScope

@CustomChattingAnnotation(direction = DIRECTION_RIGHT, targetClass = VideoMessage::class)
class VideoRightView : CustomChattingView {

    private lateinit var binding: ItemVideoRightBinding

    override fun setScope(scope: CoroutineScope) {

    }

    override fun onGenerateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): View {
        binding = ItemVideoRightBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onBindChattingData(position: Int, message: Msg) {
        val videoMsg = message.message as VideoMessage
        if(!videoMsg.cover.isNullOrEmpty()){
            binding.ivContent.loadImage(
                videoMsg.cover!!,
                roundedCorners = 16f,
                placeholderRes = com.amigo.uibase.R.drawable.img_placehoder,
            )
        }else{
            binding.ivContent.loadVideo(
                videoMsg.url!!,
                roundedCorners = 16f,
                placeholderRes = com.amigo.uibase.R.drawable.img_placehoder,
            )
        }
        binding.root.setOnClickListener {
            VideoPreviewActivity.startPreview(it.context, videoMsg.url!!)
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