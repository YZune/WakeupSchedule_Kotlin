package com.suda.yzune.wakeupschedule.utils

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.Uid
import biweekly.util.Frequency
import biweekly.util.Recurrence
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import java.util.*

object ICalUtils {

    fun getClassEvents(ical: ICalendar, startTimeMap: ArrayList<Calendar>,
                       endTimeMap: ArrayList<Calendar>,
                       maxWeek: Int,
                       course: CourseBean,
                       termStart: Date) {
        var i = 1
        while (i <= maxWeek) {
            if (course.inWeek(i)) {
                var j = i
                while (course.inWeek(++j));
                j--
                val event = getClassEvent(startTimeMap, endTimeMap, course, 1, i, j, termStart)
                if (event != null) {
                    ical.addEvent(event)
                }
                i += j - i
            }
            i++
        }
    }

    private fun getClassEvent(startTimeMap: ArrayList<Calendar>,
                              endTimeMap: ArrayList<Calendar>,
                              course: CourseBean,
                              currentWeek: Int,
                              startWeek: Int,
                              endWeek: Int,
                              termStart: Date
    ): VEvent? {
        val dayBefore = (currentWeek - startWeek) * 7
        val dayAfter = (endWeek - currentWeek) * 7 + course.day

        // repeat every week until endDate
        val recur = Recurrence.Builder(Frequency.WEEKLY).interval(1)
                .until(CourseUtils.getDateAfter(termStart, dayAfter))
                .build()
//        Recur(Recur.WEEKLY, DateTime(CourseUtils.getDateAfter(termStart, dayAfter)))
//        val rule = RRule(recur)

        val startTime = startTimeMap[course.startNode - 1]
        val endTime = endTimeMap[course.startNode + course.step - 2]

        val dailyStart = Calendar.getInstance()
        dailyStart.time = CourseUtils.getDateBefore(termStart, dayBefore)
        dailyStart.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY))
        dailyStart.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE))
        dailyStart.set(Calendar.DAY_OF_WEEK, weekDayConvert(course.day))

        val dailyEnd = Calendar.getInstance()
        dailyEnd.time = CourseUtils.getDateBefore(termStart, dayBefore)
        dailyEnd.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY))
        dailyEnd.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE))
        dailyEnd.set(Calendar.DAY_OF_WEEK, weekDayConvert(course.day))

        // create event, repeat weekly
        val event = VEvent()
        event.setUid("WakeUpSchedule-" + Uid.random().value)
        event.setSummary(course.courseName)
        event.setDateStart(dailyStart.time)
        event.setDateEnd(dailyEnd.time)
        event.setRecurrenceRule(recur)
        event.setLocation("${course.room} ${course.teacher}")
        event.setDescription("${course.getNodeString()}\n${course.room}\n${course.teacher}")

        return event
    }

    private fun weekDayConvert(i: Int): Int {
        if (i in 1..7) {
            when (i) {
                1 -> return Calendar.MONDAY
                2 -> return Calendar.TUESDAY
                3 -> return Calendar.WEDNESDAY
                4 -> return Calendar.THURSDAY
                5 -> return Calendar.FRIDAY
                6 -> return Calendar.SATURDAY
                7 -> return Calendar.SUNDAY
            }
        }
        return -1
    }

    fun getClassTime(timeList: List<TimeDetailBean>, isStart: Boolean): ArrayList<Calendar> {
        val result = arrayListOf<Calendar>()
        timeList.forEach {
            val calendar = Calendar.getInstance()
            val t = if (isStart) {
                it.startTime.split(':')
            } else {
                it.endTime.split(':')
            }
            calendar.set(Calendar.YEAR, 2016)
            calendar.set(Calendar.HOUR_OF_DAY, t[0].toInt())
            calendar.set(Calendar.MINUTE, t[1].toInt())
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            result.add(calendar)
        }
        return result
    }
}