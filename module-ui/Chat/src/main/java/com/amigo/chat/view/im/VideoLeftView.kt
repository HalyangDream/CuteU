package com.amigo.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.chat.databinding.ItemVideoLeftBinding
import com.amigo.chat.view.CustomChattingAnnotation
import com.amigo.chat.view.CustomChattingView
import com.amigo.chat.view.DIRECTION_LEFT
import com.amigo.im.bean.Msg
import com.amigo.message.custom.msg.ImageMessage
import com.amigo.message.custom.msg.VideoMessage
import com.amigo.picture.loadImage
import com.amigo.picture.loadVideo
import com.amigo.picture.transformation.BlurTransformation
import com.amigo.tool.dpToPx
import com.amigo.uibase.gone
import com.amigo.uibase.media.preview.PicturePreviewActivity
import com.amigo.uibase.media.preview.VideoPreviewActivity
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.visible
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
                placeholderRes = com.amigo.uibase.R.drawable.img_placehoder,
            )
        }else{
            binding.ivContent.loadVideo(
                videoMessage.url!!,
                roundedCorners = 12f.dpToPx(context),
                placeholderRes = com.amigo.uibase.R.drawable.img_placehoder,
            )
        }


        binding.root.setOnClickListener {
            VideoPreviewActivity.startPreview(it.context, videoMessage.url!!)
        }
    }
}