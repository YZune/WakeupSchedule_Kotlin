package com.suda.yzune.wakeupschedule.today_appwidget

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.SplashActivity
import com.suda.yzune.wakeupschedule.utils.*
import java.util.*


/**
 * Implementation of App Widget functionality.
 */
class TodayCourseAppWidget : AppWidgetProvider() {

    private var calendar = Calendar.getInstance()

    @SuppressLint("NewApi")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "WAKEUP_REMIND_COURSE") {
            if (context.getPrefer().getBoolean(Const.KEY_COURSE_REMIND, false)) {
                val courseName = intent.getStringExtra("courseName")
                var room = intent.getStringExtra("room")
                val time = intent.getStringExtra("time")
                val weekDay = intent.getStringExtra("weekDay")
                val index = intent.getIntExtra("index", 0)

                if (room == "") {
                    room = "未知"
                }

                val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                val cancelIntent = Intent(context, TodayCourseAppWidget::class.java).apply {
                    action = "WAKEUP_CANCEL_REMINDER"
                    putExtra("index", index)
                }
                val cancelPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, index, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val openIntent = Intent(context, SplashActivity::class.java)
                val openPendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, openIntent, 0)

                val notification = NotificationCompat.Builder(context, "schedule_reminder")
                        .setContentTitle("$time $courseName")
                        .setSubText("上课提醒")
                        .setContentText("$weekDay  地点：$room")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.wakeup)
                        .setAutoCancel(false)
                        .setOngoing(context.getPrefer().getBoolean(Const.KEY_REMINDER_ON_GOING, false))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                        .setVibrate(longArrayOf(0, 5000, 500, 5000))
                        .addAction(R.drawable.wakeup, "记得给手机静音哦", cancelPendingIntent)
                        .addAction(R.drawable.wakeup, "我知道啦", cancelPendingIntent)
                        .setContentIntent(openPendingIntent)
                manager.notify(index, notification.build())
            }
        }
        if (intent.action == "WAKEUP_NEXT_DAY") {
            val dataBase = AppDatabase.getDatabase(context)
            val widgetDao = dataBase.appWidgetDao()
            val tableDao = dataBase.tableDao()
            goAsync {
                val table = tableDao.getDefaultTable()
                for (appWidget in widgetDao.getWidgetsByTypes(0, 1)) {
                    AppWidgetUtils.refreshTodayWidget(context, AppWidgetManager.getInstance(context), appWidget.id, table, true)
                }
            }
        }
        if (intent.action == "WAKEUP_BACK_TIME") {
            val dataBase = AppDatabase.getDatabase(context)
            val widgetDao = dataBase.appWidgetDao()
            val tableDao = dataBase.tableDao()
            goAsync {
                val table = tableDao.getDefaultTable()
                for (appWidget in widgetDao.getWidgetsByTypes(0, 1)) {
                    AppWidgetUtils.refreshTodayWidget(context, AppWidgetManager.getInstance(context), appWidget.id, table)
                }
            }
        }
        if (intent.action == "WAKEUP_CANCEL_REMINDER") {
            val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(intent.getIntExtra("index", 0))
        }
        super.onReceive(context, intent)
    }

    @SuppressLint("NewApi")
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.appWidgetDao()
        val tableDao = dataBase.tableDao()
        val courseDao = dataBase.courseDao()
        val timeDao = dataBase.timeDetailDao()

        goAsync {
            val table = tableDao.getDefaultTable()
            if (context.getPrefer().getBoolean(Const.KEY_COURSE_REMIND, false)) {
                val week = CourseUtils.countWeek(table.startDate, table.sundayFirst)
                if (week >= 0) {
                    val weekDay = CourseUtils.getWeekday()
                    val before = context.getPrefer().getInt(Const.KEY_REMINDER_TIME, 20)
                    val type = if (week % 2 == 0) 2 else 1
                    val courseList = courseDao.getCourseByDayOfTable(CourseUtils.getWeekdayInt(), week, type, table.id)

                    val timeList = timeDao.getTimeList(table.timeTable)

                    val manager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                    courseList.forEachIndexed { index, courseBean ->

                        val time = timeList[courseBean.startNode - 1].startTime
                        val timeSplit = time.split(":")
                        calendar.timeInMillis = System.currentTimeMillis()
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeSplit[0]))
                        calendar.set(Calendar.MINUTE, Integer.parseInt(timeSplit[1]))
                        calendar.add(Calendar.MINUTE, 0 - before)

                        if (calendar.timeInMillis < System.currentTimeMillis()) {
                            return@forEachIndexed
                        }

                        val i = Intent(context, TodayCourseAppWidget::class.java)
                        i.putExtra("courseName", courseBean.courseName)
                        i.putExtra("room", courseBean.room)
                        i.putExtra("weekDay", weekDay)
                        i.putExtra("index", index)
                        i.putExtra("time", time)
                        i.action = "WAKEUP_REMIND_COURSE"

                        val pi = PendingIntent.getBroadcast(context, index, i, PendingIntent.FLAG_UPDATE_CURRENT)

                        when {
                            Build.VERSION.SDK_INT in 19..22 -> {
                                manager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
                            }
                            Build.VERSION.SDK_INT >= 23 -> {
                                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
                            }
                        }
                    }
                }
            }

            for (appWidget in widgetDao.getWidgetsByTypes(0, 1)) {
                AppWidgetUtils.refreshTodayWidget(context, appWidgetManager, appWidget.id, table)
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.appWidgetDao()
        goAsync {
            for (id in appWidgetIds) {
                widgetDao.deleteAppWidget(id)
            }
        }
    }
}

