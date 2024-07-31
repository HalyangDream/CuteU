package com.cute.logic.http.model

import com.cute.http.ApiClient
import com.cute.http.ApiRepository
import com.cute.http.ApiResponse
import com.cute.logic.http.HttpCommonParam
import com.cute.logic.http.HttpCommonParam.toRequestBody
import com.cute.logic.http.response.list.BlackListResponse
import com.cute.logic.http.response.list.FeedResponse
import com.cute.logic.http.response.list.FilterCondition
import com.cute.logic.http.response.list.LikeMeResponse
import com.cute.logic.http.response.list.MatchOptionResponse
import com.cute.logic.http.response.list.MyLikeResponse
import com.cute.logic.http.response.list.ReportReasonResponse
import com.cute.logic.http.response.list.VideoListResponse
import com.cute.logic.http.service.ListService

class ListRepository : ApiRepository() {


    private val service by lazy { ApiClient.getService(ListService::class.java) }


    suspend fun getMatchOption(): ApiResponse<MatchOptionResponse> {
        return launchRequest {
            service.matchOption(
                HttpCommonParam.getCommonParam().toRequestBody()
            )
        }
    }


    suspend fun getFilterCondition(): ApiResponse<FilterCondition> {
        return launchRequest {
            service.getFilterCondition(
                HttpCommonParam.getCommonParam().toRequestBody()
            )
        }
    }

    suspend fun getFeedList(page: Int): ApiResponse<FeedResponse> {
        val params = HttpCommonParam.getCommonParam().apply {
            put("page", page)
        }
        return launchRequest { service.getFeedList(params.toRequestBody()) }
    }


    suspend fun getVideoList(
        page: Int,
        feeling: String?,
        language: String?,
        region: String?,
        country: String?
    ): ApiResponse<VideoListResponse> {
        val params = HttpCommonParam.getCommonParam().apply {
            put("page", page)
            if (!feeling.isNullOrEmpty()) {
                put("feeling", feeling)
            }
            if (!language.isNullOrEmpty()) {
                put("language", language)
            }
            if (!region.isNullOrEmpty()) {
                put("region", region)
            }
            if (!country.isNullOrEmpty()) {
                put("country", country)
            }
        }
        return launchRequest {
            service.getVideoList(params.toRequestBody())
        }

    }


    suspend fun getMyLikeList(page: Int): ApiResponse<MyLikeResponse> {
        val params = HttpCommonParam.getCommonParam().apply {
            put("page", page)
        }
        return launchRequest { service.getMyLikeList(params.toRequestBody()) }
    }

    suspend fun getLikeMeList(page: Int): ApiResponse<LikeMeResponse> {
        val params = HttpCommonParam.getCommonParam().apply {
            put("page", page)
        }
        return launchRequest { service.getLikeMeList(params.toRequestBody()) }
    }


    suspend fun getBlackList(page: Int): ApiResponse<BlackListResponse> {
        val params = HttpCommonParam.getCommonParam().apply {
            put("page", page)
        }
        return launchRequest { service.getBlackList(params.toRequestBody()) }
    }

    suspend fun reportReasonList(): ApiResponse<ReportReasonResponse> {
        return launchRequest {
            service.reportReasonList(
                HttpCommonParam.getCommonParam().toRequestBody()
            )
        }
    }
}