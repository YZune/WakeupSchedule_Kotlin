package com.suda.yzune.wakeupschedule.utils

import java.security.MessageDigest


object Utils {

    fun getMD5Str(str: String): String {
        val md5StrBuff = StringBuffer()
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.reset()
            messageDigest.update(str.toByteArray(charset("UTF-8")))
            val byteArray = messageDigest.digest()
            for (i in byteArray.indices) {
                if (Integer.toHexString(0xFF and byteArray[i].toInt()).length == 1)
                    md5StrBuff.append("0").append(
                            Integer.toHexString(0xFF and byteArray[i].toInt()))
                else
                    md5StrBuff.append(Integer.toHexString(0xFF and byteArray[i].toInt()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return md5StrBuff.toString()
    }

}