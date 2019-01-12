package com.suda.yzune.wakeupschedule.schedule_manage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean

class ScheduleManageViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()
    private val baseDao = dataBase.courseBaseDao()
    private val widgetDao = dataBase.appWidgetDao()

    val tableSelectList = arrayListOf<TableSelectBean>()
    val courseList = arrayListOf<CourseBaseBean>()

    fun initTableSelectList(): LiveData<List<TableSelectBean>> {
        return tableDao.getTableSelectList()
    }

    fun getCourseBaseBeanListByTable(tableId: Int): LiveData<List<CourseBaseBean>> {
        return baseDao.getCourseBaseBeanOfTable(tableId)
    }

    suspend fun getTableById(id: Int): TableBean {
        return tableDao.getTableByIdInThread(id)
    }

    suspend fun addBlankTable(tableName: String) {
        tableDao.insertTable(TableBean(id = 0, tableName = tableName))
    }

    suspend fun deleteTable(id: Int) {
        tableDao.deleteTable(id)
    }

    suspend fun deleteCourse(course: CourseBaseBean) {
        baseDao.deleteCourseBaseBeanOfTable(course.id, course.tableId)
    }

    suspend fun getScheduleWidgetIds(): List<AppWidgetBean> {
        return widgetDao.getWidgetsByBaseTypeInThread(0)
    }
}