package com.cute.http

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList


class SocketClient private constructor() {


    private val socketListeners = CopyOnWriteArrayList<WeakReference<SocketListener>>()
    private var webSocket: WebSocket? = null


    companion object {

        val client: SocketClient by lazy { SocketClient() }

    }

    fun connection(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()
        webSocket = OkHttpClient().newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                for (socketListener in socketListeners) {
                    socketListener.get()?.onMessage(text)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
            }

        })
    }

    fun breakConnection() {
        webSocket?.close(999, " breakConnection ")
        webSocket = null
        socketListeners.clear()
    }


    fun addSocketListener(listener: SocketListener) {
        socketListeners.add(WeakReference(listener))
    }

    fun removeSocketListener(listener: SocketListener) {
        for (i in socketListeners.size - 1 downTo 0) {
            val item = socketListeners[i]
            if (item.get() != null && item.get() == listener) {
                socketListeners.remove(item)
                break
            }
        }
    }
}


interface SocketListener {

    fun onMessage(content: String)
}