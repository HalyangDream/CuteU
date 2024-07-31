package com.cute.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.chat.databinding.ItemBlurVideoLeftBinding
import com.cute.chat.view.CustomChattingAnnotation
import com.cute.chat.view.CustomChattingView
import com.cute.chat.view.DIRECTION_LEFT
import com.cute.chat.viewmodel.MessageGlobalViewModel
import com.cute.im.bean.Msg
import com.cute.message.custom.msg.BlurVideoMessage
import com.cute.picture.loadImage
import com.cute.picture.loadVideo
import com.cute.picture.transformation.BlurTransformation
import com.cute.tool.dpToPx
import com.cute.uibase.gone
import com.cute.uibase.media.preview.VideoPreviewActivity
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@CustomChattingAnnotation(direction = DIRECTION_LEFT, targetClass = BlurVideoMessage::class)
class BlurVideoLeftView : CustomChattingView {

    private var scope: CoroutineScope? = null
    private var isUnlocking = false
    private lateinit var binding: ItemBlurVideoLeftBinding

    override fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }

    override fun onGenerateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): View {
        binding = ItemBlurVideoLeftBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onBindChattingData(position: Int, message: Msg) {
        val videoMessage = message.message as BlurVideoMessage
        val context = binding.ivContent.context

        if(!videoMessage.cover.isNullOrEmpty()){
            binding.ivContent.loadImage(
                videoMessage.cover!!,
                roundedCorners = 12f.dpToPx(context),
                placeholderRes = com.cute.uibase.R.drawable.img_placehoder,
                blurTransformation = if (videoMessage.isBlur) BlurTransformation(
                    context,
                    25f,
                    3f
                ) else null
            )
        }else{
            binding.ivContent.loadVideo(
                videoMessage.url!!,
                roundedCorners = 12f.dpToPx(context),
                placeholderRes = com.cute.uibase.R.drawable.img_placehoder,
                blurTransformation = if (videoMessage.isBlur) BlurTransformation(
                    context,
                    25f,
                    3f
                ) else null
            )
        }

        if (videoMessage.isBlur) {
            binding.rlPrivate.visible()
            binding.ivVipLock.visible()
        } else {
            binding.rlPrivate.gone()
            binding.ivVipLock.gone()
        }
        binding.root.setOnClickListener {
            if (videoMessage.isBlur) {
                unlockBlur(it.context, message, videoMessage)
            } else {
                VideoPreviewActivity.startPreview(it.context, videoMessage.url!!)
            }
            UserBehavior.setChargeSource("unlock_private")
        }
    }

    private fun unlockBlur(
        context: Context,
        message: Msg,
        videoMsg: BlurVideoMessage,
    ) {
        isUnlocking = true
        scope?.launch {
            MessageGlobalViewModel.unlockBlurVideoMessage(message, videoMsg) {
                isUnlocking = false
            }
        }
        UserBehavior.setChargeSource("unlock_private")
    }

}