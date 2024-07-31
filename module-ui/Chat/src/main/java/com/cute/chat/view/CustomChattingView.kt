package com.cute.chat.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.baselogic.storage.UserDataStore
import com.cute.baselogic.userDataStore
import com.cute.chat.view.im.UnknownView
import com.cute.im.bean.Msg
import com.cute.im.cutom.CustomMessage
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

interface CustomChattingView {

    fun onGenerateView(context: Context, inflater: LayoutInflater, parent: ViewGroup): View
    fun onBindChattingData(position: Int, message: Msg)

    fun setScope(scope: CoroutineScope)

    fun isNotifyView(): Boolean = false

    companion object {

        private val viewTypes = mutableListOf<KClass<out CustomChattingView>>()

        fun registerViewType(vararg kClass: KClass<out CustomChattingView>) {
            for (item in kClass) {
                viewTypes.add(item)
            }
        }

        fun findView(
            context: Context,
            message: Msg
        ): CustomChattingView {
            for (viewType in viewTypes) {
                if (viewType == UnknownView::class) continue
                val target = isMatch(context, message, viewType)
                if (target != null) {
                    val constructor = target.java.constructors[0]
                    return constructor.newInstance() as CustomChattingView
                }
            }
            return UnknownView()
        }

        private fun isMatch(
            context: Context,
            message: Msg,
            targetClass: KClass<out CustomChattingView>
        ): KClass<out CustomChattingView>? {
            val customMessage = message.message ?: return null
            val annotations = targetClass.annotations
            if (annotations.isEmpty()) return null
            val annotation = annotations[0]
            if (annotation !is CustomChattingAnnotation) return null

            val targetDirection = annotation.direction
            val targetMessage = annotation.targetClass

            if (targetDirection == DIRECTION_CENTER && customMessage::class == targetMessage) {
                return targetClass
            }
            val uid = context.userDataStore.getUid()
            if (targetDirection == DIRECTION_LEFT && customMessage::class == targetMessage) {
                if (message.receiveId == "$uid") {
                    return targetClass
                }
            }
            if (targetDirection == DIRECTION_RIGHT && customMessage::class == targetMessage) {
                if (message.sendId == "$uid") {
                    return targetClass
                }
            }

            return null
        }

    }


}