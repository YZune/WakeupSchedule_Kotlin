package com.suda.yzune.wakeupschedule.today_appwidget

import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import java.text.ParseException

class TodayCourseAppWidgetService : RemoteViewsService() {

    private var week = 1
    private lateinit var table: TableBean
    private val timeList = arrayListOf<TimeDetailBean>()
    private val courseList = arrayListOf<CourseBean>()

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return TodayCourseListRemoteViewsFactory()
    }

    private inner class TodayCourseListRemoteViewsFactory : RemoteViewsService.RemoteViewsFactory {
        private val dataBase = AppDatabase.getDatabase(applicationContext)
        private val tableDao = dataBase.tableDao()
        private val baseDao = dataBase.courseBaseDao()
        private val timeDao = dataBase.timeDetailDao()

        override fun onCreate() {

        }

        override fun onDataSetChanged() {
            table = tableDao.getDefaultTableInThread()
            try {
                week = CourseUtils.countWeek(table.startDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            courseList.clear()
            courseList.addAll(baseDao.getCourseByDayOfTableInThread(CourseUtils.getWeekdayInt(), week, table.id))
            timeList.clear()
            timeList.addAll(timeDao.getTimeListInThread(table.timeTable))
        }

        override fun onDestroy() {

        }

        override fun getCount(): Int {
            return courseList.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            val mRemoteViews = RemoteViews(applicationContext.packageName, R.layout.item_today_course_widget)
            mRemoteViews.setTextViewText(R.id.tv_startTime, timeList[courseList[position].startNode - 1].startTime)
            mRemoteViews.setTextViewText(R.id.tv_endTime, timeList[courseList[position].startNode + courseList[position].step - 2].startTime)
            mRemoteViews.setTextViewText(R.id.widget_name, courseList[position].courseName)
            mRemoteViews.setTextViewText(R.id.widget_room, courseList[position].room)
            mRemoteViews.setTextViewText(R.id.widget_teacher, courseList[position].teacher)
            mRemoteViews.setTextViewText(R.id.tv_start, courseList[position].startNode.toString())
            mRemoteViews.setTextViewText(R.id.tv_end, "${courseList[position].startNode + courseList[position].step - 1}")
            return mRemoteViews
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return false
        }
    }

}