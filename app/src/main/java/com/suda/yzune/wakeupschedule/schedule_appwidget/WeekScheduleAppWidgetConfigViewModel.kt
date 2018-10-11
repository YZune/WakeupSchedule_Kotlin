package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import kotlin.concurrent.thread

class WeekScheduleAppWidgetConfigViewModel(application: Application) : AndroidViewModel(application) {
    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()
    private val widgetDao = dataBase.appWidgetDao()

    fun initTableSelectList(): LiveData<List<TableSelectBean>> {
        return tableDao.getTableSelectList()
    }

    fun getTableData(id: Int): LiveData<TableBean> {
        return tableDao.getTableById(id)
    }

    fun insertWeekAppWidgetData(appWidget: AppWidgetBean) {
        thread(name = "insertWeekAppWidgetDataThread") {
            try {
                widgetDao.insertAppWidget(appWidget)
            } catch (e: Exception) {

            }
        }
    }
}