package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.UpdateUtils

/**
 * Implementation of App Widget functionality.
 */
class ScheduleAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        UpdateUtils.tranOldData(context.applicationContext)
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.appWidgetDao()
        val tableDao = dataBase.tableDao()

        for (appWidget in widgetDao.getWidgetsByTypesInThread(0, 0)) {
            AppWidgetUtils.refreshScheduleWidget(context, appWidgetManager, appWidget.id, tableDao.getTableByIdInThread(Integer.parseInt(appWidget.info)))
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.appWidgetDao()
        for (id in appWidgetIds) {
            widgetDao.deleteAppWidget(id)
        }
    }

}

