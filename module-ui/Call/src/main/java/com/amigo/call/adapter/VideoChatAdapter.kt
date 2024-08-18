package com.amigo.call.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.viewbinding.ViewBinding
import com.amigo.baselogic.userDataStore
import com.amigo.basic.recycler.MultiLayoutRvAdapter
import com.amigo.call.R
import com.amigo.call.databinding.ItemGiftMessageMeBinding
import com.amigo.call.databinding.ItemVideoMessageMeBinding
import com.amigo.call.databinding.ItemVideoMessageOtherBinding
import com.amigo.im.IMCore
import com.amigo.im.bean.Msg
import com.amigo.im.service.MessageService
import com.amigo.logic.http.model.ToolRepository
import com.amigo.message.custom.msg.TextMessage
import com.amigo.picture.loadImage
import com.amigo.tool.AppUtil
import com.amigo.uibase.visible
import kotlinx.coroutines.launch

class VideoChatAdapter(context: Context, val lifecycleScope: LifecycleCoroutineScope) :
    MultiLayoutRvAdapter<VideoMsg>(context) {

    private val toolRepository by lazy { ToolRepository() }

    private companion object {
        const val TEXT_ME = 0
        const val TEXT_OTHER = 1
    }

    override fun itemViewTypes(): IntArray = intArrayOf(TEXT_ME, TEXT_OTHER)

    override fun fullSpanItem(vieType: Int): Boolean = false

    override fun createHolder(vieType: Int, parent: ViewGroup): MultiHolder<out ViewBinding> {
        return when (vieType) {
            TEXT_ME -> {
                VideoMeMsgHolder(
                    mLayoutInflater.inflate(
                        R.layout.item_video_message_me,
                        parent,
                        false
                    )
                )
            }

            TEXT_OTHER -> {
                VideoOtherMsgHolder(
                    mLayoutInflater.inflate(
                        R.layout.item_video_message_other,
                        parent,
                        false
                    )
                )
            }


            else -> {
                VideoMeMsgHolder(
                    mLayoutInflater.inflate(
                        R.layout.item_video_message_me,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindItemData(
        position: Int,
        item: VideoMsg?,
        holder: MultiHolder<out ViewBinding>
    ) {
        if (item != null) {
            bindTextMessage(position, item, holder)
        }
    }


    private fun bindTextMessage(
        position: Int,
        item: VideoMsg,
        holder: MultiHolder<out ViewBinding>
    ) {
        if (getItemViewType(position) == TEXT_ME) {
            val binding = (holder as VideoMeMsgHolder).binding
            binding.tvMessageContent.text = item.content
        } else {
            val binding = (holder as VideoOtherMsgHolder).binding
            binding.tvMessageContent.text = item.content
            binding.translateBtn.setOnClickListener {
                lifecycleScope.launch {
                    val translate =
                        toolRepository.translate(
                            item.content,
                            AppUtil.getSysLocale()
                        )
                    if (translate.isSuccess) {
                        binding.translateLine.visibility = View.VISIBLE
                        binding.translateBtn.setImageResource(R.drawable.ic_call_translate_sel)
                        binding.tvMessageTranslate.visible()
                        binding.translateLine.visible()
                        binding.tvMessageTranslate.text = translate.data?.translateContent
                    }
                }
            }
        }
    }

    override fun ensureViewType(position: Int, data: List<VideoMsg>): Int {
        return when (data[position].isAnchor) {
            true -> TEXT_OTHER
            else -> TEXT_ME
        }
    }


    class VideoMeMsgHolder(view: View) : MultiHolder<ItemVideoMessageMeBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemVideoMessageMeBinding {
            return ItemVideoMessageMeBinding.bind(itemView)
        }
    }

    class VideoOtherMsgHolder(view: View) : MultiHolder<ItemVideoMessageOtherBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemVideoMessageOtherBinding {
            return ItemVideoMessageOtherBinding.bind(itemView)
        }
    }


}