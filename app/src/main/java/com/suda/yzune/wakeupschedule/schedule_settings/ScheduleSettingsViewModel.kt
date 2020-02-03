package com.suda.yzune.wakeupschedule.schedule_settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import java.util.*

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

    fun getCurrentWeek(): Int {
        return CourseUtils.countWeek(table.startDate, table.sundayFirst)
    }

    fun setCurrentWeek(week: Int) {
        val cal = Calendar.getInstance()
        if (table.sundayFirst) {
            cal.firstDayOfWeek = Calendar.SUNDAY
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        } else {
            cal.firstDayOfWeek = Calendar.MONDAY
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        cal.add(Calendar.WEEK_OF_YEAR, -week + 1)
        mYear = cal.get(Calendar.YEAR)
        mMonth = cal.get(Calendar.MONTH) + 1
        mDay = cal.get(Calendar.DATE)
        table.startDate = "${mYear}-${mMonth}-${mDay}"
    }

}