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
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import kotlinx.coroutines.*


/**
 * Implementation of App Widget functionality.
 */
class TodayCourseAppWidget : AppWidgetProvider() {

    private var job: Job? = null

    @SuppressLint("NewApi")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "WAKEUP_REMIND_COURSE") {
            val course = intent.getParcelableExtra<CourseBean>("course")
            val date = intent.getStringExtra("date")
            val weekDay = intent.getStringExtra("weekDay")
            val index = intent.getIntExtra("index", 0)
            val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            val notification = NotificationCompat.Builder(context, "schedule_reminder")
                    .setContentTitle(date)
                    .setContentText(weekDay)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.wakeup)
                    .setAutoCancel(false)
                    .setNumber(2)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setVibrate(longArrayOf(0, 2000))
                    .build()
            manager!!.notify(index, notification)
        }
        super.onReceive(context, intent)
    }

    @SuppressLint("NewApi")
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.appWidgetDao()
        val tableDao = dataBase.tableDao()
        val baseDao = dataBase.courseBaseDao()
        val timeDao = dataBase.timeDetailDao()

        job = GlobalScope.launch(Dispatchers.Main) {

            val table = async(Dispatchers.IO) {
                tableDao.getDefaultTableInThread()
            }.await()

            val week = CourseUtils.countWeek(table.startDate)
            val date = CourseUtils.getTodayDate()
            val weekDay = CourseUtils.getWeekday()

            val courseList = async(Dispatchers.IO) {
                if (week % 2 == 0) {
                    baseDao.getCourseByDayOfTableInThread(CourseUtils.getWeekdayInt(), week, 2, table.id)
                } else {
                    baseDao.getCourseByDayOfTableInThread(CourseUtils.getWeekdayInt(), week, 1, table.id)
                }
            }.await()

            val timeList = async(Dispatchers.IO) {
                timeDao.getTimeListInThread(table.timeTable)
            }.await()

            val manager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val t = System.currentTimeMillis()
            courseList.forEachIndexed { index, courseBean ->
                val i = Intent(context, TodayCourseAppWidget::class.java)
                i.putExtra("course", courseBean)
                i.putExtra("date", date)
                i.putExtra("weekDay", weekDay)
                i.putExtra("index", index)
                i.action = "WAKEUP_REMIND_COURSE"

                val pi = PendingIntent.getBroadcast(context, index, i, PendingIntent.FLAG_UPDATE_CURRENT)

                when {
                    Build.VERSION.SDK_INT < 19 -> {
                        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi)
                    }
                    Build.VERSION.SDK_INT in 19..22 -> {
                        manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi)
                    }
                    Build.VERSION.SDK_INT >= 23 -> {
                        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t + index * 2000, pi)
                    }
                }
            }

            for (appWidget in widgetDao.getWidgetsByTypesInThread(0, 1)) {
                AppWidgetUtils.refreshTodayWidget(context, appWidgetManager, appWidget.id, table)
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

