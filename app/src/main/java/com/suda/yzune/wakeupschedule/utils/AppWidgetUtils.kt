package com.suda.yzune.wakeupschedule.utils

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.schedule_appwidget.ScheduleAppWidgetService

object AppWidgetUtils {
    fun refreshScheduleWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, tableBean: TableBean) {
        val mRemoteViews = RemoteViews(context.packageName, R.layout.schedule_app_widget)
        val week = CourseUtils.countWeek(tableBean.startDate)
        val date = CourseUtils.getTodayDate()
        val weekDay = CourseUtils.getWeekday()
        mRemoteViews.setTextViewText(R.id.tv_date, date)
        if (week > 0) {
            mRemoteViews.setTextViewText(R.id.tv_week, "第${week}周    $weekDay")
        } else {
            mRemoteViews.setTextViewText(R.id.tv_week, "还没有开学哦")
        }

        if (tableBean.showSun) {
            if (tableBean.sundayFirst) {
                mRemoteViews.setViewVisibility(R.id.tv_title7, View.GONE)
                mRemoteViews.setViewVisibility(R.id.tv_title0_1, View.VISIBLE)
            } else {
                mRemoteViews.setViewVisibility(R.id.tv_title7, View.VISIBLE)
                mRemoteViews.setViewVisibility(R.id.tv_title0_1, View.GONE)
            }
        } else {
            mRemoteViews.setViewVisibility(R.id.tv_title7, View.GONE)
            mRemoteViews.setViewVisibility(R.id.tv_title0_1, View.GONE)
        }

        if (tableBean.showSat) {
            mRemoteViews.setViewVisibility(R.id.tv_title6, View.VISIBLE)
        } else {
            mRemoteViews.setViewVisibility(R.id.tv_title6, View.GONE)
        }

        mRemoteViews.setTextColor(R.id.tv_date, tableBean.widgetTextColor)
        mRemoteViews.setTextColor(R.id.tv_week, tableBean.widgetTextColor)
        for (i in 0 until 8) {
            mRemoteViews.setTextColor(R.id.tv_title0_1 + i, tableBean.widgetTextColor)
        }

        val lvIntent = Intent(context, ScheduleAppWidgetService::class.java)
        lvIntent.putExtra("tableName", tableBean.tableName)
        mRemoteViews.setRemoteAdapter(R.id.lv_schedule, lvIntent)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_schedule)
        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews)
    }
}