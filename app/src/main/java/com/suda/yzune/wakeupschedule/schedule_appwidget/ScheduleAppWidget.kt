package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.UpdateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Implementation of App Widget functionality.
 */
class ScheduleAppWidget : AppWidgetProvider() {

    private var job: Job? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        UpdateUtils.tranOldData(context.applicationContext)
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.appWidgetDao()
        val tableDao = dataBase.tableDao()
        job = GlobalScope.launch(Dispatchers.IO) {
            val table = tableDao.getDefaultTableInThread()
            for (appWidget in widgetDao.getWidgetsByTypesInThread(0, 0)) {
                AppWidgetUtils.refreshScheduleWidget(context, appWidgetManager, appWidget.id, table)
            }
            job?.cancel()
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.appWidgetDao()
        job = GlobalScope.launch(Dispatchers.IO) {
            for (id in appWidgetIds) {
                widgetDao.deleteAppWidget(id)
            }
            job?.cancel()
        }
    }

}

