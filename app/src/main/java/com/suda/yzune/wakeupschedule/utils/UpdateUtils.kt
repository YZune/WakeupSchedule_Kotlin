package com.suda.yzune.wakeupschedule.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.TableBean
import kotlin.concurrent.thread

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

    fun tranOldData(context: Context): LiveData<Int> {
        val tranInfo = MutableLiveData<Int>()
        if (PreferenceUtils.getStringFromSP(context.applicationContext, "termStart", "2018-09-03") != "") {
            val tableData = TableBean(
                    tableName = "",
                    itemHeight = PreferenceUtils.getIntFromSP(context.applicationContext, "item_height", 56),
                    maxWeek = PreferenceUtils.getIntFromSP(context.applicationContext, "sb_weeks", 30),
                    itemTextSize = PreferenceUtils.getIntFromSP(context.applicationContext, "sb_text_size", 12),
                    showOtherWeekCourse = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show", false),
                    showTime = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show_time_detail", false),
                    showSat = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show_sat", true),
                    showSun = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show_weekend", true),
                    sundayFirst = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_sunday_first", false),
                    nodes = PreferenceUtils.getIntFromSP(context.applicationContext, "classNum", 11),
                    itemAlpha = PreferenceUtils.getIntFromSP(context.applicationContext, "sb_alpha", 60),
                    background = PreferenceUtils.getStringFromSP(context.applicationContext, "pic_uri", "")!!,
                    startDate = PreferenceUtils.getStringFromSP(context.applicationContext, "termStart", "2018-09-03")!!,
                    widgetItemAlpha = PreferenceUtils.getIntFromSP(context.applicationContext, "sb_widget_alpha", 60),
                    widgetItemHeight = PreferenceUtils.getIntFromSP(context.applicationContext, "widget_item_height", 56),
                    widgetItemTextSize = PreferenceUtils.getIntFromSP(context.applicationContext, "sb_widget_text_size", 12),
                    type = 1,
                    id = 0)

            if (!PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_stroke", true)) {
                tableData.strokeColor = "#00ffffff"
            }

            if (PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_color", false)) {
                tableData.textColor = "#000000"
            }

            if (PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_widget_color", false)) {
                tableData.widgetTextColor = "#000000"
            }

            val dataBase = AppDatabase.getDatabase(context)
            val tableDao = dataBase.tableDao()

            thread(name = "tranOldDataThread") {
                try {
                    tableDao.insertTable(tableData)
                    PreferenceUtils.remove(context.applicationContext, "termStart")
                    PreferenceUtils.remove(context.applicationContext, "item_height")
                    PreferenceUtils.remove(context.applicationContext, "sb_weeks")
                    PreferenceUtils.remove(context.applicationContext, "sb_text_size")
                    PreferenceUtils.remove(context.applicationContext, "s_show")
                    PreferenceUtils.remove(context.applicationContext, "s_show_time_detail")
                    PreferenceUtils.remove(context.applicationContext, "s_show_sat")
                    PreferenceUtils.remove(context.applicationContext, "s_show_weekend")
                    PreferenceUtils.remove(context.applicationContext, "s_sunday_first")
                    PreferenceUtils.remove(context.applicationContext, "classNum")
                    PreferenceUtils.remove(context.applicationContext, "sb_alpha")
                    PreferenceUtils.remove(context.applicationContext, "pic_uri")
                    PreferenceUtils.remove(context.applicationContext, "sb_widget_alpha")
                    PreferenceUtils.remove(context.applicationContext, "widget_item_height")
                    PreferenceUtils.remove(context.applicationContext, "sb_widget_text_size")
                    PreferenceUtils.remove(context.applicationContext, "s_stroke")
                    PreferenceUtils.remove(context.applicationContext, "s_color")
                    PreferenceUtils.remove(context.applicationContext, "s_widget_color")
                    tranInfo.postValue(1)
                } catch (e: Exception) {
                    tranInfo.postValue(2)
                }
            }
        } else {
            tranInfo.value = 1
        }
        return tranInfo
    }
}