package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import okhttp3.Call

object UpdateUtils {

    @Throws(Exception::class)
    fun getVersionCode(context: Context): Int {
        //获取packagemanager的实例
        val packageManager = context.packageManager
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        val packInfo = packageManager.getPackageInfo(context.packageName, 0)
        return packInfo.versionCode
    }

    @Throws(Exception::class)
    fun getVersionName(context: Context): String {
        //获取packagemanager的实例
        val packageManager = context.packageManager
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        val packInfo = packageManager.getPackageInfo(context.packageName, 0)
        return packInfo.versionName
    }
}