package com.cute.im.cutom

abstract class CustomNotify {

    abstract fun parseJson(json: String?)

    abstract fun notifyType(): Int
}