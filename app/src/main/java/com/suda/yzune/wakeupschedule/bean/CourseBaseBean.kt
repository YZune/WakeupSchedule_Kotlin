package com.suda.yzune.wakeupschedule.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(primaryKeys = ["id", "tableName"])
data class CourseBaseBean(
        var id: Int,
        var courseName: String,
        var color: String,
        var tableName: String
        )