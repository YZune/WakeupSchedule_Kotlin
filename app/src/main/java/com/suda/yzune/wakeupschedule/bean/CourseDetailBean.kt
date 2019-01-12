package com.suda.yzune.wakeupschedule.bean

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index

@Entity(foreignKeys = [(
        ForeignKey(entity = CourseBaseBean::class,
                parentColumns = ["id", "tableId"],
                childColumns = ["id", "tableId"],
                onUpdate = CASCADE,
                onDelete = CASCADE
        ))],
        primaryKeys = ["day", "startNode", "startWeek", "type", "tableId", "id"],
        indices = [Index(value = ["id", "tableId"], unique = false)])

data class CourseDetailBean(
        var id: Int,
        var day: Int,
        var room: String?,
        var teacher: String?,
        var startNode: Int,
        var step: Int,
        var startWeek: Int,
        var endWeek: Int,
        var type: Int,
        var tableId: Int
)