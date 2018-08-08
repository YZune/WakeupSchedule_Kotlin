package com.suda.yzune.wakeupschedule.bean

import android.arch.persistence.room.Entity

/**
 * 0. 课程表Widget
 *      0. 周视图
 *      1. 日视图
 *
 * 1. 咩咩Widget
 */


//todo: 增加一列else，放tableName或其他信息
@Entity(primaryKeys = ["id"])
data class AppWidgetBean(
        var id: Int,
        var baseType: Int,
        var detailType: Int
)