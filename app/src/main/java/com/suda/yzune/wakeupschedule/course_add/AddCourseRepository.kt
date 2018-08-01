package com.suda.yzune.wakeupschedule.course_add

import android.arch.lifecycle.LiveData
import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean

class AddCourseRepository(context: Context) {

    lateinit var detailList: MutableList<CourseDetailBean>

    private val dataBase = AppDatabase.getDatabase(context)
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()
    private var weekMap = mutableMapOf<Int, ArrayList<Int>>()

    fun initData(): MutableList<CourseDetailBean> {
        detailList = mutableListOf(newBlankCourse())
        return detailList
    }

    fun initData(id: Int): LiveData<List<CourseDetailBean>> {
        detailList = mutableListOf()
        return detailDao.getDetailById(id)
    }

    fun getList(): MutableList<CourseDetailBean> {
        return detailList
    }

    fun getLastId(): LiveData<Int> {
        return baseDao.getLastId()
    }

    fun initBaseData(id: Int): LiveData<CourseBaseBean>{
        return baseDao.getCourseById(id)
    }

    fun getWeekMap(): MutableMap<Int, ArrayList<Int>> {
        return weekMap
    }

    fun newBlankCourse(): CourseDetailBean {
        return CourseDetailBean(
                id = -1, day = 0, teacher = "", room = "",
                startNode = 0, step = 0, startWeek = 1, endWeek = 30,
                type = 0, tableName = ""
        )
    }
}