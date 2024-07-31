package com.cute.uibase.event

sealed class RemoteNotifyEvent {
    object PaySuccessEvent : RemoteNotifyEvent()

    object RefreshInfoEvent : RemoteNotifyEvent()
}
