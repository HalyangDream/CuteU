package com.amigo.message.custom.notify

import com.amigo.im.cutom.CustomNotify

class RatingDialogNotify : CustomNotify() {


    override fun parseJson(json: String?) {
    }

    override fun notifyType(): Int = 3
}