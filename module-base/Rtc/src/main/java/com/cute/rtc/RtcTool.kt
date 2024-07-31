package com.cute.rtc

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * author : mac
 * date   : 2023/1/5
 * e-mail : taolei@51cashbox.com
 */
object RtcTool {

    //定义GB的计算常量
    private val GB = 1024 * 1024 * 1024

    //定义MB的计算常量
    private val MB = 1024 * 1024

    //定义KB的计算常量
    private val KB = 1024

    /**
     * 是否支持arm64
     */
    fun isSupportArm64(): Boolean {
        for (supportedAbi in Build.SUPPORTED_ABIS) {
            if (supportedAbi == "arm64-v8a") {
                return true
            }
        }
        return false
    }

    fun getArm64Abi(): String {
        return "arm64-v8a"
    }

    fun getArmAbi(): String {
        return "armeabi-v7a"
    }

    fun getLibraryRootPath(context: Context): String {
        return "${Environment.getDataDirectory().path}/data/${context.packageName}/rtc"
    }

    /**
     * 是否有动态库
     */
    fun hasDynamicLibrary(context: Context, version: String): Boolean {
        val savePath = getLibraryRootPath(context)
        //检查有没有动态库
        val file = File(savePath)
        if (!file.exists() || !file.isDirectory) {
            file.mkdirs()
        }
        val libFileDir = File("$savePath/$version")
        if (libFileDir.exists() && libFileDir.isDirectory
            && directorySize(libFileDir) > MB * 10
        ) {
            return true
        }
        return false
    }

    /**
     * 是否有动态库的压缩包
     */
    fun hasDynamicLibraryZip(context: Context, version: String): Boolean {
        val savePath = getLibraryRootPath(context)

        val libZipFile = File("$savePath/$version.zip")
        if (libZipFile.exists() && libZipFile.isFile && (libZipFile.length() * KB) > MB * 10) {
            return true
        }
        return false
    }

    /**
     * 解压动态库
     */
    fun unZipLibrary(context: Context, version: String): Boolean {
        val result = hasDynamicLibraryZip(context, version)
        if (!result) {
            return false
        }
        val savePath = getLibraryRootPath(context)
        val libFileDir = File("$savePath/$version")
        val libZipFile = File("$savePath/$version.zip")
        try {
            //解压缩
            if (libFileDir.exists()) {
                libFileDir.delete()
            }
            libFileDir.mkdirs()
            val libZip = ZipFile(libZipFile)
            val elements = libZip.entries()
            var zipEntry: ZipEntry
            while (elements.hasMoreElements()) {
                zipEntry = elements.nextElement()
                if (zipEntry.isDirectory) {
                    continue
                }
                val zipInputStream = libZip.getInputStream(zipEntry)
                val file = File(libFileDir, zipEntry.name.split("/")[1])
                file.createNewFile()
                val outputStream = FileOutputStream(file)
                val byteArray = ByteArray(4096)
                var len: Int
                while (true) {
                    len = zipInputStream.read(byteArray)
                    if (len == -1) {
                        break
                    }
                    outputStream.write(byteArray, 0, len)
                    outputStream.flush()
                }
                outputStream.close()
                zipInputStream.close()
            }
            libZip.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        } finally {
            val unZipSuccess = hasDynamicLibrary(context, version)
            if (!unZipSuccess) {
                deleteLibrary(context, version)
            }
        }
        return hasDynamicLibrary(context, version)
    }

    /**
     * 删除动态库
     */
    fun deleteLibrary(context: Context, version: String) {
        val path = getLibraryRootPath(context)
        val file = File("$path/$version")
        val zipFile = File("$path/$version.zip")
        if (file.exists() && file.isDirectory) {
            file.delete()
        }
        if (zipFile.exists() && zipFile.isFile) {
            zipFile.delete()
        }
    }

    /**
     * 获取文件夹里面内容的大小
     * @return KB
     */
    private fun directorySize(directory: File): Long {
        if (!directory.exists() || !directory.isDirectory) {
            throw IOException("directory not exists Or directory is File")
        }

        val files = directory.listFiles()
        if (files.isNullOrEmpty()) return 0
        var size: Long = 0
        for (file in files) {
            size += file.length()
        }
        return size * KB
    }

}