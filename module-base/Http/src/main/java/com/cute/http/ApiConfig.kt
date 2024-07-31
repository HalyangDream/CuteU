package com.cute.http

import okhttp3.Interceptor

class ApiConfig private constructor() {


    lateinit var baseUrl: String


    var list: MutableList<Interceptor> = mutableListOf()
    var readTimeout: Long = 10
    var writeTimeout: Long = 10
    var connectTimeout: Long = 10

    var listener: HandleApiResponseListener? = null


    class Builder {

        private val config: ApiConfig = ApiConfig()

        fun setBaseUrl(url: String): Builder {
            config.baseUrl = url
            return this
        }

        fun setInterceptor(vararg interceptor: Interceptor): Builder {
            config.list.addAll(interceptor)
            return this
        }

        fun setReadTimeout(time: Long): Builder {
            config.readTimeout = time
            return this
        }

        fun setWriteTimeout(time: Long): Builder {
            config.writeTimeout = time
            return this
        }

        fun setConnectTimeout(time: Long): Builder {
            config.connectTimeout = time
            return this
        }


        fun setHandleResponseListener(listener: HandleApiResponseListener): Builder {
            config.listener = listener
            return this
        }

        fun build(): ApiConfig {
            return config
        }

    }
}