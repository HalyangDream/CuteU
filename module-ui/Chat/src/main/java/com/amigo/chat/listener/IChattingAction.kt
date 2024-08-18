package com.amigo.chat.listener

interface IChattingAction {
    fun onSendTextMessage(message: String)

    fun onOpenAlbum()
}