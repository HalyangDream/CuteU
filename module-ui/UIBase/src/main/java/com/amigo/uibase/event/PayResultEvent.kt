package com.amigo.uibase.event

sealed class PayResultEvent {

    object PayFailureEvent : PayResultEvent()

    object PayCancelEvent : PayResultEvent()
}
