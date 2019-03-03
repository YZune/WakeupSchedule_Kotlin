package com.suda.yzune.wakeupschedule.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.SplashActivity
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.schedule_appwidget.ScheduleAppWidgetService
import com.suda.yzune.wakeupschedule.today_appwidget.TodayCourseAppWidget
import com.suda.yzune.wakeupschedule.today_appwidget.TodayCourseAppWidgetService

object AppWidgetUtils {
    private val daysArray = arrayOf("日", "一", "二", "三", "四", "五", "六", "日")

    fun updateWidget(context: Context) {
        val intent = Intent()
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        context.sendBroadcast(intent)
    }

    fun refreshScheduleWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, tableBean: TableBean) {
        val mRemoteViews = RemoteViews(context.packageName, R.layout.schedule_app_widget)
        var week = CourseUtils.countWeek(tableBean.startDate, tableBean.sundayFirst)
        val date = CourseUtils.getTodayDate()
        val weekDay = CourseUtils.getWeekday()
        mRemoteViews.setTextViewText(R.id.tv_date, date)
        if (week > 0) {
            mRemoteViews.setTextViewText(R.id.tv_week, "第${week}周    $weekDay")
        } else {
            mRemoteViews.setTextViewText(R.id.tv_week, "还没有开学哦")
            week = 1
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
        val weekDate = CourseUtils.getDateStringFromWeek(CourseUtils.countWeek(tableBean.startDate, tableBean.sundayFirst), week, tableBean.sundayFirst)
        mRemoteViews.setTextColor(R.id.tv_title0, tableBean.widgetTextColor)
        mRemoteViews.setTextViewText(R.id.tv_title0, weekDate[0] + "\n月")
        if (tableBean.sundayFirst) {
            for (i in 0..6) {
                mRemoteViews.setTextColor(R.id.tv_title0_1 + i, tableBean.widgetTextColor)
                mRemoteViews.setTextViewText(R.id.tv_title0_1 + i, daysArray[i] + "\n${weekDate[i + 1]}")
            }
        } else {
            for (i in 0..6) {
                mRemoteViews.setTextColor(R.id.tv_title1 + i, tableBean.widgetTextColor)
                mRemoteViews.setTextViewText(R.id.tv_title1 + i, daysArray[i + 1] + "\n${weekDate[i + 1]}")
            }
        }
        val lvIntent = Intent(context, ScheduleAppWidgetService::class.java)
        mRemoteViews.setRemoteAdapter(R.id.lv_schedule, lvIntent)
        val intent = Intent(context, SplashActivity::class.java)
        val pIntent = PendingIntent.getActivity(context, 0, intent, 0)
        mRemoteViews.setOnClickPendingIntent(R.id.tv_date, pIntent)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_schedule)
        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews)
    }

    fun refreshTodayWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, tableBean: TableBean, nextDay: Boolean = false) {
        val mRemoteViews = RemoteViews(context.packageName, R.layout.today_course_app_widget)
        val week = CourseUtils.countWeek(tableBean.startDate, tableBean.sundayFirst, nextDay)
        val date = CourseUtils.getTodayDate()
        val weekDay = CourseUtils.getWeekday(nextDay)
        mRemoteViews.setTextColor(R.id.tv_date, tableBean.widgetTextColor)
        mRemoteViews.setTextColor(R.id.tv_week, tableBean.widgetTextColor)
        mRemoteViews.setInt(R.id.iv_next, "setColorFilter", tableBean.widgetTextColor)
        mRemoteViews.setTextViewTextSize(R.id.tv_week, TypedValue.COMPLEX_UNIT_SP, tableBean.widgetItemTextSize.toFloat())
        if (nextDay) {
            mRemoteViews.setTextViewText(R.id.tv_date, "明天")
            mRemoteViews.setViewVisibility(R.id.iv_next, View.GONE)
            mRemoteViews.setViewVisibility(R.id.iv_back, View.VISIBLE)
        } else {
            mRemoteViews.setTextViewText(R.id.tv_date, date)
            mRemoteViews.setViewVisibility(R.id.iv_next, View.VISIBLE)
            mRemoteViews.setViewVisibility(R.id.iv_back, View.GONE)
        }
        if (week > 0) {
            mRemoteViews.setTextViewText(R.id.tv_week, "第${week}周    $weekDay")
        } else {
            mRemoteViews.setTextViewText(R.id.tv_week, "还没有开学哦")
        }
        val lvIntent = Intent(context, TodayCourseAppWidgetService::class.java)
        lvIntent.data = if (nextDay) {
            Uri.fromParts("content", "1", null)
        } else {
            Uri.fromParts("content", "0", null)
        }
        mRemoteViews.setRemoteAdapter(R.id.lv_course, lvIntent)
        val intent = Intent(context, SplashActivity::class.java)
        val pIntent = PendingIntent.getActivity(context, 0, intent, 0)
        mRemoteViews.setOnClickPendingIntent(R.id.tv_date, pIntent)

        val i = Intent(context, TodayCourseAppWidget::class.java)
        i.action = "WAKEUP_NEXT_DAY"
        val pi = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT)
        mRemoteViews.setOnClickPendingIntent(R.id.iv_next, pi)

        val backIntent = Intent(context, TodayCourseAppWidget::class.java)
        backIntent.action = "WAKEUP_BACK_TIME"
        val backPi = PendingIntent.getBroadcast(context, 2, backIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mRemoteViews.setOnClickPendingIntent(R.id.iv_back, backPi)

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_course)
        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews)
    }
}