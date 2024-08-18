package com.amigo.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.chat.databinding.ItemUnknownCenterBinding
import com.amigo.chat.view.CustomChattingAnnotation
import com.amigo.chat.view.CustomChattingView
import com.amigo.chat.view.DIRECTION_CENTER
import com.amigo.im.bean.Msg
import com.amigo.im.cutom.CustomMessage
import kotlinx.coroutines.CoroutineScope

@CustomChattingAnnotation(direction = DIRECTION_CENTER, targetClass = CustomMessage::class)
class UnknownView : CustomChattingView {

    override fun setScope(scope: CoroutineScope) {

    }
    override fun onGenerateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): View {
        return ItemUnknownCenterBinding.inflate(inflater, parent, false).root
    }

    override fun onBindChattingData(position: Int, message: Msg) {

    }

    override fun isNotifyView(): Boolean {
        return true
    }
}