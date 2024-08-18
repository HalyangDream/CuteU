package com.amigo.uibase.event

sealed class RemoteNotifyEvent {
    object PaySuccessEvent : RemoteNotifyEvent()

    object RefreshInfoEvent : RemoteNotifyEvent()
}
