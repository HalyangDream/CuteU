package com.cute.tool

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

object EventBus {

    private val _events = MutableSharedFlow<Any>(replay = 0, extraBufferCapacity = 50)

    val event = _events.asSharedFlow()

    fun post(event: Any) {
        runBlocking { _events.emit(event) }
    }

    inline fun <reified T> Flow<Any>.subscribe(
        scope: CoroutineScope,
        crossinline onEvent: suspend (T) -> Unit
    ) {
        this.filter { it is T }
            .map { it as T }
            .onEach { onEvent(it) }.launchIn(scope)
    }
}