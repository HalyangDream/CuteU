package com.cute.logic.http.service

import com.cute.http.ApiResponse
import com.cute.logic.http.response.profile.AccountInfo
import com.cute.logic.http.response.profile.Balance
import com.cute.logic.http.response.profile.Profile
import com.cute.logic.http.response.profile.ProfileAlbum
import com.cute.logic.http.response.profile.ProfileDetail
import com.cute.logic.http.response.profile.TagData
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProfileService {


    @POST("/v1/profile/tag_list")
    suspend fun getTagList(@Body body: RequestBody): ApiResponse<TagData>

    @POST("/v1/profile/info")
    suspend fun getProfileInfo(@Body body: RequestBody): ApiResponse<Profile>

    @POST("/v1/profile/detail")
    suspend fun getProfileDetail(@Body body: RequestBody): ApiResponse<ProfileDetail>

    @POST("/v1/profile/update")
    suspend fun editProfileInfo(@Body body: RequestBody): ApiResponse<Unit>

    @POST("/v1/profile/album/delete")
    suspend fun deleteAlbum(@Body body: RequestBody): ApiResponse<Unit>

    @POST("/v1/profile/album/upload")
    suspend fun uploadAlbum(@Body body: RequestBody): ApiResponse<ProfileAlbum>

    @POST("/v1/profile/info/account")
    suspend fun accountInfo(@Body body: RequestBody): ApiResponse<AccountInfo>

    @POST("/v1/profile/info/balance")
    suspend fun getBalance(@Body body: RequestBody): ApiResponse<Balance>

    @POST("/v1/profile/destroy")
    suspend fun accountDestroy(@Body body: RequestBody): ApiResponse<Unit>
}