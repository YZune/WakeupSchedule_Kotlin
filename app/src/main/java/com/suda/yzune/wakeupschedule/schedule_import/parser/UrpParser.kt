package com.suda.yzune.wakeupschedule.schedule_import.parser

import com.suda.yzune.wakeupschedule.schedule_import.Common
import com.suda.yzune.wakeupschedule.schedule_import.bean.Course
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class UrpParser(source: String) : Parser(source) {

    override fun generateCourseList(): List<Course> {
        val courseList = arrayListOf<Course>()
        val doc = Jsoup.parse(source)
        var kbtables = doc.getElementsByAttributeValue("class", "displayTag")
        try {
            kbtables.last().getElementsByTag("tbody").first()
        } catch (e: Exception) {
            kbtables = doc.getElementsByAttributeValue("class", "table table-striped table-bordered")
        }
        var nameIndex = -1
        var teacherIndex = -1
        var weekIndex = -1
        var dayIndex = -1
        var nodeIndex = -1
        var stepIndex = -1
        var buildingIndex = -1
        var roomIndex = -1
        var step = 1

        kbtables.forEach { kbtable ->
            if (kbtable.text().contains("星期一")) return@forEach
            val head = kbtable.getElementsByTag("thead").first()
            val headSize = head.getElementsByTag("th").size

            head.getElementsByTag("th").eachText().forEachIndexed { index, s ->
                when (s.trim()) {
                    "课程名" -> nameIndex = index
                    "教师" -> teacherIndex = index
                    "周次" -> weekIndex = index
                    "星期" -> dayIndex = index
                    "节次" -> nodeIndex = index
                    "节数" -> stepIndex = index
                    "教学楼" -> buildingIndex = index
                    "教室" -> roomIndex = index
                }
            }
            if (dayIndex == -1) return@forEach
            val tBody = kbtable.getElementsByTag("tbody").first()
            var courseName = ""
            var teacher = ""
            for (tr in tBody.getElementsByTag("tr")) {
                val tds = tr.getElementsByTag("td")
                val wholeFlag = tds.size > headSize - weekIndex
                val acDayIndex = if (wholeFlag) dayIndex else dayIndex - weekIndex
                if (tds[acDayIndex].text().trim().isBlank()) continue
                // 课名和老师
                if (wholeFlag) {
                    courseName = tds[nameIndex].text()
                    teacher = tds[teacherIndex].text().trim()
                }
                // 教室
                val room = try {
                    tds[if (wholeFlag) buildingIndex else buildingIndex - weekIndex].text().trim() + tds[if (wholeFlag) roomIndex else roomIndex - weekIndex].text().trim()
                } catch (e: Exception) {
                    ""
                }
                // 开始节数
                val nodeE = tds[if (wholeFlag) nodeIndex else nodeIndex - weekIndex]
                val startNode = getStartNode(nodeE)
                // 持续节数
                step = if (stepIndex != -1) {
                    getStep(tds[if (wholeFlag) stepIndex else stepIndex - weekIndex].text().trim())
                } else {
                    nodeE.text().trim().substringAfter('-').substringBefore('节')
                            .trim().toInt() - startNode + 1
                }
                // 星期
                val day = getDay(tds[acDayIndex])
                val acWeekIndex = if (wholeFlag) weekIndex else 0

                val weekStr = tds[acWeekIndex].text().trim()
                var startWeek = 1
                var endWeek = 20
                if (weekStr.contains(',') && !weekStr.contains('-')) {
                    val weekList = arrayListOf<Int>()
                    val weekStrList = weekStr.split(',')
                    weekStrList.forEachIndexed { index, s ->
                        if (index != weekStrList.size - 1) {
                            weekList.add(s.substringBefore('周').trim().toInt())
                        } else {
                            weekList.add(s.substringBefore('周').trim().toInt())
                        }
                    }
                    weekList.sort()
                    Common.weekIntList2WeekBeanList(weekList).forEach { weekBean ->
                        courseList.add(
                                Course(
                                        name = courseName, room = room,
                                        teacher = teacher, day = day,
                                        startNode = startNode, endNode = startNode + step - 1,
                                        startWeek = weekBean.start, endWeek = weekBean.end,
                                        type = weekBean.type
                                )
                        )
                    }
                } else {
                    weekStr.split(',').forEach { week ->
                        val r = Common.weekPattern1.find(week)
                        if (r != null) {
                            val temp = r.value.split('-')
                            if (temp.size == 1) {
                                startWeek = temp[0].toInt()
                                endWeek = temp[0].toInt()
                            } else {
                                startWeek = temp[0].toInt()
                                endWeek = temp[1].toInt()
                            }
                        }
                        val type = when {
                            week.contains('单') -> 1
                            week.contains('双') -> 2
                            else -> 0
                        }
                        courseList.add(
                                Course(
                                        name = courseName, room = room,
                                        teacher = teacher, day = day,
                                        startNode = startNode, endNode = startNode + step - 1,
                                        startWeek = startWeek, endWeek = endWeek,
                                        type = type
                                )
                        )
                    }
                }
            }
        }
        return courseList
    }

    private fun getDay(dayE: Element): Int {
        val str = dayE.text().trim()
        return try {
            str.toInt()
        } catch (e: Exception) {
            Common.getWeekFromChinese(str)
        }
    }

    private fun getStartNode(nodeE: Element): Int {
        return if (nodeE.text().contains('-')) {
            val start = nodeE.text().trim().substringBefore('-').toInt()
            start
        } else {
            try {
                nodeE.text().trim().substringAfter('第').substringBefore('大').substringBefore('小').toInt()
            } catch (e: Exception) {
                Common.getNodeInt(
                        nodeE.text().trim().substringAfter('第').substringBefore('大').substringBefore(
                                '小'
                        )
                )
            }
        }
    }

    private fun getStep(str: String): Int {
        return try {
            str.toInt()
        } catch (e: Exception) {
            Common.getNodeInt(str)
        }
    }

}