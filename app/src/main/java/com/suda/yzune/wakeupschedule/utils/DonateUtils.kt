package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder

object DonateUtils {

    fun isAppInstalled(context: Context, pkgName: String): Boolean {
        val packageManager = context.packageManager
        val pInfo = packageManager.getInstalledPackages(0)
        if (pInfo != null) {
            for (i in pInfo.indices) {
                val pn = pInfo[i].packageName
                if (pn == pkgName) {
                    return true
                }
            }
        }
        return false
    }
}