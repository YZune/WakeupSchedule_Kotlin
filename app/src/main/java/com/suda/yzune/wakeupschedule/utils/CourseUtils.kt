package com.suda.yzune.wakeupschedule.utils

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.suda.yzune.wakeupschedule.bean.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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

    @Throws(ParseException::class)
    fun daysBetween(context: Context): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val todayTime = sdf.format(Date())// 获取当前的日期
        val cal = Calendar.getInstance()
        cal.time = sdf.parse(PreferenceUtils.getStringFromSP(context, "termStart", "2018-09-03"))
        val time1 = cal.timeInMillis
        cal.time = sdf.parse(todayTime)
        val time2 = cal.timeInMillis
        val betweenDays = (time2 - time1) / (1000 * 3600 * 24)
        return Integer.parseInt(betweenDays.toString())
    }

    @Throws(ParseException::class)
    fun countWeek(context: Context): Int {
        return daysBetween(context) / 7 + 1
    }

    fun getWeekday(): String {
        var weekDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
        if (weekDay == 1) {
            weekDay = 7
        } else {
            weekDay -= 1
        }
        return getDayInt(weekDay)
    }

    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("M月d日", Locale.CHINA)
        return dateFormat.format(Date())
    }

    fun isQQClientAvailable(context: Context): Boolean {
        val packageManager = context.packageManager
        val pInfo = packageManager.getInstalledPackages(0)
        if (pInfo != null) {
            for (i in pInfo.indices) {
                val pn = pInfo[i].packageName
                if (pn.equals("com.tencent.qqlite", ignoreCase = true) || pn.equals("com.tencent.mobileqq", ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    fun calAfterTime(time: String, min: Int): String {
        val timeHour = Integer.valueOf(time.substring(0, 2))
        val timeMin = Integer.valueOf(time.substring(3, 5))
        val add = timeMin + min
        val newHour = timeHour + add / 60
        val newMin = add % 60
        var strTime = ""

        if (newHour < 10 && newMin >= 10) {
            strTime = "0$newHour:$newMin"
        } else if (newHour < 10 && newMin < 10) {
            strTime = "0$newHour:0$newMin"
        } else if (newHour >= 10 && newMin >= 10) {
            strTime = newHour.toString() + ":" + newMin
        } else if (newHour >= 10 && newMin < 10) {
            strTime = newHour.toString() + ":0" + newMin
        }
        return strTime
    }

    fun isContainName(list: java.util.ArrayList<CourseBaseBean>, name: String): Int {
        var flag = -1
        if (!list.isEmpty()) {
            for (bean in list) {
                if (bean.courseName == name) {
                    flag = bean.id
                    break
                }
            }
        }
        return flag
    }
}