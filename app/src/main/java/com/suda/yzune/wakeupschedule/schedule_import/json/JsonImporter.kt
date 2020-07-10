package com.suda.yzune.wakeupschedule.schedule_import.json

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.bean.TimeBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import tiiehenry.classschedule.json.ClassSchedule

class JsonImporter {
    companion object {

        private fun parseTimeBean(day: Int, node: String): TimeBean {
            return if (node.contains("-")) {
                val start = node.substringBefore("-").toInt()
                val end = node.substringAfter("-").toInt()
                TimeBean(day, start, end)
            } else {
                TimeBean(day, node.toInt(), node.toInt())
            }
        }

        private fun parseRangeList(rangeList: MutableList<String>): ArrayList<Int> {
            val list = arrayListOf<Int>()
            for (range in rangeList) {
                val splited = range.split(",")
                for (it in splited) {
                    if (it.isNotEmpty()) {
                        if (it.contains("-")) {
                            val start = it.substringBefore("-").toInt()
                            val end = it.substringAfter("-").toInt()
                            for (i in start..end) {
                                list.add(i)
                            }
                        } else {
                            list.add(it.toInt())
                        }
                    }
                }
            }
            return list
        }
        fun importClassSchedule(cs: ClassSchedule, baseList: ArrayList<CourseBaseBean>, detailList: ArrayList<CourseDetailBean>, importId:Int, app: Context) {
            cs.课程.forEachIndexed { id, it ->
                baseList.add(CourseBaseBean(id, it.名称, "#${Integer.toHexString(ViewUtils.getCustomizedColor(app, id % 9))}", importId))
                for (classTime in it.上课安排) {
                    for (day in classTime.星期) {
                        for (node in classTime.节次) {
                            if (node.isEmpty()) {
                                continue
                            }
                            val timeBean = parseTimeBean(day, node)
                            val bean = CourseEditBean(
                                    id = id, time = MutableLiveData(timeBean), room = classTime.地点,
                                    teacher = classTime.教师, weekList = MutableLiveData(parseRangeList(classTime.周次)),
                                    tableId = importId)
                            detailList.addAll(CourseUtils.editBean2DetailBeanList(bean))
                        }
                    }
                }
            }
        }
    }
}