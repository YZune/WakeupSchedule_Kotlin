package com.suda.yzune.wakeupschedule.schedule_manage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean

class ScheduleManageViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()
    private val courseDao = dataBase.courseDao()
    private val widgetDao = dataBase.appWidgetDao()

    suspend fun initTableSelectList(): MutableList<TableSelectBean> {
        return tableDao.getTableSelectList().toMutableList()
    }

    suspend fun getCourseBaseBeanListByTable(tableId: Int): MutableList<CourseBaseBean> {
        return courseDao.getCourseBaseBeanOfTable(tableId).toMutableList()
    }

    suspend fun getTableById(id: Int): TableBean? {
        return tableDao.getTableById(id)
    }

    suspend fun addBlankTable(tableName: String): Long {
        return tableDao.insertTable(TableBean(id = 0, tableName = tableName))
    }

    suspend fun deleteTable(id: Int) {
        tableDao.deleteTable(id)
    }

    suspend fun clearTable(id: Int) {
        tableDao.clearTable(id)
    }

    suspend fun deleteCourse(course: CourseBaseBean) {
        courseDao.deleteCourseBaseBeanOfTable(course.id, course.tableId)
    }

    suspend fun getScheduleWidgetIds(): List<AppWidgetBean> {
        return widgetDao.getWidgetsByBaseType(0)
    }
}