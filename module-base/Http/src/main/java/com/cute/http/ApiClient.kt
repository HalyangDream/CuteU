package com.cute.http

import android.os.Bundle
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {


    private lateinit var retrofit: Retrofit
    private var apiResponseListener: HandleApiResponseListener? = null

    fun initializationApi(isDebugMode: Boolean, config: ApiConfig) {
        initialization(isDebugMode, config)
    }

    fun <T> getService(service: Class<T>): T {
        return retrofit.create(service)
    }


    private fun initialization(isDebugMode: Boolean, config: ApiConfig) {
        retrofit = Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(
                OkHttpClient.Builder().apply {
                    for (interceptor in config.list) {
                        this.addInterceptor(interceptor)
                    }
                    this.addInterceptor(HttpLoggingInterceptor().apply {
                        this.level =
                            if (isDebugMode) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                    })
                    this.writeTimeout(config.writeTimeout, TimeUnit.SECONDS)
                    this.connectTimeout(config.connectTimeout, TimeUnit.SECONDS)
                    this.readTimeout(config.readTimeout, TimeUnit.SECONDS)
                    this.retryOnConnectionFailure(true)
                }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        this.apiResponseListener = config.listener
    }

    internal fun handleApiResponse(response: ApiResponse<*>, dialogBundle: Bundle?) {
        apiResponseListener?.onHandle(response, dialogBundle)
    }

}