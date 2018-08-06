package com.suda.yzune.wakeupschedule.bean

import android.arch.persistence.room.Entity

@Entity(primaryKeys = ["node"])
data class TimeDetailBean(
        val node: Int,
        var startTime: String,
        var endTime: String
)