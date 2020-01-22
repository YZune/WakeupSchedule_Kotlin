package com.suda.yzune.wakeupschedule.schedule_settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.bean.TableBean

class ScheduleSettingsViewModel(application: Application) : AndroidViewModel(application) {

    var mYear = 2018
    var mMonth = 9
    var mDay = 20
    lateinit var table: TableBean
    lateinit var termStartList: List<String>

    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()
    private val widgetDao = dataBase.appWidgetDao()

    suspend fun saveSettings() {
        tableDao.updateTable(table)
    }

    suspend fun getScheduleWidgetIds(): List<AppWidgetBean> {
        return widgetDao.getWidgetsByBaseType(0)
    }
}