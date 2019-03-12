package com.suda.yzune.wakeupschedule.bean

import androidx.room.Entity

@Entity(primaryKeys = ["id"])
data class WeekWidgetBean(
        var id: Int = 0,
        var tableId: Int = -1,
        var bgColor: Int = 0x9affffff.toInt(),
        var bgRadius: Int = 8,
        var courseColor: Int = 0xffffffff.toInt(),
        var titleColor: Int = 0xff000000.toInt(),
        var strokeColor: Int = 0x80ffffff.toInt(),
        var highlightColor: Int,
        var textSize: Int = 14,
        var textAlignment: Int = 0,
        var itemHeight: Int = 56,
        var itemAlpha: Int = 60,
        var showSat: Boolean = true,
        var showSun: Boolean = true,
        var sundayFirst: Boolean = false,
        var showOtherWeekCourse: Boolean = true,
        var showTime: Boolean = false,
        var showTitle: Boolean = true,
        var showButton: Boolean = true
)