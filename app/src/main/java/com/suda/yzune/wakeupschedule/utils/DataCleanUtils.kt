package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import java.nio.file.Files.delete
import java.nio.file.Files.isDirectory
import java.nio.file.Files.exists
import android.os.Environment.MEDIA_MOUNTED
import android.database.sqlite.SQLiteDatabase.deleteDatabase
import android.os.Environment
import java.io.File


object DataCleanUtils {
    fun cleanInternalCache(context: Context) {
        deleteFilesByDirectory(context.cacheDir)
    }

    //清除本应用所有数据库(/data/data/com.xxx.xxx/databases)
    fun cleanDatabases(context: Context) {
        deleteFilesByDirectory(File("/data/data/" + context.packageName + "/databases"))
    }

    //清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
    fun cleanSharedPreference(context: Context) {
        deleteFilesByDirectory(File("/data/data/" + context.packageName + "/shared_prefs"))
    }

    //按名字清除本应用数据库
    fun cleanDatabaseByName(context: Context, dbName: String) {
        context.deleteDatabase(dbName)
    }

    //清除/data/data/com.xxx.xxx/files下的内容
    fun cleanFiles(context: Context) {
        deleteFilesByDirectory(context.filesDir)
    }

    //清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
    fun cleanExternalCache(context: Context) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            deleteFilesByDirectory(context.externalCacheDir)
        }
    }

    //清除本应用所有的数据
    fun cleanApplicationData(context: Context) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
        cleanSharedPreference(context)
        cleanFiles(context)
    }

    private fun deleteFilesByDirectory(directory: File?) {
        if (directory != null && directory.exists() && directory.isDirectory) {
            for (item in directory.listFiles()) {
                item.delete()
            }
        }
    }
}