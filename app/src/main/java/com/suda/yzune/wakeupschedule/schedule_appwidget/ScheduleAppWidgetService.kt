package com.suda.yzune.wakeupschedule.schedule_appwidget

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
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countWeek
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import java.text.ParseException

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
        private val timeDao = dataBase.timeDetailDao()
        private val timeList = arrayListOf<TimeDetailBean>()

        override fun onCreate() {

        }

        override fun onDataSetChanged() {
            itemHeight = SizeUtils.dp2px(mContext, PreferenceUtils.getIntFromSP(mContext, "widget_item_height", 56).toFloat())
            marTop = resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
            if (PreferenceUtils.getBooleanFromSP(mContext.applicationContext, "s_show_time_detail", false)) {
                timeList.clear()
                timeList.addAll(timeDao.getTimeListInThread())
            }else{
                timeList.clear()
            }
        }

        override fun onDestroy() {

        }

        override fun getCount(): Int {
            return 1
        }

        override fun getViewAt(position: Int): RemoteViews {
            val mRemoteViews = RemoteViews(mContext.packageName, R.layout.item_schedule_widget)

            initData(mContext, mRemoteViews, PreferenceUtils.getBooleanFromSP(mContext.applicationContext, "s_show_time_detail", false))
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

        fun initView(context: Context, view: View) {
            val weekPanel7 = view.findViewById<View>(R.id.weekPanel_7)
            val title7 = view.findViewById<View>(R.id.title7)
            val weekPanel0 = view.findViewById<LinearLayout>(R.id.weekPanel_0)
            val weekName = view.findViewById<LinearLayout>(R.id.weekName)
            val showWeekend = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show_weekend", true)
            if (showWeekend) {
                weekPanel7.visibility = View.VISIBLE
                title7.visibility = View.VISIBLE
            } else {
                weekPanel7.visibility = View.GONE
                title7.visibility = View.GONE
            }

            for (i in 0 until weekPanel0.childCount) {
                val lp = weekPanel0.getChildAt(i).layoutParams
                lp.height = itemHeight
                weekPanel0.getChildAt(i).layoutParams = lp
            }
            if (PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_widget_color", false)) {
                for (i in 0 until weekPanel0.childCount) {
                    val tv = weekPanel0.getChildAt(i) as TextView
                    tv.setTextColor(resources.getColor(R.color.white))
                }
                for (i in 0 until weekName.childCount) {
                    val tv = weekName.getChildAt(i) as TextView
                    tv.setTextColor(resources.getColor(R.color.white))
                }
            } else {
                for (i in 0 until weekPanel0.childCount) {
                    val tv = weekPanel0.getChildAt(i) as TextView
                    tv.setTextColor(resources.getColor(R.color.black))
                }
                for (i in 0 until weekName.childCount) {
                    val tv = weekName.getChildAt(i) as TextView
                    tv.setTextColor(resources.getColor(R.color.black))
                }
            }

            val nodesNum = PreferenceUtils.getIntFromSP(context.applicationContext, "classNum", 11)
            for (i in 8..nodesNum) {
                val tv = weekPanel0.getChildAt(i) as TextView
                tv.visibility = View.VISIBLE
            }
            for (i in nodesNum + 1 until weekPanel0.childCount) {
                val tv = weekPanel0.getChildAt(i) as TextView
                tv.visibility = View.GONE
            }
        }

        fun initData(context: Context, views: RemoteViews, show: Boolean) {
            try {
                week = countWeek(context)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (week <= 0) {
                week = 1
            }

            val showWeekend = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show_weekend", true)
            val daysEnd = if (showWeekend) 7 else 6

            val view = View.inflate(mContext, R.layout.fragment_schedule, null)
            initView(context, view)

            for (i in 1..daysEnd) {
                val list = baseDao.getCourseByDayInThread(i)
                initWeekPanel(context, view, list, i, true)
            }
            val scrollView = view.findViewById<ScrollView>(R.id.scrollPanel)
            ViewUtils.layoutView(scrollView, SizeUtils.dp2px(context, 375f), SizeUtils.dp2px(context, 375f))
            views.setBitmap(R.id.iv_schedule, "setImageBitmap", ViewUtils.getViewBitmap(scrollView))
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
                myGrad.alpha = Math.round(255 * (PreferenceUtils.getIntFromSP(context.applicationContext, "sb_widget_alpha", 60) / 100.0)).toInt()

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

                if (timeList.isNotEmpty()) {
                    tv.text = timeList[c.startNode - 1].startTime + "\n" + tv.text
                }

                ll.addView(tv)
                pre = c
            }
        }

    }

}