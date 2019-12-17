package com.suda.yzune.wakeupschedule.today_appwidget

import android.content.Intent
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
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

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return if (intent != null) {
            val i = intent.data?.schemeSpecificPart?.toInt()
            return if (i == 1) {
                TodayCourseListRemoteViewsFactory(true)
            } else {
                TodayCourseListRemoteViewsFactory(false)
            }
        } else {
            TodayCourseListRemoteViewsFactory()
        }
    }

    private inner class TodayCourseListRemoteViewsFactory(val nextDay: Boolean = false) : RemoteViewsFactory {

        private val dataBase = AppDatabase.getDatabase(applicationContext)
        private val tableDao = dataBase.tableDao()
        private val courseDao = dataBase.courseDao()
        private val timeDao = dataBase.timeDetailDao()

        private var week = 1
        private lateinit var table: TableBean
        private val timeList = arrayListOf<TimeDetailBean>()
        private val courseList = arrayListOf<CourseBean>()

        override fun onCreate() {
            table = tableDao.getDefaultTable()
        }

        override fun onDataSetChanged() {
            table = tableDao.getDefaultTable()
            try {
                week = CourseUtils.countWeek(table.startDate, table.sundayFirst, nextDay)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            courseList.clear()
            if (week % 2 == 0) {
                courseList.addAll(courseDao.getCourseByDayOfTableInThread(CourseUtils.getWeekdayInt(nextDay), week, 2, table.id))
            } else {
                courseList.addAll(courseDao.getCourseByDayOfTableInThread(CourseUtils.getWeekdayInt(nextDay), week, 1, table.id))
            }
            timeList.clear()
            timeList.addAll(timeDao.getTimeListInThread(table.timeTable))
        }

        override fun onDestroy() {
            timeList.clear()
            courseList.clear()
        }

        override fun getCount(): Int {
            return if (courseList.isEmpty()) {
                1
            } else {
                courseList.size
            }
        }

        override fun getViewAt(position: Int): RemoteViews {
            if (courseList.isNotEmpty()) {
                val mRemoteViews = RemoteViews(applicationContext.packageName, R.layout.item_today_course_widget)
                try {
                    mRemoteViews.setTextColor(R.id.tv_startTime, table.widgetTextColor)
                    mRemoteViews.setTextColor(R.id.tv_endTime, table.widgetTextColor)
                    mRemoteViews.setTextColor(R.id.widget_name, table.widgetTextColor)
                    mRemoteViews.setTextColor(R.id.widget_room, table.widgetTextColor)
                    mRemoteViews.setTextColor(R.id.widget_teacher, table.widgetTextColor)
                    mRemoteViews.setTextColor(R.id.tv_start, table.widgetTextColor)
                    mRemoteViews.setTextColor(R.id.tv_end, table.widgetTextColor)
                    mRemoteViews.setInt(R.id.iv_room, "setColorFilter", table.widgetTextColor)
                    mRemoteViews.setInt(R.id.iv_teacher, "setColorFilter", table.widgetTextColor)
                    mRemoteViews.setTextViewText(R.id.tv_startTime, timeList[courseList[position].startNode - 1].startTime)
                    mRemoteViews.setTextViewText(R.id.tv_endTime, timeList[courseList[position].startNode + courseList[position].step - 2].endTime)
                    mRemoteViews.setTextViewText(R.id.widget_name, courseList[position].courseName)

                    mRemoteViews.setTextViewTextSize(R.id.tv_startTime, COMPLEX_UNIT_SP, table.widgetItemTextSize.toFloat())
                    mRemoteViews.setTextViewTextSize(R.id.tv_endTime, COMPLEX_UNIT_SP, table.widgetItemTextSize.toFloat())
                    mRemoteViews.setTextViewTextSize(R.id.widget_name, COMPLEX_UNIT_SP, table.widgetItemTextSize + 2f)
                    mRemoteViews.setTextViewTextSize(R.id.widget_room, COMPLEX_UNIT_SP, table.widgetItemTextSize.toFloat())
                    mRemoteViews.setTextViewTextSize(R.id.widget_teacher, COMPLEX_UNIT_SP, table.widgetItemTextSize.toFloat())
                    mRemoteViews.setTextViewTextSize(R.id.tv_start, COMPLEX_UNIT_SP, table.widgetItemTextSize.toFloat())
                    mRemoteViews.setTextViewTextSize(R.id.tv_end, COMPLEX_UNIT_SP, table.widgetItemTextSize.toFloat())

                    if (courseList[position].room != "") {
                        mRemoteViews.setTextViewText(R.id.widget_room, courseList[position].room)
                    } else {
                        mRemoteViews.setViewVisibility(R.id.widget_room, View.GONE)
                        mRemoteViews.setViewVisibility(R.id.iv_room, View.GONE)
                    }
                    if (courseList[position].teacher != "") {
                        mRemoteViews.setTextViewText(R.id.widget_teacher, courseList[position].teacher)
                    } else {
                        mRemoteViews.setViewVisibility(R.id.widget_teacher, View.GONE)
                        mRemoteViews.setViewVisibility(R.id.iv_teacher, View.GONE)
                    }
                    if (courseList[position].teacher == "" && courseList[position].room == "") {
                        mRemoteViews.setViewVisibility(R.id.ll_info, View.GONE)
                    }
                    mRemoteViews.setTextViewText(R.id.tv_start, courseList[position].startNode.toString())
                    mRemoteViews.setTextViewText(R.id.tv_end, "${courseList[position].startNode + courseList[position].step - 1}")
                } catch (e: Exception) {
                    mRemoteViews.setTextViewText(R.id.widget_name, ">_<" + e.message)
                }
                return mRemoteViews
            } else {
                val mRemoteViews = RemoteViews(applicationContext.packageName, R.layout.item_today_course_empty)
                mRemoteViews.setTextColor(R.id.tv_empty, table.widgetTextColor)
                return mRemoteViews
            }
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