package com.suda.yzune.wakeupschedule.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class TimeTableBean(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        var name: String
)