package com.suda.yzune.wakeupschedule.schedule_import.bean

data class Course(
        val name: String,
        val day: Int, //1 - 7
        val room: String = "",
        val teacher: String = "",
        val startNode: Int,
        val endNode: Int,
        val startWeek: Int,
        val endWeek: Int,
        val type: Int
)