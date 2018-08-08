package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.widget.*
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.schedule.CourseDetailFragment
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countWeek
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_schedule.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleAppWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ScheduleRemoteViewsFactory(this.applicationContext, intent)
    }

    private inner class ScheduleRemoteViewsFactory(private val mContext: Context, intent: Intent) : RemoteViewsService.RemoteViewsFactory {

        var week = 0
        var itemHeight = 0
        var marTop = 0
        private val dataBase = AppDatabase.getDatabase(mContext)
        private val baseDao = dataBase.courseBaseDao()

        override fun onCreate() {

        }

        override fun onDataSetChanged() {
            itemHeight = SizeUtils.dp2px(mContext, 56f)
            marTop = resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
        }

        override fun onDestroy() {

        }

        override fun getCount(): Int {
            return 1
        }

        override fun getViewAt(position: Int): RemoteViews {
            val mRemoteViews = RemoteViews(mContext.packageName, R.layout.item_schedule_widget)

            initView(mContext, mRemoteViews)
            initData(mContext, mRemoteViews, position)
//            val intent = Intent(ScheduleAppWidget.ITEM_CLICK)
//            mRemoteViews.setOnClickFillInIntent(R.id.ll_contentPanel, intent)
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

        fun initView(context: Context, views: RemoteViews) {
            val nodesNum = PreferenceUtils.getIntFromSP(context.applicationContext, "classNum", 11)
            for (i in 0..nodesNum - 8) {
                views.setViewVisibility(R.id.tv_9 + i, View.VISIBLE)
            }
            for (i in nodesNum - 7 until 9) {
                views.setViewVisibility(R.id.tv_9 + i, View.GONE)
            }
        }

        fun initData(context: Context, views: RemoteViews, position: Int) {
            try {
                week = countWeek(context)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val showWeekend = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show_weekend", true)
            val daysEnd = if (showWeekend) 7 else 6

            val view = View.inflate(mContext, R.layout.fragment_schedule, null)

            for (i in 1..daysEnd) {
                val list = baseDao.getCourseByDayInThread(i)
                val sortedList = list.sortedBy { it.startNode }
                initWeekPanel(views, context, sortedList, i, false)
                initWeekPanel(context, view, list, i, false)
            }
            val scrollView = view.findViewById<ScrollView>(R.id.scrollPanel)
            ViewUtils.layoutView(scrollView, 600, 600)
            ViewUtils.saveImg(ViewUtils.getViewBitmap(scrollView))
        }

        private fun initWeekPanel(views: RemoteViews, context: Context, data: List<CourseBean>?, day: Int, show: Boolean) {
            val llIndex = R.id.weekPanel_1 + day - 1
            views.removeAllViews(llIndex)
            if (data == null || data.isEmpty()) return
            var pre = data[0]
            for (i in data.indices) {
                val c = data[i]
                val tv = RemoteViews(context.packageName, R.layout.item_textview)
                tv.setInt(R.id.item_tv, "setHeight", itemHeight * c.step + marTop * (c.step - 1))
                if (i > 0) {
                    val tv1 = RemoteViews(context.packageName, R.layout.item_textview)
                    tv1.setInt(R.id.item_tv, "setHeight", (c.startNode - (pre.startNode + pre.step)) * (itemHeight + marTop) + marTop)
                    views.addView(llIndex, tv1)
                } else {
                    val tv1 = RemoteViews(context.packageName, R.layout.item_textview)
                    tv1.setInt(R.id.item_tv, "setHeight", (c.startNode - 1) * (itemHeight + marTop) + marTop)
                    views.addView(llIndex, tv1)
                }
                val a: String = if (Math.round(255 * (64.0 / 100)) != 0L) {
                    Integer.toHexString(Math.round(255 * (64.0 / 100)).toInt())
                } else {
                    "00"
                }
                tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#" + a + c.color.substring(3)))

                when (c.type) {
                    0 -> tv.setTextViewText(R.id.item_tv, c.courseName + "@" + c.room)
                    1 -> {
                        tv.setTextViewText(R.id.item_tv, c.courseName + "@" + c.room + "\n单周")
                        if (week % 2 == 0) {
                            if (show) {
                                tv.setTextViewText(R.id.item_tv, c.courseName + "@" + c.room + "\n单周[非本周]")
                                tv.setViewVisibility(R.id.item_tv, View.VISIBLE)
                                tv.setFloat(R.id.item_tv, "setAlpha", 0.6f)
                                tv.setInt(R.id.item_tv, "setBackgroundColor", resources.getColor(R.color.grey))
                            } else {
                                tv.setViewVisibility(R.id.item_tv, View.INVISIBLE)
                            }
                        }
                    }
                    2 -> {
                        tv.setTextViewText(R.id.item_tv, c.courseName + "@" + c.room + "\n双周")
                        if (week % 2 != 0) {
                            if (show) {
                                tv.setTextViewText(R.id.item_tv, c.courseName + "@" + c.room + "\n双周[非本周]")
                                tv.setViewVisibility(R.id.item_tv, View.VISIBLE)
                                tv.setFloat(R.id.item_tv, "setAlpha", 0.6f)
                                tv.setInt(R.id.item_tv, "setBackgroundColor", resources.getColor(R.color.grey))
                            } else {
                                tv.setViewVisibility(R.id.item_tv, View.INVISIBLE)
                            }
                        }
                    }
                }

                views.addView(llIndex, tv)
                pre = c
            }
        }

        private fun initWeekPanel(context: Context, view: View, data: List<CourseBean>?, day: Int, show: Boolean) {
            val llIndex = day - 1
            val ll = view.findViewById<View>(R.id.weekPanel_1 + llIndex) as LinearLayout?
            ll!!.removeAllViews()
            if (data == null || data.isEmpty()) return
            var pre = data[0]
            for (i in data.indices) {
                Log.d("旋转", "更新了")
                val c = data[i]
                val tv = TextView(context)
                val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        itemHeight * c.step + marTop * (c.step - 1))
                if (i > 0) {
                    lp.setMargins(0, (c.startNode - (pre.startNode + pre.step)) * (itemHeight + marTop) + marTop, 0, 0)
                } else {
                    lp.setMargins(0, (c.startNode - 1) * (itemHeight + marTop) + marTop, 0, 0)
                }
                tv.layoutParams = lp
                //tv.gravity = Gravity.CENTER_VERTICAL
                tv.textSize = 12f
                tv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                tv.setPadding(8, 8, 8, 8)
                tv.setTextColor(resources.getColor(R.color.white))

                tv.background = resources.getDrawable(R.drawable.course_item_bg)
                val myGrad = tv.background as GradientDrawable
                myGrad.setColor(Color.parseColor(c.color))
                myGrad.alpha = Math.round(255 * (60.0 / 100)).toInt()

                when (c.type) {
                    0 -> tv.text = c.courseName + "@" + c.room
                    1 -> {
                        tv.text = c.courseName + "@" + c.room + "\n单周"
                        if (week % 2 == 0) {
                            if (show) {
                                tv.text = c.courseName + "@" + c.room + "\n单周[非本周]"
                                tv.visibility = View.VISIBLE
                                tv.alpha = 0.6f
                                myGrad.setColor(resources.getColor(R.color.grey))
                            } else {
                                tv.visibility = View.INVISIBLE
                            }
                        }
                    }
                    2 -> {
                        tv.text = c.courseName + "@" + c.room + "\n双周"
                        if (week % 2 != 0) {
                            if (show) {
                                tv.alpha = 0.6f
                                tv.text = c.courseName + "@" + c.room + "\n双周[非本周]"
                                tv.visibility = View.VISIBLE
                                myGrad.setColor(resources.getColor(R.color.grey))
                            } else {
                                tv.visibility = View.INVISIBLE
                            }
                        }
                    }
                }

                if (c.startWeek > week || c.endWeek < week) {
                    if (show) {
                        tv.alpha = 0.6f
                        tv.text = c.courseName + "@" + c.room + "[非本周]"
                        tv.visibility = View.VISIBLE
                        myGrad.setColor(resources.getColor(R.color.grey))
                    } else {
                        tv.visibility = View.INVISIBLE
                    }
                }

                ll.addView(tv)
                pre = c
            }
        }

    }

}