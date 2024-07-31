package com.cute.chat.listener

interface IChattingAction {
    fun onSendTextMessage(message: String)

    fun onOpenAlbum()
}