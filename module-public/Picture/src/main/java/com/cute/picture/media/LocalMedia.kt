package com.cute.picture.media

import android.graphics.Bitmap
import android.net.Uri

data class LocalMedia(
    val id: Long,
    val name: String,
    val size: Long,
    val uri: Uri,
    val thumbBitmap: Bitmap?,
    val path: String,
    val height: Int,
    val width: Int,
    val bucketId: Long,
    val bucket: String,
    val mineType: String
)