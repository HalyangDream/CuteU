package com.cute.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.cute.baselogic.userDataStore
import com.cute.chat.databinding.ItemBlurImageLeftBinding
import com.cute.chat.databinding.ItemImageLeftBinding
import com.cute.chat.view.CustomChattingAnnotation
import com.cute.chat.view.CustomChattingView
import com.cute.chat.view.DIRECTION_LEFT
import com.cute.chat.viewmodel.MessageGlobalViewModel
import com.cute.im.IMCore
import com.cute.im.bean.Msg
import com.cute.im.service.MessageService
import com.cute.logic.http.model.MessageRepository
import com.cute.message.custom.msg.BlurImageMessage
import com.cute.message.custom.msg.ImageMessage
import com.cute.picture.loadImage
import com.cute.picture.transformation.BlurTransformation
import com.cute.tool.Toaster
import com.cute.tool.dpToPx
import com.cute.uibase.gone
import com.cute.uibase.media.preview.PicturePreviewActivity
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.IStoreService
import com.cute.uibase.setThrottleListener
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@CustomChattingAnnotation(direction = DIRECTION_LEFT, targetClass = BlurImageMessage::class)
class BlurImageLeftView : CustomChattingView {

    private var scope: CoroutineScope? = null
    private lateinit var binding: ItemBlurImageLeftBinding

    private var isUnlocking = false //是否正在解锁中

    override fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }

    override fun onGenerateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): View {
        binding = ItemBlurImageLeftBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onBindChattingData(position: Int, message: Msg) {
        val imageMsg = message.message as BlurImageMessage
        val context = binding.ivContent.context
        binding.ivContent.loadImage(
            imageMsg.url!!,
            roundedCorners = 12f.dpToPx(context),
            placeholderRes = com.cute.uibase.R.drawable.img_placehoder,
            blurTransformation = if (imageMsg.isBlur) BlurTransformation(
                context,
                25f,
                3f
            ) else null
        )
        if (imageMsg.isBlur) {
            binding.rlPrivate.visible()
            binding.ivVipLock.visible()
        } else {
            binding.rlPrivate.gone()
            binding.ivVipLock.gone()
        }
        binding.root.setThrottleListener {
            if (imageMsg.isBlur) {
                if (isUnlocking) {
                    Toaster.showShort(it.context, "Unlocking")
                } else {
                    unlockBlur(it.context, message, imageMsg)
                }
            } else {
                PicturePreviewActivity.startPreview(context, arrayOf(imageMsg.url!!))
            }
        }
    }

    private fun unlockBlur(
        context: Context,
        message: Msg,
        imageMsg: BlurImageMessage,
    ) {
        isUnlocking = true
        scope?.launch {
            MessageGlobalViewModel.unlockBlurImageMessage(message, imageMsg) {
                isUnlocking = false
            }
        }
        UserBehavior.setChargeSource("unlock_private")
    }
}