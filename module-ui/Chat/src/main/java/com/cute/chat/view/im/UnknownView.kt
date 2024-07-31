package com.cute.chat.view.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.chat.databinding.ItemUnknownCenterBinding
import com.cute.chat.view.CustomChattingAnnotation
import com.cute.chat.view.CustomChattingView
import com.cute.chat.view.DIRECTION_CENTER
import com.cute.im.bean.Msg
import com.cute.im.cutom.CustomMessage
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