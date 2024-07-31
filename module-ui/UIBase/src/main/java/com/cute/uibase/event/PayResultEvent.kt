package com.cute.uibase.event

sealed class PayResultEvent {

    object PayFailureEvent : PayResultEvent()

    object PayCancelEvent : PayResultEvent()
}
