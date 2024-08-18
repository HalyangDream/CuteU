package com.amigo.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.amigo.baselogic.userDataStore
import com.amigo.chat.databinding.ItemBlurImageLeftBinding
import com.amigo.chat.databinding.ItemImageLeftBinding
import com.amigo.chat.view.CustomChattingAnnotation
import com.amigo.chat.view.CustomChattingView
import com.amigo.chat.view.DIRECTION_LEFT
import com.amigo.chat.viewmodel.MessageGlobalViewModel
import com.amigo.im.IMCore
import com.amigo.im.bean.Msg
import com.amigo.im.service.MessageService
import com.amigo.logic.http.model.MessageRepository
import com.amigo.message.custom.msg.BlurImageMessage
import com.amigo.message.custom.msg.ImageMessage
import com.amigo.picture.loadImage
import com.amigo.picture.transformation.BlurTransformation
import com.amigo.tool.Toaster
import com.amigo.tool.dpToPx
import com.amigo.uibase.gone
import com.amigo.uibase.media.preview.PicturePreviewActivity
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.IStoreService
import com.amigo.uibase.setThrottleListener
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.visible
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
            placeholderRes = com.amigo.uibase.R.drawable.img_placehoder,
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