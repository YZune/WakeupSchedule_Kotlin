package com.suda.yzune.wakeupschedule.bean

data class TableSelectBean(
        var id: Int,
        var tableName: String,
        var background: String = "",
        var type: Int = 0
)