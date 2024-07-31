package com.cute.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.cute.chat.databinding.ItemImageLeftBinding
import com.cute.chat.view.CustomChattingAnnotation
import com.cute.chat.view.CustomChattingView
import com.cute.chat.view.DIRECTION_LEFT
import com.cute.im.bean.Msg
import com.cute.message.custom.msg.ImageMessage
import com.cute.picture.loadImage
import com.cute.picture.transformation.BlurTransformation
import com.cute.tool.dpToPx
import com.cute.uibase.gone
import com.cute.uibase.media.preview.PicturePreviewActivity
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.IStoreService
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible
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
            placeholderRes = com.cute.uibase.R.drawable.img_placehoder,
        )

        binding.root.setOnClickListener {
            PicturePreviewActivity.startPreview(it.context, arrayOf(imageMsg.url!!))
        }
    }
}