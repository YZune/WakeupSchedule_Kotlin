package com.suda.yzune.wakeupschedule.bean

data class TableSelectBean(
        var id: Int,
        var tableName: String,
        var background: String = "",
        var maxWeek: Int,
        var nodes: Int,
        var type: Int = 0
)