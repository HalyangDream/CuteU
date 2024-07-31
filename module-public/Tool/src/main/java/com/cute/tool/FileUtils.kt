package com.cute.tool

import android.net.Uri
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.IOException


object FileUtils {

    private const val PATH_SUFFIX = ".fileprovider"


    private fun getExternalStorageDirector(): String {
        val path = Environment.getExternalStorageDirectory().path + "/"
        return path
    }

    /**
     * 创建App外部存储的文件夹
     * 文件夹名称
     */
    fun createExternalFolder(directorName: String?) {
        val path = getExternalStorageDirector() + "$directorName"
        val folder = File(path)
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }


    /**
     * 判断文件是否存在
     */
    fun isFileExists(filePath: String?): Boolean {
        if (filePath.isNullOrEmpty()) return true
        val file = File(filePath)
        if (file.exists()) {
            return true
        }
        return false
    }

    /**
     * 删除文件
     */
    fun deleteFile(filePath: String?) {
        if (filePath.isNullOrEmpty()) return
        try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

//    fun getUriByFile(file: File?): Uri? {
//        if (file == null) return null
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            val authority = Activis + PATH_SUFFIX
//            FileProvider.getUriForFile(BaseApp.appContext, authority, file)
//        } else {
//            Uri.fromFile(file)
//        }
//    }
}