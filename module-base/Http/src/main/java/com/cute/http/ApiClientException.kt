package com.cute.http

import com.google.gson.JsonParseException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiClientException(
    val code: String, override val message: String,
    override val cause: Throwable? = null
) : Exception() {


    companion object {
        // 网络状态码
        const val CODE_NET_ERROR = -4000
        const val CODE_TIMEOUT = -4010
        const val CODE_JSON_PARSE_ERROR = -4020
        const val CODE_SERVER_ERROR = -4030

        // 业务状态码
        const val CODE_AUTH_INVALID = 401

        fun build(e: Throwable): ApiClientException {
            return if (e is HttpException) {
                ApiClientException("$CODE_NET_ERROR", "Network anomaly(${e.code()},${e.message()})")
            } else if (e is UnknownHostException) {
                ApiClientException("$CODE_NET_ERROR", "Network connection failed, please check and try again")
            } else if (e is ConnectTimeoutException || e is SocketTimeoutException) {
                ApiClientException("$CODE_TIMEOUT", "Request timed out, please try again later")
            } else if (e is IOException) {
                ApiClientException("$CODE_NET_ERROR", "Network anomaly(${e.message})")
            } else if (e is JsonParseException || e is JSONException) {
                // Json解析失败
                ApiClientException("$CODE_JSON_PARSE_ERROR", "Data parsing error, please try again later")
            } else {
                ApiClientException("$CODE_SERVER_ERROR", "System error(${e.message})")
            }
        }
    }

    fun <T> toResponse(): ApiResponse<T> {
        return ApiResponse(code, message, false, true, null)
    }

}