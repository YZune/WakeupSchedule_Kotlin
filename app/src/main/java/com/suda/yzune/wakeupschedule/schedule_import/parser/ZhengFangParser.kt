package com.suda.yzune.wakeupschedule.schedule_import.parser

import com.suda.yzune.wakeupschedule.schedule_import.Common
import com.suda.yzune.wakeupschedule.schedule_import.bean.Course

class ZhengFangParser(source: String, private val type: Int) : Parser(source) {

    override fun generateCourseList(): List<Course> {
        val doc = org.jsoup.Jsoup.parse(source)
        val table1 = doc.getElementById("Table1")
        val trs = table1.getElementsByTag("tr")
        val importBeanList = ArrayList<ImportBean>()
        var node: Int = -1
        for (tr in trs) {
            val tds = tr.getElementsByTag("td")
            var countFlag = false
            var countDay = 0
            for (td in tds) {
                val courseSource = td.text().trim()
                if (courseSource.length <= 1) {
                    if (countFlag) {
                        countDay++
                    }
                    continue
                }
                if (Common.otherHeader.contains(courseSource)) {
                    //other list
                    continue
                }
                val result = Common.parseHeaderNodeString(courseSource)
                if (result != -1) {
                    node = result
                    countFlag = true
                    continue
                }
                countDay++
                when (type) {
                    0 -> importBeanList.addAll(parseImportBean(countDay, td.html(), node))
                    1 -> importBeanList.addAll(parseImportBean1(countDay, courseSource, node))
                }
            }
        }
        return importList2CourseList(importBeanList, source)
    }

    private fun parseImportBean(cDay: Int, html: String, node: Int): ArrayList<ImportBean> {
        val courses = ArrayList<ImportBean>()
        var isAbnormal = false
        val courseSplits = if (html.substringBeforeLast("</td>").contains("<br><br><br>")) {
            isAbnormal = true
            html.substringBeforeLast("</td>").split("<br><br><br>")
        } else {
            html.substringBeforeLast("</td>").split("<br><br>")
        }
        for (courseStr in courseSplits) {
            val split = courseStr.substringAfter("\">").substringBeforeLast("</a>").split("<br>")
            if (split.isEmpty() || split.size < 3) continue
            val temp = if (split[1] in Common.courseProperty) {
                if (split.size == 4) {
                    ImportBean(startNode = node, name = split[0],
                            timeInfo = split[2],
                            room = split[3], teacher = "", cDay = cDay)
                } else {
                    ImportBean(startNode = node, name = split[0],
                            timeInfo = split[2],
                            room = split[4], teacher = split[3], cDay = cDay)
                }
            } else {
                if (split.size == 3) {
                    if (!isAbnormal) {
                        ImportBean(startNode = node, name = split[0],
                                timeInfo = split[1],
                                room = split[2], teacher = "", cDay = cDay)
                    } else {
                        ImportBean(startNode = node, name = split[0],
                                timeInfo = split[1],
                                room = "", teacher = split[2], cDay = cDay)
                    }
                } else {
                    ImportBean(startNode = node, name = split[0],
                            timeInfo = split[1],
                            room = split[3], teacher = split[2], cDay = cDay)
                }
            }
            courses.add(temp)
        }
        return courses
    }

    private fun parseImportBean1(cDay: Int, source: String, node: Int): ArrayList<ImportBean> {
        val courses = ArrayList<ImportBean>()
        val split = source.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
        var preIndex = -1
        var hasTypeFlag = false
        for (i in split.indices) {
            if (split[i].contains('{') && split[i].contains('}')) {
                if (preIndex != -1) {
                    if (split[preIndex - 1] in Common.courseProperty) {
                        hasTypeFlag = true
                    }
                    val temp = ImportBean(startNode = node, name = if (hasTypeFlag && preIndex >= 2) split[preIndex - 2] else split[preIndex - 1],
                            timeInfo = split[preIndex],
                            room = "", teacher = "", cDay = cDay)
                    if ((i - preIndex - 2) == 1) {
                        temp.teacher = split[preIndex + 1]
                    } else {
                        temp.teacher = split[preIndex + 1]
                        temp.room = split[preIndex + 2]
                    }
                    courses.add(temp)
                    preIndex = i
                } else {
                    preIndex = i
                }
            }
            if (i == split.size - 1) {
                if (split[preIndex - 1] in Common.courseProperty) {
                    hasTypeFlag = true
                }
                val temp = ImportBean(startNode = node, name = if (hasTypeFlag && preIndex >= 2) split[preIndex - 2] else split[preIndex - 1],
                        timeInfo = split[preIndex],
                        room = "", teacher = "", cDay = cDay)
                if ((i - preIndex) == 1) {
                    temp.teacher = split[preIndex + 1]
                } else {
                    temp.teacher = split[preIndex + 1]
                    temp.room = split[preIndex + 2]
                }
                courses.add(temp)
            }
        }
        return courses
    }

    private fun importList2CourseList(importList: ArrayList<ImportBean>, source: String): List<Course> {
        val retryList = arrayListOf<Int>()
        val result = arrayListOf<Course>()
        for (i in importList) {
            val time = parseTime(i, i.timeInfo, i.startNode, source, i.name)
            val day = if (i.timeInfo.substring(0, 2) in Common.chineseWeekList) time[0] else i.cDay
            result.add(
                    Course(
                            name = i.name, day = day, room = i.room ?: "",
                            teacher = i.teacher ?: "", startNode = i.startNode,
                            endNode = i.startNode + time[1] - 1,
                            type = time[4],
                            startWeek = time[2],
                            endWeek = time[3]
                    )
            )
            if (day == 0) {
                retryList.add(importList.size - 1)
            }
        }
        return result
    }

    private fun parseTime(importBean: ImportBean, time: String, startNode: Int, source: String, courseName: String): Array<Int> {
        val result = Array(5) { 0 }
        //按顺序分别为day, step, startWeek, endWeek, type

        //day
        if (time[0] == '周') {
            val dayStr = time.substring(0, 2)
            val day = Common.getWeekFromChinese(dayStr)
            result[0] = day
        }
        if (result[0] == 0) {
            var startIndex = source.indexOf(">第${startNode}节</td>")
            if (startIndex == -1) {
                startIndex = source.indexOf(">第${Common.getNodeStr(startNode)}节</td>")
            }
            var endIndex = 0
            if (startIndex != -1) {
                endIndex = source.indexOf(courseName, startIndex)
            }
            if (startIndex != -1 && endIndex != -1) {
                result[0] = Common.countStr(source.substring(startIndex, endIndex), "Center")
            }
        }

        //step
        var step = 0
        when {
            time.contains("节/") -> {
                val numLocate = time.indexOf("节/")
                step = time.substring(numLocate - 1, numLocate).toInt()
            }
            time.contains(",") -> {
                var locate = 0
                step = 1
                while (time.indexOf(",", locate) != -1 && locate < time.length) {
                    step += 1
                    locate = time.indexOf(",", locate) + 1
                }
            }
            time.contains("第${startNode}节") -> {
                step = 1
            }
        }
        if (step == 0) {
            val matchResult = Common.nodePattern.find(time)
            if (matchResult != null) {
                val nodeInfo = matchResult.value
                val nodes = nodeInfo.substring(1, nodeInfo.length - 1).split("-".toRegex()).dropLastWhile { it.isEmpty() }
                if (nodes.isNotEmpty()) {
                    importBean.startNode = nodes[0].toInt()
                }
                if (nodes.size > 1) {
                    step = nodes[1].toInt() - importBean.startNode + 1
                }
            }
        }
        result[1] = step

        //周数
        var startWeek = 1
        var endWeek = 20
        val matchResult = Common.weekPattern.find(time)
        if (matchResult != null) {
            val weekInfo = matchResult.value //{第2-16周
            val weeks = weekInfo.substring(2, weekInfo.length - 1).split("-".toRegex()).dropLastWhile { it.isEmpty() }
            if (weeks.isNotEmpty()) {
                startWeek = weeks[0].toInt()
                result[2] = startWeek
            }
            if (weeks.size > 1) {
                endWeek = weeks[1].toInt()
                result[3] = endWeek
            }
        }

        //单双周
        if (time.contains("单周")) {
            result[4] = 1
        } else if (time.contains("双周")) {
            result[4] = 2
        }

        return result
    }

}

data class ImportBean(var name: String,
                      var timeInfo: String,
                      var teacher: String?,
                      var room: String?,
                      var startNode: Int,
                      var cDay: Int = 0)