package com.amigo.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.amigo.chat.databinding.ItemImageLeftBinding
import com.amigo.chat.view.CustomChattingAnnotation
import com.amigo.chat.view.CustomChattingView
import com.amigo.chat.view.DIRECTION_LEFT
import com.amigo.im.bean.Msg
import com.amigo.message.custom.msg.ImageMessage
import com.amigo.picture.loadImage
import com.amigo.picture.transformation.BlurTransformation
import com.amigo.tool.dpToPx
import com.amigo.uibase.gone
import com.amigo.uibase.media.preview.PicturePreviewActivity
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.IStoreService
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.visible
import kotlinx.coroutines.CoroutineScope

@CustomChattingAnnotation(direction = DIRECTION_LEFT, targetClass = ImageMessage::class)
class ImageLeftView : CustomChattingView {

    private lateinit var binding: ItemImageLeftBinding

    override fun setScope(scope: CoroutineScope) {

    }

    override fun onGenerateView(
        context: Context, inflater: LayoutInflater, parent: ViewGroup
    ): View {
        binding = ItemImageLeftBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onBindChattingData(position: Int, message: Msg) {
        val imageMsg = message.message as ImageMessage
        val context = binding.ivContent.context
        binding.ivContent.loadImage(
            imageMsg.url!!,
            roundedCorners = 12f.dpToPx(context),
            placeholderRes = com.amigo.uibase.R.drawable.img_placehoder,
        )

        binding.root.setOnClickListener {
            PicturePreviewActivity.startPreview(it.context, arrayOf(imageMsg.url!!))
        }
    }
}