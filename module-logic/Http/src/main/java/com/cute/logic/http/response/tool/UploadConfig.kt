package com.cute.logic.http.response.tool

import com.google.gson.annotations.SerializedName

data class UploadConfig(
    @SerializedName("res_url") val resUrl: String,
    @SerializedName("file_key") val fileKey: String,
    @SerializedName("file_key_all_path") val fileUrl: String,
    @SerializedName("file_type") val fileType: Int,
    @SerializedName("aws_header") val header: HashMap<String, MutableList<String>>
)





