package com.suda.yzune.wakeupschedule.utils

import android.arch.lifecycle.MutableLiveData
import com.suda.yzune.wakeupschedule.bean.*

object CourseUtils {
    fun getDayInt(weekDay: Int): String {
        return when (weekDay) {
            1 -> "周一"
            2 -> "周二"
            3 -> "周三"
            4 -> "周四"
            5 -> "周五"
            6 -> "周六"
            7 -> "周日"
            else -> ""
        }
    }

    fun courseBean2DetailBean(c: CourseBean): CourseDetailBean {
        return CourseDetailBean(
                id = c.id, room = c.room, day = c.day, teacher = c.teacher,
                startNode = c.startNode, step = c.step, startWeek = c.startWeek,
                endWeek = c.endWeek, tableName = c.tableName, type = c.type
        )
    }

    fun courseBean2BaseBean(c: CourseBean): CourseBaseBean {
        return CourseBaseBean(
                id = c.id, courseName = c.courseName,
                color = c.color, tableName = c.tableName
        )
    }

    fun editBean2DetailBeanList(editBean: CourseEditBean): MutableList<CourseDetailBean> {
        val result = mutableListOf<CourseDetailBean>()
        intList2WeekBeanList(editBean.weekList.value!!).forEach {
            result.add(CourseDetailBean(
                    id = editBean.id, room = editBean.room, teacher = editBean.teacher,
                    day = editBean.time.value!!.day, startNode = editBean.time.value!!.startNode,
                    step = editBean.time.value!!.endNode - editBean.time.value!!.startNode + 1,
                    startWeek = it.start, endWeek = it.end, type = it.type,
                    tableName = editBean.tableName
            ))

        }
        return result
    }

    fun detailBean2EditBean(c: CourseDetailBean): CourseEditBean {
        return CourseEditBean(
                id = c.id,
                time = MutableLiveData<TimeBean>().apply {
                    this.value = TimeBean(day = c.day, startNode = c.startNode, endNode = c.startNode + c.step - 1)
                },
                room = c.room, teacher = c.teacher,
                weekList = MutableLiveData<ArrayList<Int>>().apply {
                    this.value = ArrayList<Int>().apply {
                        when (c.type) {
                            0 -> {
                                for (i in c.startWeek..c.endWeek) {
                                    this.add(i)
                                }
                            }
                            else -> {
                                for (i in c.startWeek..c.endWeek step 2) {
                                    this.add(i)
                                }
                            }
                        }
                    }
                },
                tableName = c.tableName
        )
    }

    fun intList2WeekBeanList(input: ArrayList<Int>): ArrayList<WeekBean> {
        var reset = 0
        var temp = WeekBean(0, 0, -1)
        val list = arrayListOf<WeekBean>()
        for (i in input.indices) {
            if (reset == 1) {
                list.add(temp)
                temp = WeekBean(0, 0, -1)
                reset = 0
            }
            if (i < input.size - 1) {
                if (temp.type == 0 && input[i + 1] - input[i] == 1) temp.end = input[i + 1]
                else if ((temp.type == 1 || temp.type == 2) && input[i + 1] - input[i] == 2)
                    temp.end = input[i + 1]
                else if (temp.type != -1) {
                    reset = 1
                }
            }
            if (i < input.size - 1 && temp.type == -1) {
                temp.start = input[i]
                when (input[i + 1] - input[i]) {
                    1 -> {
                        temp.type = 0
                        temp.end = input[i + 1]
                    }
                    2 -> {
                        temp.type = if (input[i] % 2 != 0) 1 else 2
                        temp.end = input[i + 1]
                    }
                    else -> {
                        temp.end = input[i]
                        temp.type = 0
                        reset = 1
                    }
                }
            }
            if (i == input.size - 1 && temp.type != -1) list.add(temp)
            if (i == input.size - 1 && temp.type == -1) {
                temp.start = input[i]
                temp.end = input[i]
                temp.type = 0
                list.add(temp)
            }
        }
        return list
    }

    fun checkSelfUnique(list: List<CourseDetailBean>): Boolean {
        var flag = true
        for (i in 0 until list.size - 1) {
            for (j in i + 1 until list.size) {
                if (list[i].day == list[j].day
                        && list[i].startNode == list[j].startNode
                        && list[i].startWeek == list[j].startWeek
                        && list[i].type == list[j].type
                        && list[i].tableName == list[j].tableName) {
                    flag = false
                    return flag
                }
            }
        }
        return flag
    }
}