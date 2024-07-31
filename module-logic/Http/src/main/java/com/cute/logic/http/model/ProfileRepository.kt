package com.cute.logic.http.model

import com.cute.http.ApiClient
import com.cute.http.ApiRepository
import com.cute.http.ApiResponse
import com.cute.logic.http.HttpCommonParam
import com.cute.logic.http.HttpCommonParam.toRequestBody
import com.cute.logic.http.response.profile.AccountInfo
import com.cute.logic.http.response.profile.Balance
import com.cute.logic.http.response.profile.Profile
import com.cute.logic.http.response.profile.ProfileAlbum
import com.cute.logic.http.response.profile.ProfileDetail
import com.cute.logic.http.response.profile.TagData
import com.cute.logic.http.service.ProfileService
import org.json.JSONArray

class ProfileRepository : ApiRepository() {

    private val service by lazy { ApiClient.getService(ProfileService::class.java) }

    suspend fun getProfileInfo(): ApiResponse<Profile> {
        return launchRequest { service.getProfileInfo(HttpCommonParam.getCommonParam().toRequestBody()) }
    }

    suspend fun getTagList(): ApiResponse<TagData> {
        return launchRequest { service.getTagList(HttpCommonParam.getCommonParam().toRequestBody()) }
    }

    suspend fun getProfileDetail(): ApiResponse<ProfileDetail> {
        return launchRequest { service.getProfileDetail(HttpCommonParam.getCommonParam().toRequestBody()) }
    }

    suspend fun deleteAlbum(resourceId: Int, isVideo: Boolean): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("resource_id", resourceId)
            put("is_video", isVideo)
        }
        return launchRequest {
            service.deleteAlbum(param.toRequestBody())
        }
    }

    suspend fun uploadAlbum(url: String, isVideo: Boolean): ApiResponse<ProfileAlbum> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("url", url)
            put("is_video", isVideo)
        }
        return launchRequest {
            service.uploadAlbum(param.toRequestBody())
        }
    }

    suspend fun updateTag(tagIdList: MutableList<String>): ApiResponse<Unit> {
        val jsonArray = JSONArray()
        if (tagIdList.isNotEmpty()) {
            tagIdList.forEach {
                jsonArray.put(it)
            }
        }
        val param = HttpCommonParam.getCommonParam().apply {
            put("tags", jsonArray)
        }
        return launchRequest {
            service.editProfileInfo(param.toRequestBody())
        }
    }

    suspend fun updateNickName(nickName: String): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("nick_name", nickName)
        }
        return launchRequest {
            service.editProfileInfo(param.toRequestBody())
        }
    }

    suspend fun updateAvatar(avatar: String): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("avatar", avatar)
        }
        return launchRequest {
            service.editProfileInfo(param.toRequestBody())
        }
    }


    suspend fun updateGender(gender: Int): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("gender", gender)
        }
        return launchRequest {
            service.editProfileInfo(param.toRequestBody())
        }
    }

    suspend fun updateHeight(height: String): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("height", height)
        }
        return launchRequest {
            service.editProfileInfo(param.toRequestBody())
        }
    }

    suspend fun updateAge(age: Int): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("age", age)
        }
        return launchRequest {
            service.editProfileInfo(param.toRequestBody())
        }
    }

    suspend fun updateIntroduction(sign: String): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("sign", sign)
        }
        return launchRequest {
            service.editProfileInfo(param.toRequestBody())
        }
    }


    suspend fun getBalance(): ApiResponse<Balance> {
        return launchRequest {
            service.getBalance(HttpCommonParam.getCommonParam().toRequestBody())
        }
    }


    suspend fun getAccountInfo(): ApiResponse<AccountInfo> {
        return launchRequest { service.accountInfo(HttpCommonParam.getCommonParam().toRequestBody()) }
    }

    suspend fun destroyAccount(): ApiResponse<Unit> {
        return launchRequest {
            service.accountDestroy(HttpCommonParam.getCommonParam().toRequestBody())
        }
    }

}