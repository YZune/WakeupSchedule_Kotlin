package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.bean.TimeTableBean

object UpdateUtils {

    @Throws(Exception::class)
    fun getVersionCode(context: Context): Int {
        val packageManager = context.packageManager
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        val packInfo = packageManager.getPackageInfo(context.packageName, 0)
        return packInfo.versionCode
    }

    @Throws(Exception::class)
    fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        val packInfo = packageManager.getPackageInfo(context.packageName, 0)
        return packInfo.versionName
    }

    fun tranOldData(context: Context) {
        if (PreferenceUtils.getBooleanFromSP(context.applicationContext, "has_intro", false) &&
                !PreferenceUtils.getBooleanFromSP(context.applicationContext, "has_adjust", false)) {
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
                    id = 1)

            if (!PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_stroke", true)) {
                tableData.strokeColor = 0x00ffffff
            }

            if (PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_color", false)) {
                tableData.textColor = 0xff000000.toInt()
            }

            if (PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_widget_color", false)) {
                tableData.widgetTextColor = 0xff000000.toInt()
            }

            val dataBase = AppDatabase.getDatabase(context)
            val tableDao = dataBase.tableDao()
            val timeDao = dataBase.timeDetailDao()
            val widgetDao = dataBase.appWidgetDao()

            try {
                tableDao.updateTable(tableData)
                widgetDao.updateFromOldVer()
                if (!PreferenceUtils.getBooleanFromSP(context.applicationContext, "isInitTimeTable", false)) {
                    val timeList = ArrayList<TimeDetailBean>().apply {
                        add(TimeDetailBean(1, "08:00", "08:50", 1))
                        add(TimeDetailBean(2, "09:00", "09:50", 1))
                        add(TimeDetailBean(3, "10:10", "11:00", 1))
                        add(TimeDetailBean(4, "11:10", "12:00", 1))
                        add(TimeDetailBean(5, "13:30", "14:20", 1))
                        add(TimeDetailBean(6, "14:30", "15:20", 1))
                        add(TimeDetailBean(7, "15:40", "16:30", 1))
                        add(TimeDetailBean(8, "16:40", "17:30", 1))
                        add(TimeDetailBean(9, "18:30", "19:20", 1))
                        add(TimeDetailBean(10, "19:30", "20:20", 1))
                        add(TimeDetailBean(11, "20:30", "21:20", 1))
                        add(TimeDetailBean(12, "00:00", "00:00", 1))
                        add(TimeDetailBean(13, "00:00", "00:00", 1))
                        add(TimeDetailBean(14, "00:00", "00:00", 1))
                        add(TimeDetailBean(15, "00:00", "00:00", 1))
                        add(TimeDetailBean(16, "00:00", "00:00", 1))
                        add(TimeDetailBean(17, "00:00", "00:00", 1))
                        add(TimeDetailBean(18, "00:00", "00:00", 1))
                        add(TimeDetailBean(19, "00:00", "00:00", 1))
                        add(TimeDetailBean(20, "00:00", "00:00", 1))
                    }
                    timeDao.insertTimeList(timeList)
                }

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
                PreferenceUtils.saveBooleanToSP(context.applicationContext, "has_adjust", true)
            } catch (e: Exception) {

            }

        }

        if (!PreferenceUtils.getBooleanFromSP(context.applicationContext, "has_intro", false)) {
            val tableData = TableBean(type = 1, id = 1, tableName = "")
            val dataBase = AppDatabase.getDatabase(context)
            val tableDao = dataBase.tableDao()
            val timeDao = dataBase.timeDetailDao()
            val timeTableDao = dataBase.timeTableDao()
            timeTableDao.insertTimeTable(TimeTableBean(id = 1, name = "默认"))
            val timeList = ArrayList<TimeDetailBean>().apply {
                add(TimeDetailBean(1, "08:00", "08:50", 1))
                add(TimeDetailBean(2, "09:00", "09:50", 1))
                add(TimeDetailBean(3, "10:10", "11:00", 1))
                add(TimeDetailBean(4, "11:10", "12:00", 1))
                add(TimeDetailBean(5, "13:30", "14:20", 1))
                add(TimeDetailBean(6, "14:30", "15:20", 1))
                add(TimeDetailBean(7, "15:40", "16:30", 1))
                add(TimeDetailBean(8, "16:40", "17:30", 1))
                add(TimeDetailBean(9, "18:30", "19:20", 1))
                add(TimeDetailBean(10, "19:30", "20:20", 1))
                add(TimeDetailBean(11, "20:30", "21:20", 1))
                add(TimeDetailBean(12, "00:00", "00:00", 1))
                add(TimeDetailBean(13, "00:00", "00:00", 1))
                add(TimeDetailBean(14, "00:00", "00:00", 1))
                add(TimeDetailBean(15, "00:00", "00:00", 1))
                add(TimeDetailBean(16, "00:00", "00:00", 1))
                add(TimeDetailBean(17, "00:00", "00:00", 1))
                add(TimeDetailBean(18, "00:00", "00:00", 1))
                add(TimeDetailBean(19, "00:00", "00:00", 1))
                add(TimeDetailBean(20, "00:00", "00:00", 1))
            }
            timeDao.insertTimeList(timeList)
            tableDao.insertTable(tableData)
            PreferenceUtils.saveBooleanToSP(context.applicationContext, "has_adjust", true)
        }
    }
}