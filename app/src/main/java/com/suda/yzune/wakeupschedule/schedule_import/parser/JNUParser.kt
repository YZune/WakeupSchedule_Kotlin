package com.suda.yzune.wakeupschedule.schedule_import.parser

import com.suda.yzune.wakeupschedule.schedule_import.bean.Course
import org.jsoup.Jsoup

// 暨南大学
class JNUParser(source: String) : Parser(source) {
    override fun generateCourseList(): List<Course> {
        val courseList = arrayListOf<Course>()

        val xml = source.substringAfter("</html>")
        val doc = Jsoup.parse(xml)
        val frame = doc.getElementById("oReportCell")
        val table = frame.getElementsByClass("a8")
        val trs = table[0].getElementsByTag("tr").subList(3, 10)

        var courseName: String
        var room: String
        var step: Int
        val courseNames = mutableMapOf<String, Int>()
        for (i in trs.indices) {
            val tds = trs[i].getElementsByTag("td")
            if (tds.isEmpty()) continue

            for (j in tds.indices) {
                val str = tds[j].getElementsByTag("div").text()
                if (str.isNullOrEmpty() || j == 0) continue

                room = str.substringBefore(' ')
                courseName = str.substringAfter('：').substringBeforeLast('(')

                if (courseNames.contains(courseName)) {
                    step = courseNames[courseName]!!
                    courseNames[courseName] = step + 1
                } else {
                    courseNames[courseName] = 1
                    courseList.add(
                        Course(
                            name = courseName, day = i + 1, room = room, teacher = "", startNode = j,
                            endNode = j + 1, startWeek = 1, endWeek = 18, type = 0
                        )
                    )
                }
            }
        }
        var c: Course
        for (i in courseList.indices) {
            c = courseList[i]
            step = courseNames[c.name]!! - 1
            courseList[i] = Course(
                c.name, c.day, c.room, "", c.startNode,
                c.startNode + step, 1, 18, 0
            )
        }
        return courseList
    }
}