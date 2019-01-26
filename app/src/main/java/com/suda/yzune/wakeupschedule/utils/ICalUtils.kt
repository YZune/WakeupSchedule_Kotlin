package com.suda.yzune.wakeupschedule.utils

import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.parameter.Value
import net.fortuna.ical4j.model.property.*
import net.fortuna.ical4j.util.UidGenerator
import java.util.*
import java.util.Calendar

object ICalUtils {

    fun getClassEvents(startTimeMap: ArrayList<Calendar>,
                       endTimeMap: ArrayList<Calendar>,
                       maxWeek: Int,
                       course: CourseBean,
                       week: Int): List<VEvent> {
        val result = arrayListOf<VEvent>()
        var i = 1
        while (i <= maxWeek) {
            if (course.inWeek(i)) {
                var j = i
                while (course.inWeek(++j));
                j--
                val event = getClassEvent(startTimeMap, endTimeMap, course, week, i, j)
                if (event != null) {
                    event.validate()
                    result.add(event)
                }
                i += j - i
            }
            i++
        }
        return result
    }


    private fun getClassEvent(startTimeMap: ArrayList<Calendar>,
                              endTimeMap: ArrayList<Calendar>,
                              course: CourseBean,
                              currentWeek: Int,
                              startWeek: Int,
                              endWeek: Int
    ): VEvent? {
        val now = Calendar.getInstance(Locale.CHINA)
        val dayBefore = (currentWeek - startWeek) * 7
        val dayAfter = (endWeek - currentWeek) * 7

        // repeat every week until endDate
        val recur = Recur(Recur.WEEKLY, DateTime(CourseUtils.getDateAfter(now.time, dayAfter)))
        recur.interval = 1
        val rule = RRule(recur)

        val startTime = startTimeMap[course.startNode - 1]
        val endTime = endTimeMap[course.startNode + course.step - 2]

        val dailyStart = Calendar.getInstance()
        dailyStart.time = CourseUtils.getDateBefore(now.time, dayBefore)
        dailyStart.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY))
        dailyStart.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE))
        dailyStart.set(Calendar.DAY_OF_WEEK, weekDayConvert(course.day))
        val start = DateTime(dailyStart.time)

        val dailyEnd = Calendar.getInstance()
        dailyEnd.time = CourseUtils.getDateBefore(now.time, dayBefore)
        dailyEnd.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY))
        dailyEnd.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE))
        dailyEnd.set(Calendar.DAY_OF_WEEK, weekDayConvert(course.day))
        val end = DateTime(dailyEnd.time)

        val paraList = ParameterList()
        paraList.add(ParameterFactoryImpl.getInstance().createParameter(Value.PERIOD.name, Value.PERIOD.value))

        val periodList = PeriodList()
        periodList.add(Period(start, end))
        val rdate = RDate(paraList, periodList)

        // create event, repeat weekly
        val event = VEvent(start, end, course.courseName)

        // set event
        event.properties.add(Uid(UidGenerator("WakeUpSchedule").generateUid().value))
        event.properties.add(Location("${course.room} ${course.teacher}"))

        event.properties.add(Description("${course.getNodeString()}\n${course.room}\n${course.teacher}"))
        event.properties.add(rdate)
        event.properties.add(rule)
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
            calendar.set(Calendar.HOUR_OF_DAY, t[0].toInt())
            calendar.set(Calendar.MINUTE, t[1].toInt())
            calendar.set(Calendar.SECOND, 0)
            result.add(calendar)
        }
        return result
    }
}