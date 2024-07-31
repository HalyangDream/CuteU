package com.cute.message.custom.notify

import com.cute.im.cutom.CustomNotify

class RatingDialogNotify : CustomNotify() {


    override fun parseJson(json: String?) {
    }

    override fun notifyType(): Int = 3
}