package com.amigo.logic.http.model

import android.provider.MediaStore.Video
import android.util.Log
import com.amigo.http.ApiClient
import com.amigo.http.ApiRepository
import com.amigo.logic.http.HttpCommonParam
import com.amigo.logic.http.HttpCommonParam.toRequestBody
import com.amigo.logic.http.response.tool.UploadConfig
import com.amigo.logic.http.response.tool.UploadResult
import com.amigo.logic.http.service.ToolService
import com.amigo.tool.Toaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object UploadRepository : ApiRepository() {

    private val _service by lazy { ApiClient.getService(ToolService::class.java) }


    private suspend fun getAwsConfig(type: Int, name: String): UploadConfig? {
        val param = HttpCommonParam.getCommonParam().apply {
            put("file_type", type)
            put("file_name", name)
        }
        val response = launchRequest { _service.getUploadConfig(param.toRequestBody()) }
        return response.data
    }


    suspend fun uploadPicture(picture: File, uploadFileName: String): UploadResult {
        val config =
            getAwsConfig(1, uploadFileName) ?: return UploadResult.Failure("Aws Config Null")

        val header = hashMapOf<String, String>()
        config.header.entries.forEach {
            header[it.key] = it.value[0]
        }
        val extension = picture.extension.ifEmpty { "jpg" }
        header["Content-Type"] = "image/$extension"
        val response = withContext(Dispatchers.IO) {
            val fileBody = picture.asRequestBody("multipart/form-data".toMediaType())
            try {
                _service.uploadFile(header, config.resUrl, fileBody)
            } catch (t: Throwable) {
                null
            }
        }

        if (response != null && response.isSuccessful) {
            return UploadResult.Success(config.fileUrl, config.fileKey)
        } else {
            return UploadResult.Failure("${response?.code()}-${response?.message()}")
        }
    }


    suspend fun uploadVideo(video: File, uploadFileName: String): UploadResult {
        val config =
            getAwsConfig(2, uploadFileName) ?: return UploadResult.Failure("Aws Config Null")
        val header = hashMapOf<String, String>()
        config.header.entries.forEach {
            header[it.key] = it.value[0]
        }
        val extension = video.extension.ifEmpty { "mp4" }
        header["Content-Type"] = "video/$extension"
        val response = withContext(Dispatchers.IO) {
            val fileBody = video.asRequestBody("multipart/form-data".toMediaType())
            try {
                _service.uploadFile(header, config.resUrl, fileBody)
            } catch (t: Throwable) {
                null
            }
        }
        if (response != null && response.isSuccessful) {
            return UploadResult.Success(config.fileUrl, config.fileKey)
        } else {
            return UploadResult.Failure("${response?.code()}-${response?.message()}")
        }
    }

    suspend fun uploadAudio(picture: File, uploadFileName: String): UploadResult {
        val config =
            getAwsConfig(3, uploadFileName) ?: return UploadResult.Failure("Aws Config Null")
        val header = hashMapOf<String, String>()
        config.header.entries.forEach {
            header[it.key] = it.value[0]
        }
        val extension = picture.extension.ifEmpty { "aac" }
        header["Content-Type"] = "audio/$extension"
        val fileBody = picture.asRequestBody("multipart/form-data".toMediaType())
        val response = withContext(Dispatchers.IO) {
            try {
                _service.uploadFile(header, config.resUrl, fileBody)
            } catch (t: Throwable) {
                null
            }
        }
        if (response != null && response.isSuccessful) {
            return UploadResult.Success(config.fileUrl, config.fileKey)
        } else {
            return UploadResult.Failure("${response?.code()}-${response?.message()}")
        }
    }

}