package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.bean.TimeBean
import com.suda.yzune.wakeupschedule.schedule_import.Common
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object CourseUtils {
    fun getDayStr(weekDay: Int): String {
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
                endWeek = c.endWeek, tableId = c.tableId, type = c.type
        )
    }

    fun editBean2DetailBeanList(editBean: CourseEditBean): MutableList<CourseDetailBean> {
        val result = mutableListOf<CourseDetailBean>()
        Common.weekIntList2WeekBeanList(editBean.weekList.value!!).forEach {
            result.add(CourseDetailBean(
                    id = editBean.id, room = editBean.room, teacher = editBean.teacher,
                    day = editBean.time.value!!.day, startNode = editBean.time.value!!.startNode,
                    step = editBean.time.value!!.endNode - editBean.time.value!!.startNode + 1,
                    startWeek = it.start, endWeek = it.end, type = it.type,
                    tableId = editBean.tableId
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
                tableId = c.tableId
        )
    }

    fun checkSelfUnique(list: List<CourseDetailBean>): Boolean {
        var flag = true
        for (i in 0 until list.size - 1) {
            for (j in i + 1 until list.size) {
                if (list[i].day == list[j].day
                        && list[i].startNode == list[j].startNode
                        && list[i].startWeek == list[j].startWeek
                        && list[i].type == list[j].type
                        && list[i].tableId == list[j].tableId) {
                    flag = false
                    return flag
                }
            }
        }
        return flag
    }

    fun getDateBefore(d: Date, day: Int): Date {
        val now = Calendar.getInstance()
        now.time = d
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day)
        return now.time
    }

    fun getDateAfter(d: Date, day: Int): Date {
        val now = Calendar.getInstance()
        now.time = d
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day)
        return now.time
    }

    @Throws(ParseException::class)
    fun daysBetween(date: String, nextDay: Boolean = false, sundayFirst: Boolean): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val cal = Calendar.getInstance()
        if (nextDay) {
            cal.add(Calendar.DATE, 1)
        }
        if (sundayFirst) {
            cal.firstDayOfWeek = Calendar.SUNDAY
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        } else {
            cal.firstDayOfWeek = Calendar.MONDAY
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val time2 = cal.timeInMillis
        cal.time = sdf.parse(date)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val time1 = cal.timeInMillis
        var betweenDays: Int = ((time2 - time1) / (1000 * 3600 * 24)).toInt()
        if (betweenDays < 0) {
            betweenDays--
        }
        return betweenDays
    }

    @Throws(ParseException::class)
    fun countWeek(date: String, sundayFirst: Boolean, nextDay: Boolean = false): Int {
        val during = daysBetween(date, nextDay, sundayFirst)
        return during / 7 + 1
    }

    fun getWeekday(nextDay: Boolean = false): String {
        val weekDay = getWeekdayInt(nextDay)
        return getDayStr(weekDay)
    }

    fun getWeekdayInt(nextDay: Boolean = false): Int {
        val cal = Calendar.getInstance()
        if (nextDay) {
            cal.add(Calendar.DATE, 1)
        }
        var weekDay = cal.get(Calendar.DAY_OF_WEEK)
        if (weekDay == 1) {
            weekDay = 7
        } else {
            weekDay -= 1
        }
        return weekDay
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
                if (pn.equals("com.tencent.qqlite", ignoreCase = true)
                        || pn.equals("com.tencent.mobileqq", ignoreCase = true)
                        || pn.equals("com.tencent.tim", ignoreCase = true)) {
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
        var newHour = timeHour + add / 60
        var newMin = add % 60
        var strTime = ""
        if (newHour > 23) {
            newHour = 0
            newMin = 0
        }
        if (newHour < 10 && newMin >= 10) {
            strTime = "0$newHour:$newMin"
        } else if (newHour < 10 && newMin < 10) {
            strTime = "0$newHour:0$newMin"
        } else if (newHour >= 10 && newMin >= 10) {
            strTime = "$newHour:$newMin"
        } else if (newHour >= 10 && newMin < 10) {
            strTime = "$newHour:0$newMin"
        }
        return strTime
    }

    fun getDateStringFromWeek(curWeek: Int, targetWeek: Int, sundayFirst: Boolean): List<String> {
        val calendar = Calendar.getInstance()
        if (targetWeek == curWeek)
            return getDateStringFromCalendar(calendar, sundayFirst)
        val amount = targetWeek - curWeek
        calendar.add(Calendar.WEEK_OF_YEAR, amount)
        return getDateStringFromCalendar(calendar, sundayFirst)
    }

    private fun getDateStringFromCalendar(calendar: Calendar, sundayFirst: Boolean): List<String> {
        val dateList = ArrayList<String>()
        if (sundayFirst) {
            calendar.firstDayOfWeek = Calendar.SUNDAY
        } else {
            calendar.firstDayOfWeek = Calendar.MONDAY
        }
        while (calendar.get(Calendar.DAY_OF_WEEK) != calendar.firstDayOfWeek) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }
        dateList.add((calendar.get(Calendar.MONTH) + 1).toString())
        for (i in 0..6) {
            dateList.add(calendar.get(Calendar.DAY_OF_MONTH).toString())
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dateList
    }
}