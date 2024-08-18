package com.amigo.im.bean

enum class MessageStatus(val status: Int) {

    SUCCESS(2),
    FAIL(0),
    SENDING(1)
}