package com.amigo.main.notify

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.amigo.main.R
import com.amigo.main.databinding.NotifyAppMessageBinding
import com.amigo.picture.loadImage
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.ITelephoneService
import com.amigo.uibase.setThrottleListener
import com.amigo.uibase.userbehavior.UserBehavior

class AppMessageNotifyView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {


    private val binding: NotifyAppMessageBinding
    private val telephoneService by lazy { RouteSdk.findService(ITelephoneService::class.java) }
    var target: Any? = null
        private set
    private var listener: AppMessageNotifyListener? = null

    init {
        val view = View.inflate(context, R.layout.notify_app_message, this)
        binding = NotifyAppMessageBinding.bind(view)
        binding.clContent.setThrottleListener {
            val tag =binding.btnBehavior.tag
            if (tag != null && tag is AppMessage && !telephoneService.isCalling()) {
                val target = tag as AppMessage
                listener?.onClick(target)
            }
        }
        binding.btnBehavior.setThrottleListener {
            if (it.tag != null && it.tag is AppMessage && !telephoneService.isCalling()) {
                val target = it.tag as AppMessage
                listener?.onClick(target)
            }
        }
    }

    private var downY: Float = 0f
    private val swipeThreshold = 100


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = event.y
            }

            MotionEvent.ACTION_UP -> {
                val deltaY = event.y - downY
                if (event.y < downY && kotlin.math.abs(deltaY) > swipeThreshold) {
                    // 当向上滑动时，隐藏View
                    listener?.onGestureRemove()
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }


    fun setupNotifyUi(message: AppMessage) {
        binding.ivAvatar.loadImage(
            message.avatar,
            isCircle = true,
            placeholderRes = com.amigo.uibase.R.drawable.img_placehoder_round,
            errorRes = com.amigo.uibase.R.drawable.img_placehoder_round
        )
        binding.tvTitle.text = message.title
        binding.tvContent.text = message.content
        binding.btnBehavior.text = getBehaviorText(message.notifyType)
        binding.btnBehavior.tag = message
    }


    fun setTarget(obj: Any?) {
        this.target = obj
    }

    fun setAppMessageNotifyListener(listener: AppMessageNotifyListener?) {
        this.listener = listener
    }

    private fun getBehaviorText(type: Int): String {
        return when (type) {
            AppMessageEnum.MESSAGE.value -> "Reply"
//            AppMessageEnum.LIKE.value -> "Chat"
//            AppMessageEnum.MATCH.value -> "Chat"
            else -> "Go"
        }
    }

    interface AppMessageNotifyListener {
        fun onClick(message: AppMessage)

        fun onGestureRemove()
    }

}