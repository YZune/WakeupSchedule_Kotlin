package com.suda.yzune.wakeupschedule.utils

import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean

object CourseUtils {
    fun courseBean2DetailBean(c: CourseBean): CourseDetailBean {
        return CourseDetailBean(
                id = c.id, room = c.room, day = c.day, teacher = c.teacher,
                startNode = c.startNode, step = c.step, startWeek = c.startWeek,
                endWeek = c.endWeek, tableName = c.tableName, type = c.type
        )
    }
}