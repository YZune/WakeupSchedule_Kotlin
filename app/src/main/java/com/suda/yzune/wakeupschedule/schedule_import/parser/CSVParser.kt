package com.suda.yzune.wakeupschedule.schedule_import.parser

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.suda.yzune.wakeupschedule.schedule_import.bean.Course

class CSVParser(source: String) : Parser(source) {

    override fun generateCourseList(): List<Course> {
        val courseList = arrayListOf<Course>()
        val lines = csvReader().readAll(source)
        for (i in 1 until lines.size) {
            val line = lines[i]
            if (line.size != 7) {
                throw Exception("第 ${i + 1} 行数据有误")
            }
            var startWeek = 0
            var endWeek = 0
            var type = 0
            val weekList = line[6].split('、')
            weekList.forEach { weekStr ->
                if (weekStr.contains('-')) {
                    val weeks = weekStr.split('-')
                    startWeek = weeks[0].trim().toInt()
                    when {
                        weekStr.contains('单') -> {
                            type = 1
                            endWeek = weeks[1].substringBefore('单').trim().toInt()
                        }
                        weekStr.contains('双') -> {
                            type = 2
                            endWeek = weeks[1].substringBefore('双').trim().toInt()
                        }
                        else -> {
                            type = 0
                            endWeek = weeks[1].trim().toInt()
                        }
                    }
                } else if (weekStr.contains("月")) {
                    // 针对 Excel 自动转为日期格式的情况
                    type = 0
                    startWeek = weekStr.substringBefore('月').trim().toInt()
                    endWeek = weekStr.substringAfter("月").trim().removeSuffix("日").trim().toInt()
                } else {
                    startWeek = weekStr.trim().toInt()
                    endWeek = weekStr.trim().toInt()
                    type = 0
                }
                val startNode = line[2].trim().toInt()
                val endNode = line[3].trim().toInt()
                courseList.add(
                        Course(
                                name = line[0], day = line[1].trim().toInt(),
                                startNode = startNode, endNode = endNode,
                                teacher = line[4], room = line[5],
                                startWeek = startWeek, endWeek = endWeek,
                                type = type
                        )
                )
            }
        }
        return courseList
    }
}