package com.suda.yzune.wakeupschedule.utils

import android.os.Environment
import java.io.File
import java.util.*


object ExportUtils {

    fun exportData(currentDir: String, data: String) {
        val file = File(currentDir, "课程表导出${Calendar.getInstance().timeInMillis}.wakeup_schedule")
        file.writeText(data)
    }

    fun isSdCardExist(): Boolean {
        Environment.getExternalStorageDirectory()
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

}