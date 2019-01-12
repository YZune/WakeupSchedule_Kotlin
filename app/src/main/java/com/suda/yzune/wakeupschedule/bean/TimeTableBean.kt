package com.suda.yzune.wakeupschedule.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TimeTableBean(
        @PrimaryKey(autoGenerate = true)
        var id: Int,
        var name: String,
        var sameLen: Boolean = true,
        var courseLen: Int = 50
)