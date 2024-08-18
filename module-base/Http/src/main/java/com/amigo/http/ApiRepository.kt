package com.amigo.http

import android.os.Bundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

open class ApiRepository {

    suspend fun <T> launchRequest(request: suspend () -> ApiResponse<T>): ApiResponse<T> {
        return withContext(Dispatchers.IO) {
            val response = try {
                request()
            } catch (t: Throwable) {
                ApiClientException.build(t).toResponse()
            }
            ApiClient.handleApiResponse(response, null)
            response
        }
    }

    suspend fun <T> launchRequestTakeDialogBundle(
        request: suspend () -> ApiResponse<T>,
        bundle: Bundle? = null
    ): ApiResponse<T> {
        return withContext(Dispatchers.IO) {
            val response = try {
                request()
            } catch (t: Throwable) {
                ApiClientException.build(t).toResponse()
            }
            ApiClient.handleApiResponse(response, bundle)
            response
        }
    }
}