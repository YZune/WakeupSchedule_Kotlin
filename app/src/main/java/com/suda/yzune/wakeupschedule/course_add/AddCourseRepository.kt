package com.suda.yzune.wakeupschedule.course_add

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import java.util.*
import kotlin.collections.ArrayList

class AddCourseRepository(context: Context) {

    private lateinit var detailList: MutableList<CourseDetailBean>
    private lateinit var baseBean: CourseBaseBean

    private val dataBase = AppDatabase.getDatabase(context)
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()
    private var weekMap = sortedMapOf<Int, MutableLiveData<ArrayList<Int>>>()
    private val deleteList = arrayListOf<Int>()

    fun saveData(newId: Int) {
        if (baseBean.id != -1) {

        }
    }

    fun removeWeek(position: Int) {
        weekMap.remove(position)
        for (i in weekMap.keys) {
            if (i > position) {
                weekMap[i - 1] = weekMap[i]
                weekMap.remove(i)
            }
        }
    }

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

    fun getDeleteList(): ArrayList<Int>{
        return deleteList
    }

    fun getLastId(): LiveData<Int> {
        return baseDao.getLastId()
    }

    fun initBaseData(): CourseBaseBean {
        baseBean = CourseBaseBean(-1, "", "", "")
        return baseBean
    }

    fun initBaseData(id: Int): LiveData<CourseBaseBean> {
        baseBean = CourseBaseBean(-1, "", "", "")
        return baseDao.getCourseById(id)
    }

    fun getBaseData(): CourseBaseBean {
        return baseBean
    }

    fun getWeekMap(): SortedMap<Int, MutableLiveData<ArrayList<Int>>> {
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