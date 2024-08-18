package com.amigo.basic

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentLinkedQueue

abstract class BaseMVIModel<I : UserIntent, S : UserState> : BaseViewModel() {


    private var hasDispatcherState = false

    private val _userStateLiveData = MutableSharedFlow<S>(replay = 50, extraBufferCapacity = 100)

    private val stateQueue = ConcurrentLinkedQueue<S>()


    abstract fun processIntent(intent: I)

    suspend fun setState(userState: S) {
        withContext(Dispatchers.Main) {
            _userStateLiveData.emit(userState)
        }
    }

    fun observerState(observer: (S) -> Unit) {
        viewModelScope.launch {
            _userStateLiveData
                .collect {
                    withContext(Dispatchers.Main) {
                        observer.invoke(it)
                    }
                }
        }
    }
}