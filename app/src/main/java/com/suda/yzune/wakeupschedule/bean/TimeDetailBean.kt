package com.suda.yzune.wakeupschedule.bean

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(foreignKeys = [(
        ForeignKey(entity = TimeTableBean::class,
                parentColumns = ["id"],
                childColumns = ["timeTable"],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        ))], primaryKeys = ["node", "timeTable"],
        indices = [Index(value = ["timeTable"], unique = false)])
data class TimeDetailBean(
        val node: Int,
        var startTime: String,
        var endTime: String,
        var timeTable: Int = 1
)