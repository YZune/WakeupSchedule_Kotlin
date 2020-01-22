package com.suda.yzune.wakeupschedule.schedule_import.parser.qz

import com.suda.yzune.wakeupschedule.schedule_import.bean.Course
import com.suda.yzune.wakeupschedule.schedule_import.parser.Parser
import org.jsoup.Jsoup

open class QzParser(source: String) : Parser(source) {

    open val tableName = "kbcontent"

    open fun parseCourseName(infoStr: String): String {
        return Jsoup.parse(infoStr.substringBefore("<font").trim()).text()
    }

    open fun convert(day: Int, nodeCount: Int, infoStr: String, courseList: MutableList<Course>) {
        val node = nodeCount * 2 - 1
        val courseHtml = Jsoup.parse(infoStr)
        val courseName = parseCourseName(infoStr)
        val teacher = courseHtml.getElementsByAttributeValue("title", "老师").text().trim()
        val room = courseHtml.getElementsByAttributeValue(
                "title",
                "教室"
        ).text().trim() + courseHtml.getElementsByAttributeValue("title", "分组").text().trim()
        val weekStr = courseHtml.getElementsByAttributeValue("title", "周次(节次)").text().substringBefore("(周)")
        val weekList = weekStr.split(',')
        var startWeek = 0
        var endWeek = 0
        var type = 0
        weekList.forEach {
            if (it.contains('-')) {
                val weeks = it.split('-')
                if (weeks.isNotEmpty()) {
                    startWeek = weeks[0].toInt()
                }
                if (weeks.size > 1) {
                    type = when {
                        weeks[1].contains('单') -> 1
                        weeks[1].contains('双') -> 2
                        else -> 0
                    }
                    endWeek = weeks[1].substringBefore('(').toInt()
                }
            } else {
                startWeek = it.substringBefore('(').toInt()
                endWeek = it.substringBefore('(').toInt()
            }
            courseList.add(
                    Course(
                            name = courseName, room = room,
                            teacher = teacher, day = day,
                            startNode = node, endNode = node + 1,
                            startWeek = startWeek, endWeek = endWeek,
                            type = type
                    )
            )
        }
    }

    override fun generateCourseList(): List<Course> {
        val courseList = arrayListOf<Course>()
        val doc = Jsoup.parse(source)
        val kbtable = doc.getElementById("kbtable")
        val trs = kbtable.getElementsByTag("tr")

        var nodeCount = 0
        for (tr in trs) {
            val tds = tr.getElementsByTag("td")
            if (tds.isEmpty()) {
                continue
            }
            nodeCount++

            var day = 0

            for (td in tds) {
                day++
                val divs = td.getElementsByTag("div")
                for (div in divs) {
                    val courseElements = div.getElementsByClass(tableName)
                    if (courseElements.text().isBlank()) {
                        continue
                    }
                    val courseHtml = courseElements.html()
                    var startIndex = 0
                    var splitIndex = courseHtml.indexOf("-----")
                    while (splitIndex != -1) {
                        convert(
                                day,
                                nodeCount,
                                courseHtml.substring(startIndex, splitIndex),
                                courseList
                        )
                        startIndex = courseHtml.indexOf("<br>", splitIndex) + 4
                        splitIndex = courseHtml.indexOf("-----", startIndex)
                    }
                    convert(
                            day,
                            nodeCount,
                            courseHtml.substring(startIndex, courseHtml.length),
                            courseList
                    )
                }
            }
        }
        return courseList
    }

}