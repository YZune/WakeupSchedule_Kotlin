package com.suda.yzune.wakeupschedule.schedule_manage

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import kotlin.concurrent.thread

class ScheduleManageViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()
    private val baseDao = dataBase.courseBaseDao()

    val tableSelectList = arrayListOf<TableSelectBean>()
    val courseList = arrayListOf<CourseBaseBean>()

    fun initTableSelectList(): LiveData<List<TableSelectBean>> {
        return tableDao.getTableSelectList()
    }

    fun getCourseBaseBeanListByTable(tableId: Int): LiveData<List<CourseBaseBean>> {
        return baseDao.getCourseBaseBeanOfTable(tableId)
    }

    fun getTableById(id: Int): TableBean {
        return tableDao.getTableByIdInThread(id)
    }

    fun deleteTable(id: Int) {
        thread(name = "deleteTableThread") {
            try {
                tableDao.deleteTable(id)
            } catch (e: Exception) {

            }
        }
    }

    fun deleteCourse(course: CourseBaseBean) {
        thread(name = "deleteCourseThread") {
            try {
                baseDao.deleteCourseBaseBeanOfTable(course.id, course.tableId)
            } catch (e: Exception) {
                Log.d("删除", e.toString())
            }
        }
    }
}