package com.amigo.logic.http.service

import com.amigo.http.ApiResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface BehaviorService {

    @POST("/v1/behavior/followed")
    suspend fun followerUser(@Body body: RequestBody): ApiResponse<Unit>

    @POST("/v1/behavior/unfollowed")
    suspend fun unFollowerUser(@Body body: RequestBody): ApiResponse<Unit>

    @POST("/v1/behavior/block")
    suspend fun blockUser(@Body body: RequestBody): ApiResponse<Unit>

    @POST("/v1/behavior/unblock")
    suspend fun unBlockUser(@Body body: RequestBody): ApiResponse<Unit>

    @POST("/v1/behavior/report")
    suspend fun reportUser(@Body body: RequestBody): ApiResponse<Unit>

}