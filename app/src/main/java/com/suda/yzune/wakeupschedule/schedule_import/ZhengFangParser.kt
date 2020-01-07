package com.suda.yzune.wakeupschedule.schedule_import

import com.suda.yzune.wakeupschedule.bean.ImportBean

class ZhengFangParser(source: String, val type: Int) : Parser(source) {

    override fun generateCourseList(): List<Course> {
        val doc = org.jsoup.Jsoup.parse(source)
        val table1 = doc.getElementById("Table1")
        val trs = table1.getElementsByTag("tr")
        val courses = ArrayList<ImportBean>()
        var node: Int
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
                node = Common.parseHeaderNodeString(courseSource)
                if (node != -1) {
                    countFlag = true
                    continue
                }
                if (Common.otherHeader.contains(courseSource)) {
                    //other list
                    continue
                }
                countDay++
                when (type) {
                    0 -> courses.addAll(parseImportBean(countDay, td.html(), node))
                    1 -> courses.addAll(parseImportBean1(countDay, courseSource, node))
                }
            }
        }
        return courses
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
        for (i in 0 until split.size) {
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
                if (split[preIndex - 1] in courseProperty) {
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

}