package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.TextView
import androidx.core.view.setPadding
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.schedule.ScheduleUI
import com.suda.yzune.wakeupschedule.utils.Const
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countWeek
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.suda.yzune.wakeupschedule.utils.getPrefer
import com.suda.yzune.wakeupschedule.widget.TipTextView
import splitties.dimensions.dip
import java.text.ParseException
import kotlin.math.roundToInt

class ScheduleAppWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        if (intent != null) {
            val list = intent.data?.schemeSpecificPart?.split(",")
                    ?: return ScheduleRemoteViewsFactory()
            if (list.size < 2) {
                return ScheduleRemoteViewsFactory(nextWeek = (list[0] == "1"))
            }
            return if (list[0] == "1") {
                ScheduleRemoteViewsFactory(list[1].toInt(), true)
            } else {
                ScheduleRemoteViewsFactory(list[1].toInt(), false)
            }
        } else {
            return ScheduleRemoteViewsFactory()
        }
    }

    private inner class ScheduleRemoteViewsFactory(val tableId: Int = -1, val nextWeek: Boolean = false) : RemoteViewsFactory {
        private lateinit var table: TableBean
        private var week = 0
        private var widgetItemHeight = 0
        private var marTop = 0
        private var alphaInt = 255
        private val dataBase = AppDatabase.getDatabase(applicationContext)
        private val tableDao = dataBase.tableDao()
        private val courseDao = dataBase.courseDao()
        private val timeDao = dataBase.timeDetailDao()
        private val timeList = arrayListOf<TimeDetailBean>()
        private val weekDay = CourseUtils.getWeekdayInt()
        private val allCourseList = Array(7) { listOf<CourseBean>() }

        override fun onCreate() {

        }

        override fun onDataSetChanged() {
            table = if (tableId == -1) {
                tableDao.getDefaultTableSync()
            } else {
                tableDao.getTableByIdSync(tableId) ?: tableDao.getDefaultTableSync()
            }

            try {
                week = if (nextWeek) countWeek(table.startDate, table.sundayFirst) + 1
                else countWeek(table.startDate, table.sundayFirst)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (week <= 0) {
                week = 1
            }

            widgetItemHeight = dip(table.widgetItemHeight)
            marTop = resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
            alphaInt = (255 * (table.widgetItemAlpha.toFloat() / 100)).roundToInt()

            for (i in 1..7) {
                allCourseList[i - 1] = courseDao.getCourseByDayOfTableSync(i, table.id)
            }

            timeList.clear()
            timeList.addAll(timeDao.getTimeListSync(table.timeTable))
        }

        override fun onDestroy() {
            timeList.clear()
        }

        override fun getCount(): Int {
            return 1
        }

        override fun getViewAt(position: Int): RemoteViews {
            val mRemoteViews = RemoteViews(applicationContext.packageName, R.layout.item_schedule_widget)
            initData(mRemoteViews)
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

        fun initData(views: RemoteViews) {
            val ui = ScheduleUI(applicationContext, table, weekDay, true)
            val showTimeDetail = applicationContext.getPrefer().getBoolean(Const.KEY_SCHEDULE_DETAIL_TIME, true)
            ui.showTimeDetail = showTimeDetail
            if (timeList.isNotEmpty() && showTimeDetail) {
                for (i in 0 until table.nodes) {
                    (ui.content.getViewById(R.id.anko_tv_node1 + i) as FrameLayout).apply {
                        findViewById<TextView>(R.id.tv_start).text = timeList[i].startTime
                        findViewById<TextView>(R.id.tv_end).text = timeList[i].endTime
                    }
                }
            }
            for (i in 1..7) {
                initWeekPanel(ui, allCourseList[i - 1], i)
            }
            val scrollView = ui.scrollView
            val info = ViewUtils.getScreenInfo(applicationContext)
            ViewUtils.layoutView(scrollView, info[0], info[1])
            views.setBitmap(R.id.iv_schedule, "setImageBitmap", ViewUtils.getViewBitmap(scrollView))
            scrollView.removeAllViews()
        }

        private fun initWeekPanel(ui: ScheduleUI, data: List<CourseBean>?, day: Int) {
            val ll = ui.content.getViewById(R.id.anko_ll_week_panel_0 + ui.dayMap[day] - 1) as FrameLayout?
                    ?: return
            ll.removeAllViews()
            if (data == null || data.isEmpty()) return
            var isCovered = false
            var pre = data[0]
            for (i in data.indices) {
                val c = data[i]

                // 过期的不显示
                if (c.endWeek < week) {
                    continue
                }

                val isOtherWeek = (week % 2 == 0 && c.type == 1) || (week % 2 == 1 && c.type == 2)
                        || (c.startWeek > week)

                if (!table.showOtherWeekCourse && isOtherWeek) continue

                var isError = false

                val strBuilder = StringBuilder()
                if (c.step <= 0) {
                    c.step = 1
                    isError = true
                }
                if (c.startNode <= 0) {
                    c.startNode = 1
                    isError = true
                }
                if (c.startNode > table.nodes) {
                    c.startNode = table.nodes
                    isError = true
                }
                if (c.startNode + c.step - 1 > table.nodes) {
                    c.step = table.nodes - c.startNode + 1
                    isError = true
                }

                val textView = TipTextView(applicationContext)

                if (ll.childCount != 0) {
                    isCovered = (pre.startNode == c.startNode)
                }

                textView.setPadding(dip(4))

                if (c.color.isEmpty()) {
                    c.color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(applicationContext, c.id % 9))}"
                }

                strBuilder.append(c.courseName)

                if (c.room != "") {
                    strBuilder.append("\n@${c.room}")
                }

                if (isOtherWeek) {
                    when (c.type) {
                        1 -> strBuilder.append("\n单周")
                        2 -> strBuilder.append("\n双周")
                    }
                    strBuilder.append("[非本周]")
                    textView.visibility = View.VISIBLE
                } else {
                    when (c.type) {
                        1 -> strBuilder.append("\n单周")
                        2 -> strBuilder.append("\n双周")
                    }
                }

                if (isCovered) {
                    val tv = ll.getChildAt(ll.childCount - 1) as TipTextView?
                    if (tv != null) {
                        if (tv.tipVisibility == TipTextView.TIP_OTHER_WEEK) {
                            tv.visibility = View.INVISIBLE
                        }
                    }
                }

                val tv = ll.findViewWithTag<TipTextView?>(c.startNode)
                if (tv != null) {
                    textView.visibility = View.INVISIBLE
                    if (tv.tipVisibility != TipTextView.TIP_VISIBLE && !isOtherWeek) {
                        if (tv.tipVisibility != TipTextView.TIP_ERROR) {
                            tv.tipVisibility = TipTextView.TIP_VISIBLE
                        }
                    }
                }

                if (isError) {
                    textView.tipVisibility = TipTextView.TIP_ERROR
                }

                if (!isOtherWeek) {
                    textView.tag = c.startNode
                } else {
                    textView.tipVisibility = TipTextView.TIP_OTHER_WEEK
                }

                if (table.showTime && timeList.isNotEmpty()) {
                    strBuilder.insert(0, timeList[c.startNode - 1].startTime + "\n")
                }

                textView.init(
                        text = strBuilder.toString(),
                        txtSize = table.widgetItemTextSize,
                        txtColor = table.widgetCourseTextColor,
                        bgColor = Color.parseColor(c.color),
                        bgAlpha = alphaInt,
                        stroke = table.widgetStrokeColor
                )

                ll.addView(textView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        widgetItemHeight * c.step + marTop * (c.step - 1)).apply {
                    gravity = Gravity.TOP
                    topMargin = (c.startNode - 1) * (widgetItemHeight + marTop) + marTop
                })

                pre = c
            }
        }

    }

}