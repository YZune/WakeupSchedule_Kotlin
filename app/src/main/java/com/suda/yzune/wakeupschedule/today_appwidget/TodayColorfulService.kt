package com.suda.yzune.wakeupschedule.today_appwidget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import org.jetbrains.anko.*
import java.text.ParseException

class TodayColorfulService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return if (intent != null) {
            val i = intent.data?.schemeSpecificPart?.toInt()
            return if (i == 1) {
                TodayColorfulRemoteViewsFactory(true)
            } else {
                TodayColorfulRemoteViewsFactory(false)
            }
        } else {
            TodayColorfulRemoteViewsFactory()
        }
    }

    private inner class TodayColorfulRemoteViewsFactory(val nextDay: Boolean = false) : RemoteViewsFactory {

        private val dataBase = AppDatabase.getDatabase(applicationContext)
        private val tableDao = dataBase.tableDao()
        private val baseDao = dataBase.courseBaseDao()
        private val timeDao = dataBase.timeDetailDao()

        private var week = 1
        private lateinit var table: TableBean
        private val timeList = arrayListOf<TimeDetailBean>()
        private val courseList = arrayListOf<CourseBean>()

        override fun onCreate() {
            table = tableDao.getDefaultTableInThread()
        }

        override fun onDataSetChanged() {
            table = tableDao.getDefaultTableInThread()
            try {
                week = CourseUtils.countWeek(table.startDate, table.sundayFirst, nextDay)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            courseList.clear()
            if (week % 2 == 0) {
                courseList.addAll(baseDao.getCourseByDayOfTableInThread(CourseUtils.getWeekdayInt(nextDay), week, 2, table.id))
            } else {
                courseList.addAll(baseDao.getCourseByDayOfTableInThread(CourseUtils.getWeekdayInt(nextDay), week, 1, table.id))
            }
            timeList.clear()
            timeList.addAll(timeDao.getTimeListInThread(table.timeTable))
        }

        override fun onDestroy() {
            timeList.clear()
            courseList.clear()
        }

        override fun getCount(): Int {
            return 1
        }

        override fun getViewAt(position: Int): RemoteViews {
            return if (courseList.isNotEmpty()) {
                val mRemoteViews = RemoteViews(applicationContext.packageName, R.layout.item_schedule_widget)
                val view = initView(applicationContext)
                val scrollView = view.find<ScrollView>(R.id.anko_sv_schedule)
                ViewUtils.layoutView(scrollView, dip(1000), dip(500))
                mRemoteViews.setBitmap(R.id.iv_schedule, "setImageBitmap", ViewUtils.getViewBitmap(scrollView))
                scrollView.removeAllViews()
                mRemoteViews
            } else {
                val mRemoteViews = RemoteViews(applicationContext.packageName, R.layout.item_today_course_empty)
                mRemoteViews.setTextColor(R.id.tv_empty, table.widgetTextColor)
                mRemoteViews
            }
        }

        private fun initView(context: Context): View {
            val iconFont = ResourcesCompat.getFont(context, R.font.iconfont)
            val dp = 5
            val alphaInt = Math.round(255 * (table.widgetItemAlpha.toFloat() / 100))
            var alphaStr = if (alphaInt != 0) {
                Integer.toHexString(alphaInt)
            } else {
                "00"
            }
            if (alphaStr.length < 2) {
                alphaStr = "0$alphaStr"
            }
            return context.UI {
                scrollView {
                    id = R.id.anko_sv_schedule
                    overScrollMode = View.OVER_SCROLL_NEVER
                    isVerticalScrollBarEnabled = false
                    verticalLayout {
                        for (c in courseList) {
                            linearLayout {
                                topPadding = dip(dp * 4)
                                bottomPadding = dip(dp * 4)
                                leftPadding = dip(dp * 4)
                                rightPadding = dip(dp * 4)
                                background = ContextCompat.getDrawable(context.applicationContext, R.drawable.course_item_bg_today)
                                val myGrad = background as GradientDrawable
//                                myGrad.cornerRadius = dip(dp * 4).toFloat()
                                myGrad.setStroke(dip(dp), table.widgetStrokeColor)
                                when {
                                    c.color.length == 7 -> myGrad.setColor(Color.parseColor("#$alphaStr${c.color.substring(1, 7)}"))
                                    c.color.isEmpty() -> {
                                        myGrad.setColor(Color.parseColor("#${alphaStr}fa6278"))
                                    }
                                    else -> myGrad.setColor(Color.parseColor("#$alphaStr${c.color.substring(3, 9)}"))
                                }

                                verticalLayout {
                                    gravity = Gravity.CENTER
                                    // 开始节
                                    textView(c.startNode.toString()) {
                                        alpha = 0.8f
                                        textColor = table.widgetCourseTextColor
                                        textSize = sp(12).toFloat()
                                        typeface = Typeface.DEFAULT_BOLD
                                    }.lparams(wrapContent, wrapContent) {
                                        bottomMargin = dip(dp * 2)
                                    }
                                    // 结束节
                                    textView("${c.startNode + c.step - 1}") {
                                        alpha = 0.8f
                                        textColor = table.widgetCourseTextColor
                                        textSize = sp(12).toFloat()
                                        typeface = Typeface.DEFAULT_BOLD
                                    }.lparams(wrapContent, wrapContent) {
                                        topMargin = dip(dp * 2)
                                    }

                                }.lparams(dip(dp * 10), matchParent)

                                verticalLayout {
                                    gravity = Gravity.CENTER

                                    textView(timeList[c.startNode - 1].startTime) {
                                        alpha = 0.8f
                                        textColor = table.widgetCourseTextColor
                                        textSize = sp(12).toFloat()
                                    }.lparams(wrapContent, wrapContent) {
                                        margin = dip(dp * 2)
                                    }

                                    textView(timeList[c.startNode + c.step - 2].endTime) {
                                        alpha = 0.8f
                                        textColor = table.widgetCourseTextColor
                                        textSize = sp(12).toFloat()
                                    }.lparams(wrapContent, wrapContent) {
                                        margin = dip(dp * 2)
                                    }

                                }.lparams(wrapContent, matchParent)

                                verticalLayout {
                                    gravity = Gravity.CENTER_VERTICAL

                                    textView(c.courseName) {
                                        textColor = table.widgetCourseTextColor
                                        textSize = sp(14).toFloat()
                                        typeface = Typeface.DEFAULT_BOLD
                                    }.lparams(matchParent, wrapContent)

                                    if (c.room != "" || c.teacher != "") {
                                        linearLayout {
                                            if (c.room != "") {
                                                textView("\uE6B2") {
                                                    alpha = 0.8f
                                                    textColor = table.widgetCourseTextColor
                                                    textSize = sp(12).toFloat()
                                                    typeface = iconFont
                                                }.lparams(wrapContent, wrapContent)

                                                textView(c.room) {
                                                    alpha = 0.8f
                                                    textColor = table.widgetCourseTextColor
                                                    maxLines = 1
                                                    textSize = sp(12).toFloat()
                                                }.lparams(wrapContent, wrapContent) {
                                                    marginStart = dip(dp * 2)
                                                    marginEnd = dip(dp * 8)
                                                }
                                            }
                                            if (c.teacher != "") {
                                                textView("\uE6EB") {
                                                    alpha = 0.8f
                                                    textColor = table.widgetCourseTextColor
                                                    textSize = sp(12).toFloat()
                                                    typeface = iconFont
                                                }.lparams(wrapContent, wrapContent)

                                                textView(c.teacher) {
                                                    alpha = 0.8f
                                                    textColor = table.widgetCourseTextColor
                                                    maxLines = 1
                                                    ellipsize = TextUtils.TruncateAt.END
                                                    textSize = sp(12).toFloat()
                                                }.lparams(wrapContent, wrapContent) {
                                                    marginStart = dip(dp * 2)
                                                }
                                            }
                                        }.lparams(matchParent, wrapContent) {
                                            topMargin = dip(dp * 4)
                                        }
                                    }

                                }.lparams(wrapContent, matchParent) {
                                    marginStart = dip(dp)
                                }

                            }.lparams(matchParent, wrapContent) {
                                bottomMargin = dip(dp * 2)
                            }
                        }
                    }
                }
            }.view
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