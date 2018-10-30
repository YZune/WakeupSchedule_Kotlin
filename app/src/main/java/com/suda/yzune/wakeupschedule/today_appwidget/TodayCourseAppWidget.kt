package com.suda.yzune.wakeupschedule.today_appwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils

/**
 * Implementation of App Widget functionality.
 */
class TodayCourseAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.appWidgetDao()
        val tableDao = dataBase.tableDao()
        val table = tableDao.getDefaultTableInThread()
        for (appWidget in widgetDao.getWidgetsByTypesInThread(0, 1)) {
            AppWidgetUtils.refreshTodayWidget(context, appWidgetManager, appWidget.id, table)
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

