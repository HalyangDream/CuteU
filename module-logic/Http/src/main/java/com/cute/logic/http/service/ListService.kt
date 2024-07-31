package com.cute.logic.http.service

import com.cute.http.ApiResponse
import com.cute.logic.http.response.list.BlackListResponse
import com.cute.logic.http.response.list.FeedResponse
import com.cute.logic.http.response.list.FilterCondition
import com.cute.logic.http.response.list.LikeMeResponse
import com.cute.logic.http.response.list.MatchOptionResponse
import com.cute.logic.http.response.list.MyLikeResponse
import com.cute.logic.http.response.list.ReportReasonResponse
import com.cute.logic.http.response.list.VideoListResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ListService {

    @POST("/v1/list/match_option")
    suspend fun matchOption(@Body body: RequestBody): ApiResponse<MatchOptionResponse>

    @POST("/v1/list/feed")
    suspend fun getFeedList(@Body body: RequestBody): ApiResponse<FeedResponse>

    @POST("/v1/list/filter_condition")
    suspend fun getFilterCondition(@Body body: RequestBody): ApiResponse<FilterCondition>

    @POST("/v1/list/video")
    suspend fun getVideoList(@Body body: RequestBody): ApiResponse<VideoListResponse>

    @POST("/v1/list/follower")
    suspend fun getMyLikeList(@Body body: RequestBody): ApiResponse<MyLikeResponse>

    @POST("/v1/list/followed")
    suspend fun getLikeMeList(@Body body: RequestBody): ApiResponse<LikeMeResponse>


    @POST("/v1/list/black")
    suspend fun getBlackList(@Body body: RequestBody): ApiResponse<BlackListResponse>

    @POST("/v1/list/report_reason")
    suspend fun reportReasonList(@Body body: RequestBody): ApiResponse<ReportReasonResponse>


}