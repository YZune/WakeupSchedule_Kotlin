package com.suda.yzune.wakeupschedule.bean

data class CourseOldBean(
        var name: String,
        var room: String,
        var teach: String,
        var id: String,
        var campus: String,
        var start: Int,
        var step: Int,
        var day: Int,
        var startWeek: Int,
        var endWeek: Int,
        var isOdd: Int,
        var num: Long
)