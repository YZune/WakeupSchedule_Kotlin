package com.suda.yzune.wakeupschedule.bean

import androidx.room.Entity

/**
 * 0. 课程表Widget
 *      0. 周视图
 *      1. 日视图
 *
 * 1. 咩咩Widget
 */

@Entity(primaryKeys = ["id"])
data class AppWidgetBean(
        var id: Int,
        var baseType: Int,
        var detailType: Int,
        var info: String
)