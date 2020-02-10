package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.security.MessageDigest


object Utils {

    fun openUrl(context: Context, url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val contentUrl = Uri.parse(url)
        intent.data = contentUrl
        context.startActivity(intent)
    }

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