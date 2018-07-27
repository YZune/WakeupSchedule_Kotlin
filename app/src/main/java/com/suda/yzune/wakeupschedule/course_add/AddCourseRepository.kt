package com.suda.yzune.wakeupschedule.course_add

import android.arch.lifecycle.LiveData
import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean

class AddCourseRepository(context: Context) {

    lateinit var detailList: MutableList<CourseDetailBean>

    private val dataBase = AppDatabase.getDatabase(context)
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()

    /**
     * @param type = 0 为添加新课程
     * @param type = 1 为修改课程
     */

    fun initData(type: Int): MutableList<CourseDetailBean> {
        when (type) {
            0 -> detailList = mutableListOf(newBlankCourse())
            1 -> detailList = mutableListOf(newBlankCourse())
        }
        return detailList
    }

    fun getLastId(): LiveData<Int> {
        return baseDao.getLastId()
    }

    private fun newBlankCourse(): CourseDetailBean {
        return CourseDetailBean(
                id = -1, day = 0, teacher = "", room = "",
                startNode = 0, step = 0, startWeek = 1, endWeek = 25,
                type = 0, tableName = ""
        )
    }
}