package com.cute.tool

import java.security.MessageDigest

object SecretUtil {


    fun md5(params: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            md.update(params.toByteArray(charset("utf-8")))
            val buf = StringBuffer()
            for (b in md.digest()) {
                buf.append(String.format("%02x", b.toInt() and 0xff))
            }
            return buf.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

}