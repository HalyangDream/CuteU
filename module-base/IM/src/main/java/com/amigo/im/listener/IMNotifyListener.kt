package com.amigo.im.listener

import com.amigo.im.cutom.CustomNotify

interface IMNotifyListener {

    fun onReceiveNotify(notify: CustomNotify)
}