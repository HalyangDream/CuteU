package com.amigo.picture.media

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object MediaDatabase {


    suspend fun getAllImages(contentResolver: ContentResolver): MutableList<LocalMedia> {
        return withContext(Dispatchers.IO) {
            val _data = mutableListOf<LocalMedia>()
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.MIME_TYPE
            )
            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )
            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val widthColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val mineTypeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                val bucketIdColumn =
                    it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val bucketColumn =
                    it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val size = it.getLong(sizeColumn)
                    val path = it.getString(dataColumn)
                    val bucketId = it.getLong(bucketIdColumn)
                    val bucket = it.getString(bucketColumn)
                    val width = it.getInt(widthColumn)
                    val height = it.getInt(heightColumn)
                    val mineType = it.getString(mineTypeColumn)
                    // 用于访问图片的Uri
                    val uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    // 处理图片数据
                    _data.add(
                        LocalMedia(
                            id,
                            name,
                            size,
                            uri,
                            getThumbnails(contentResolver, id, uri),
                            path,
                            height,
                            width,
                            bucketId,
                            bucket,
                            mineType
                        )
                    )
                }
            }
            _data
        }
    }


    private fun getThumbnails(contentResolver: ContentResolver, id: Long, uri: Uri): Bitmap? {

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentResolver.loadThumbnail(uri, Size(240, 240), null)
            } else {
                MediaStore.Images.Thumbnails.getThumbnail(
                    contentResolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, null
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

}