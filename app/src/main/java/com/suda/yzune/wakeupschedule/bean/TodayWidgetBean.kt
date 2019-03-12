package com.suda.yzune.wakeupschedule.bean

import androidx.room.Entity

@Entity(primaryKeys = ["id"])
data class TodayWidgetBean(
        var id: Int = 0,
        var tableId: Int = -1,
        var bgColor: Int = 0x9affffff.toInt(),
        var bgRadius: Int = 8,
        var courseColor: Int = 0xff000000.toInt(),
        var titleColor: Int = 0xff000000.toInt(),
        var timeColor: Int = 0xff000000.toInt(),
        var textSize: Int = 14,
        var showTitle: Boolean = true,
        var showButton: Boolean = true
)