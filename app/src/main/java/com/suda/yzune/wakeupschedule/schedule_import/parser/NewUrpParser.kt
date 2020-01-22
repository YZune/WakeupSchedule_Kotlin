package com.suda.yzune.wakeupschedule.schedule_import.parser

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.suda.yzune.wakeupschedule.schedule_import.Common
import com.suda.yzune.wakeupschedule.schedule_import.bean.Course
import com.suda.yzune.wakeupschedule.schedule_import.bean.NewUrpCourseInfo

class NewUrpParser(source: String) : Parser(source) {

    override fun generateCourseList(): List<Course> {
        val result = arrayListOf<Course>()
        val gson = Gson()
        val json = JsonParser.parseString(source)
                .asJsonObject
                .getAsJsonArray("dateList")[0].asJsonObject
                .getAsJsonArray("selectCourseList").toString()
        val list = gson.fromJson(json, Array<NewUrpCourseInfo>::class.java)
        val weekIntList = arrayListOf<Int>()
        list.forEach { info ->
            info.timeAndPlaceList?.forEach { detail ->
                weekIntList.clear()
                detail.classWeek.forEachIndexed { index, c ->
                    if (c == '1') {
                        weekIntList.add(index + 1)
                    }
                }
                Common.weekIntList2WeekBeanList(weekIntList).forEach { week ->
                    result.add(
                            Course(
                                    name = info.courseName, day = detail.classDay,
                                    room = (detail.campusName ?: "") + (detail.teachingBuildingName
                                            ?: "") + detail.classroomName,
                                    teacher = info.attendClassTeacher,
                                    startNode = detail.classSessions,
                                    endNode = detail.classSessions + detail.continuingSession - 1,
                                    startWeek = week.start, endWeek = week.end,
                                    type = week.type
                            )
                    )
                }
            }
        }
        return result
    }

}