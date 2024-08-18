package com.amigo.chat.view

import com.amigo.im.cutom.CustomMessage
import kotlin.reflect.KClass

const val DIRECTION_CENTER = 0
const val DIRECTION_LEFT = 1
const val DIRECTION_RIGHT = 2
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomChattingAnnotation(
    val direction: Int,
    val targetClass: KClass<out CustomMessage>
)
