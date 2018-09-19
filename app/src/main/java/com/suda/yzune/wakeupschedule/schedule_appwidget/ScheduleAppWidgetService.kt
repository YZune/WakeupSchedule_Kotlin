package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.*
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
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
        private var week = 0
        private var itemHeight = 0
        private var marTop = 0
        private var showWhite = true
        private var showSunday = true
        private var sundayFirst = false
        private var showSat = true
        private var showStroke = true
        private var showNone = true
        private var nodesNum = 11
        private var textSize = 12
        private var alpha = 0
        private val dataBase = AppDatabase.getDatabase(mContext)
        private val baseDao = dataBase.courseBaseDao()
        private val timeDao = dataBase.timeDetailDao()
        private val tableDao = dataBase.tableDao()
        private val timeList = arrayListOf<TimeDetailBean>()
        private val summerTimeList = arrayListOf<TimeDetailBean>()
        private lateinit var table: TableBean

        override fun onCreate() {

        }

        override fun onDataSetChanged() {
            //table = tableDao.getTableByNameInThread()
            itemHeight = SizeUtils.dp2px(mContext, PreferenceUtils.getIntFromSP(mContext, "widget_item_height", 56).toFloat())
            marTop = resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
            if (PreferenceUtils.getBooleanFromSP(mContext.applicationContext, "s_show_time_detail", false)) {
                timeList.clear()
                summerTimeList.clear()
                //todo:timeList.addAll(timeDao.getTimeListInThread())
            } else {
                timeList.clear()
                summerTimeList.clear()
            }
        }

        override fun onDestroy() {

        }

        override fun getCount(): Int {
            return 1
        }

        override fun getViewAt(position: Int): RemoteViews {
            val mRemoteViews = RemoteViews(mContext.packageName, R.layout.item_schedule_widget)
            initData(mContext, mRemoteViews)
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

        fun initView(view: View, weekPanel0: View, context: Context) {
            val weekPanel7 = view.findViewById<View>(R.id.weekPanel_7)
            val weekPanel6 = view.findViewById<View>(R.id.weekPanel_6)

            if (showSunday) {
                if (sundayFirst) {
                    weekPanel7.visibility = View.GONE
                    weekPanel0.visibility = View.VISIBLE
                } else {
                    weekPanel7.visibility = View.VISIBLE
                    weekPanel0.visibility = View.GONE
                }
            } else {
                weekPanel7.visibility = View.GONE
                weekPanel0.visibility = View.GONE
            }

            if (showSat) {
                weekPanel6.visibility = View.VISIBLE
            } else {
                weekPanel6.visibility = View.GONE
            }

            for (i in 0 until 16) {
                val tv = view.findViewById<TextView>(R.id.tv_node1 + i)
                val lp = tv.layoutParams
                lp.height = itemHeight
                tv.layoutParams = lp
                if (showWhite) {
                    tv.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.white))
                } else {
                    tv.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.black))
                }
                if (i >= nodesNum) {
                    tv.visibility = View.GONE
                } else {
                    tv.visibility = View.VISIBLE
                }
            }
        }

        fun initData(context: Context, views: RemoteViews) {
            try {
                week = countWeek("")
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (week <= 0) {
                week = 1
            }

            alpha = PreferenceUtils.getIntFromSP(context.applicationContext, "sb_widget_alpha", 60)
            showStroke = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_stroke", true)
            showNone = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show", false)
            nodesNum = PreferenceUtils.getIntFromSP(context.applicationContext, "classNum", 11)
            showWhite = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_widget_color", false)
            showSat = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show_sat", true)
            showSunday = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show_weekend", true)
            sundayFirst = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_sunday_first", false)
            textSize = PreferenceUtils.getIntFromSP(context.applicationContext, "sb_widget_text_size", 12)

            val view = View.inflate(mContext, R.layout.fragment_schedule, null)
            val weekPanel0 = view.findViewById<LinearLayout>(R.id.weekPanel_0)
            initView(view, weekPanel0, context)

            for (i in 1..7) {
                val list = baseDao.getCourseByDayOfTableInThread(i)
                initWeekPanel(weekPanel0, context, view, list, i)
            }
            val scrollView = view.findViewById<ScrollView>(R.id.scrollPanel)
            ViewUtils.layoutView(scrollView, SizeUtils.dp2px(context, 375f), SizeUtils.dp2px(context, 375f))
            views.setBitmap(R.id.iv_schedule, "setImageBitmap", ViewUtils.getViewBitmap(scrollView))
        }

        private fun initWeekPanel(weekPanel0: LinearLayout, context: Context, view: View, data: List<CourseBean>?, day: Int) {
            val llIndex = day - 1
            val ll = view.findViewById<LinearLayout>(R.id.weekPanel_1 + llIndex)
            ll.removeAllViews()
            if (data == null || data.isEmpty()) return
            var pre = data[0]
            for (i in data.indices) {
                val strBuilder = StringBuilder()
                val c = data[i]
                val tv = TextView(context)
                val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        itemHeight * c.step + marTop * (c.step - 1))
                if (i > 0) {
                    lp.setMargins(0, (c.startNode - (pre.startNode + pre.step)) * (itemHeight + marTop) + marTop, 0, 0)
                } else {
                    lp.setMargins(0, (c.startNode - 1) * (itemHeight + marTop) + marTop, 0, 0)
                }
                tv.layoutParams = lp
                //tv.gravity = Gravity.CENTER_VERTICAL
                tv.textSize = textSize.toFloat()
                tv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                tv.setPadding(8, 8, 8, 8)
                tv.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.white))

                tv.background = ContextCompat.getDrawable(context.applicationContext, R.drawable.course_item_bg)
                val myGrad = tv.background as GradientDrawable
                if (!showStroke) {
                    myGrad.setStroke(SizeUtils.dp2px(context.applicationContext, 2f), ContextCompat.getColor(context.applicationContext, R.color.transparent))
                } else {
                    myGrad.setStroke(SizeUtils.dp2px(context.applicationContext, 2f), Color.parseColor("#80ffffff"))
                }

                val alphaInt = Math.round(255 * (alpha.toFloat() / 100))
                val a = if (alphaInt != 0) {
                    Integer.toHexString(alphaInt)
                } else {
                    "00"
                }

                if (c.color.length == 7) {
                    myGrad.setColor(Color.parseColor("#$a${c.color.substring(1, 7)}"))
                } else {
                    myGrad.setColor(Color.parseColor("#$a${c.color.substring(3, 9)}"))
                }

                strBuilder.append(c.courseName)

                if (c.room != "") {
                    strBuilder.append("@${c.room}")
                }

                when (c.type) {
                    1 -> {
                        strBuilder.append("\n单周")
                        if (week % 2 == 0) {
                            if (showNone) {
                                strBuilder.append("\n单周[非本周]")
                                tv.visibility = View.VISIBLE
                                tv.alpha = 0.6f
                                myGrad.setColor(ContextCompat.getColor(context.applicationContext, R.color.grey))
                            } else {
                                tv.visibility = View.INVISIBLE
                            }
                        }
                    }
                    2 -> {
                        strBuilder.append("\n双周")
                        if (week % 2 != 0) {
                            if (showNone) {
                                tv.alpha = 0.6f
                                strBuilder.append("\n双周[非本周]")
                                tv.visibility = View.VISIBLE
                                myGrad.setColor(ContextCompat.getColor(context.applicationContext, R.color.grey))
                            } else {
                                tv.visibility = View.INVISIBLE
                            }
                        }
                    }
                }

                if (c.startWeek > week || c.endWeek < week) {
                    if (showNone) {
                        tv.alpha = 0.6f
                        strBuilder.append("[非本周]")
                        tv.visibility = View.VISIBLE
                        myGrad.setColor(ContextCompat.getColor(context.applicationContext, R.color.grey))
                    } else {
                        tv.visibility = View.INVISIBLE
                    }
                }

                if (timeList.isNotEmpty()) {
                    strBuilder.insert(0, timeList[c.startNode - 1].startTime + "\n")
                }
                if (summerTimeList.isNotEmpty()) {
                    strBuilder.insert(0, summerTimeList[c.startNode - 1].startTime + "\n")
                }

                tv.text = strBuilder
                if (day == 7) {
                    if (sundayFirst) {
                        weekPanel0.addView(tv)
                    } else {
                        ll.addView(tv)
                    }
                } else {
                    ll.addView(tv)
                }
                pre = c
            }
        }

    }

}