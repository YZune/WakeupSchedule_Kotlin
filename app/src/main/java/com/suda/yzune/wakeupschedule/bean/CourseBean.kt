package com.suda.yzune.wakeupschedule.bean

data class CourseBean(
        val id: Int,
        var courseName: String,
        var day: Int,
        var room: String?,
        var teacher: String?,
        var startNode: Int,
        var step: Int,
        var startWeek: Int,
        var endWeek: Int,
        var type: Int,
        var color: String,
        var tableName: String
)