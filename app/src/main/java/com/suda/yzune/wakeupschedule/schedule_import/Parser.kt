package com.suda.yzune.wakeupschedule.schedule_import

import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class Parser {

    val courseList: ArrayList<Course> = arrayListOf()
    private val baseList: ArrayList<CourseBaseBean> = arrayListOf()
    private val detailList: ArrayList<CourseDetailBean> = arrayListOf()

    private fun convertCourse(context: Context, tableId: Int) {
        courseList.forEach { course ->
            var id = CourseUtils.isContainName(baseList, course.name)
            if (id == -1) {
                id = baseList.size
                baseList.add(
                        CourseBaseBean(
                                id = id, courseName = course.name,
                                color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(context, id % 9))}",
                                tableId = tableId
                        )
                )
            }
            detailList.add(CourseDetailBean(
                    id = id, room = course.room,
                    teacher = course.teacher, day = course.day,
                    step = course.endNode - course.startNode + 1,
                    startWeek = course.startWeek, endWeek = course.endWeek,
                    type = course.type, startNode = course.startNode,
                    tableId = tableId
            ))
        }
    }

    suspend fun saveCourse(context: Context, tableId: Int, db: AppDatabase): String {
        return withContext(Dispatchers.IO) {
            try {
                db.tableDao().insertTable(TableBean(id = tableId, tableName = "未命名"))
            } catch (e: Exception) {
                db.courseDao().removeCourseBaseBeanOfTable(tableId)
            }
            db.courseDao().insertCourses(baseList, detailList)
            // todo: context.getString()
            "成功导入${baseList.size}门课程"
        }
    }

}