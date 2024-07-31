package com.cute.im.annotation

import kotlin.reflect.KClass

/**
 * author : mac
 * date   : 2022/5/11
 *
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class IMService(val impl:KClass<*>)
