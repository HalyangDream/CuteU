package com.cute.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.chat.databinding.ItemVideoRightBinding
import com.cute.chat.view.CustomChattingAnnotation
import com.cute.chat.view.CustomChattingView
import com.cute.chat.view.DIRECTION_RIGHT
import com.cute.im.bean.MessageStatus
import com.cute.im.bean.Msg
import com.cute.message.custom.msg.VideoMessage
import com.cute.picture.loadImage
import com.cute.picture.loadVideo
import com.cute.tool.dpToPx
import com.cute.uibase.gone
import com.cute.uibase.media.preview.VideoPreviewActivity
import com.cute.uibase.visible
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
                placeholderRes = com.cute.uibase.R.drawable.img_placehoder,
            )
        }else{
            binding.ivContent.loadVideo(
                videoMsg.url!!,
                roundedCorners = 16f,
                placeholderRes = com.cute.uibase.R.drawable.img_placehoder,
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