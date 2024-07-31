package com.cute.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.chat.databinding.ItemVideoLeftBinding
import com.cute.chat.view.CustomChattingAnnotation
import com.cute.chat.view.CustomChattingView
import com.cute.chat.view.DIRECTION_LEFT
import com.cute.im.bean.Msg
import com.cute.message.custom.msg.ImageMessage
import com.cute.message.custom.msg.VideoMessage
import com.cute.picture.loadImage
import com.cute.picture.loadVideo
import com.cute.picture.transformation.BlurTransformation
import com.cute.tool.dpToPx
import com.cute.uibase.gone
import com.cute.uibase.media.preview.PicturePreviewActivity
import com.cute.uibase.media.preview.VideoPreviewActivity
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible
import kotlinx.coroutines.CoroutineScope

@CustomChattingAnnotation(direction = DIRECTION_LEFT, targetClass = VideoMessage::class)
class VideoLeftView : CustomChattingView {

    private lateinit var binding: ItemVideoLeftBinding
    override fun setScope(scope: CoroutineScope) {

    }
    override fun onGenerateView(
        context: Context, inflater: LayoutInflater, parent: ViewGroup
    ): View {
        binding = ItemVideoLeftBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onBindChattingData(position: Int, message: Msg) {
        val videoMessage = message.message as VideoMessage
        val context = binding.ivContent.context

        if(!videoMessage.cover.isNullOrEmpty()){
            binding.ivContent.loadImage(
                videoMessage.cover!!,
                roundedCorners = 12f.dpToPx(context),
                placeholderRes = com.cute.uibase.R.drawable.img_placehoder,
            )
        }else{
            binding.ivContent.loadVideo(
                videoMessage.url!!,
                roundedCorners = 12f.dpToPx(context),
                placeholderRes = com.cute.uibase.R.drawable.img_placehoder,
            )
        }


        binding.root.setOnClickListener {
            VideoPreviewActivity.startPreview(it.context, videoMessage.url!!)
        }
    }
}