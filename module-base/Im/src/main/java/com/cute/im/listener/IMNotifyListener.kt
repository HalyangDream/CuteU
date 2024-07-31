package com.cute.im.listener

import com.cute.im.cutom.CustomNotify

interface IMNotifyListener {

    fun onReceiveNotify(notify: CustomNotify)
}