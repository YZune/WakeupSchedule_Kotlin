package com.suda.yzune.wakeupschedule.schedule_import.parser

import com.google.gson.Gson
import com.suda.yzune.wakeupschedule.schedule_import.Common
import com.suda.yzune.wakeupschedule.schedule_import.bean.ChengFangInfo
import com.suda.yzune.wakeupschedule.schedule_import.bean.Course

class ChengFangParser(source: String) : Parser(source) {

    override fun generateCourseList(): List<Course> {
        val courseList = arrayListOf<Course>()
        val json = source.substringAfter("var kbxx = ").substringBefore(';')
        val gson = Gson()
        val weekList = arrayListOf<Int>()
        gson.fromJson(json, Array<ChengFangInfo>::class.java).forEach {
            weekList.clear()
            it.zcs.split(',').forEach { str ->
                weekList.add(str.toInt())
            }
            weekList.sort()
            val day = it.xq.toInt()
            val startNode = it.jcdm2.split(',')[0].toInt()
            val endNode =
                    if (it.jcdm2.contains(',')) it.jcdm2.split(',').last().toInt() else it.jcdm2.split(',')[0].toInt()
            val step = endNode - startNode + 1
            Common.weekIntList2WeekBeanList(weekList).forEach { weekBean ->
                courseList.add(
                        Course(
                                name = it.kcmc, room = it.jxcdmcs,
                                teacher = it.teaxms, day = day,
                                startWeek = weekBean.start, endWeek = weekBean.end,
                                type = weekBean.type, startNode = startNode,
                                endNode = startNode + step - 1
                        )
                )
            }
        }
        return courseList
    }

}