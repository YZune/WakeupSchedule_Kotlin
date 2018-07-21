package com.suda.yzune.wakeupschedule.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.Index

@Entity(foreignKeys = [(
        ForeignKey(entity = CourseBaseBean::class,
                parentColumns = ["id", "tableName"],
                childColumns = ["id", "tableName"],
                onUpdate = CASCADE,
                onDelete = CASCADE
        ))],
        primaryKeys = ["day", "startNode"],
        indices = [Index(value = ["id", "tableName"], unique = false)])

data class CourseDetailBean(
        val id: Int,
        var day: Int,
        var room: String?,
        var teacher: String?,
        var startNode: Int,
        var step: Int,
        var startWeek: Int,
        var endWeek: Int,
        var type: Int,
        var tableName: String
)