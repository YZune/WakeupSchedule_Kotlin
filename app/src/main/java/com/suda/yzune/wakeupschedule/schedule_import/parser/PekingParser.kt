package com.suda.yzune.wakeupschedule.schedule_import.parser

import com.suda.yzune.wakeupschedule.schedule_import.Common
import com.suda.yzune.wakeupschedule.schedule_import.bean.Course
import org.jsoup.Jsoup

// 北大
class PekingParser(source: String) : Parser(source) {

    override fun generateCourseList(): List<Course> {
        val courseList = arrayListOf<Course>()
        val doc = Jsoup.parse(source)
        val kbtable = doc.select("table[class=datagrid]").first()
        val tBody = kbtable.getElementsByTag("tbody").first()
        var teacher = ""
        for (tr in tBody.getElementsByTag("tr")) {
            val tds = tr.getElementsByTag("td")
            if (tds.size >= 11) {
                if (tds[8].text().contains('未'))
                    continue
                val courseName = tds[0].text().trim()
                teacher = tds[4].text().trim()
                val timeInfos = tds[7].html().split("<br>")
                var startWeek = 1
                var endWeek = 16
                var startNode = 1
                var endNode = 2
                var type = 0
                var day = 7
                timeInfos.forEach {
                    val timeInfo = Jsoup.parse(it).text().trim().split(' ')
                    if (timeInfo.size >= 2) {
                        if (timeInfo[0].contains('~')) {
                            startWeek = timeInfo[0].substringBefore('~').toInt()
                            endWeek = timeInfo[0].substringAfter('~').substringBefore('周').toInt()
                        }
                        type = when {
                            timeInfo[1].contains('单') -> 1
                            timeInfo[1].contains('双') -> 2
                            else -> 0
                        }
                        Common.chineseWeekList.forEachIndexed { index, s ->
                            if (index != 0) {
                                if (timeInfo[1].contains(s)) {
                                    day = index
                                    return@forEachIndexed
                                }
                            }
                        }
                        val matchResult = Common.nodePattern1.find(timeInfo[1])
                        if (matchResult != null) {
                            val m = matchResult.value
                            startNode = m.substringBefore('~').toInt()
                            endNode = m.substringAfter('~').substringBefore('节').toInt()
                        }
                        val room = if (timeInfo.size >= 3) {
                            timeInfo[2]
                        } else {
                            timeInfo[1].substringAfter('(').substringBefore(')')
                        }
                        courseList.add(
                                Course(
                                        name = courseName, day = day, room = room,
                                        teacher = teacher, startNode = startNode,
                                        endNode = endNode, startWeek = startWeek,
                                        endWeek = endWeek, type = type
                                )
                        )
                    }
                }
            }
        }
        return courseList
    }

}