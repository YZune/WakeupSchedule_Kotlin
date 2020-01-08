package com.suda.yzune.wakeupschedule.schedule_import

import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class Parser(val source: String) {

    private val _baseList: ArrayList<CourseBaseBean> = arrayListOf()
    private val _detailList: ArrayList<CourseDetailBean> = arrayListOf()

    abstract fun generateCourseList(): List<Course>

    private fun convertCourse(context: Context, tableId: Int) {
        generateCourseList().forEach { course ->
            var id = Common.findExistedCourseId(_baseList, course.name)
            if (id == -1) {
                id = _baseList.size
                _baseList.add(
                        CourseBaseBean(
                                id = id, courseName = course.name,
                                color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(context, id % 9))}",
                                tableId = tableId
                        )
                )
            }
            _detailList.add(CourseDetailBean(
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
        convertCourse(context, tableId)
        return withContext(Dispatchers.IO) {
            try {
                db.tableDao().insertTable(TableBean(id = tableId, tableName = "未命名"))
            } catch (e: Exception) {
                db.courseDao().removeCourseBaseBeanOfTable(tableId)
            }
            db.courseDao().insertCourses(_baseList, _detailList)
            // todo: context.getString()
            "成功导入${_baseList.size}门课程"
        }
    }

}