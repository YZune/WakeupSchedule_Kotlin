package com.suda.yzune.wakeupschedule.today_appwidget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import splitties.dimensions.dip
import splitties.views.bottomPadding
import splitties.views.dsl.core.*
import splitties.views.horizontalPadding
import splitties.views.topPadding
import java.text.ParseException
import kotlin.math.roundToInt

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
        private val courseDao = dataBase.courseDao()
        private val timeDao = dataBase.timeDetailDao()

        private var week = 1
        private lateinit var table: TableBean
        private val timeList = arrayListOf<TimeDetailBean>()
        private val courseList = arrayListOf<CourseBean>()

        override fun onCreate() {
            table = tableDao.getDefaultTableSync()
        }

        override fun onDataSetChanged() {
            table = tableDao.getDefaultTableSync()
            try {
                week = CourseUtils.countWeek(table.startDate, table.sundayFirst, nextDay)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            courseList.clear()
            if (week % 2 == 0) {
                courseList.addAll(courseDao.getCourseByDayOfTableSync(CourseUtils.getWeekdayInt(nextDay), week, 2, table.id))
            } else {
                courseList.addAll(courseDao.getCourseByDayOfTableSync(CourseUtils.getWeekdayInt(nextDay), week, 1, table.id))
            }
            timeList.clear()
            timeList.addAll(timeDao.getTimeListSync(table.timeTable))
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
            return if (courseList.isNotEmpty()) {
                val mRemoteViews = RemoteViews(applicationContext.packageName, R.layout.item_schedule_widget)
                val view = initView(applicationContext, position)
                val contentView = view.findViewById<LinearLayout>(R.id.anko_layout)
                val info = ViewUtils.getScreenInfo(applicationContext)
                ViewUtils.layoutView(contentView, info[0], info[1])
                val bitmap = ViewUtils.getViewBitmap(contentView, true, dip(2))
                mRemoteViews.setBitmap(R.id.iv_schedule, "setImageBitmap", bitmap)
                contentView.removeAllViews()
                mRemoteViews
            } else {
                val mRemoteViews = RemoteViews(applicationContext.packageName, R.layout.item_today_course_empty)
                mRemoteViews.setTextColor(R.id.tv_empty, table.widgetTextColor)
                mRemoteViews
            }
        }

        private fun initView(context: Context, position: Int): View {
            val iconFont = ResourcesCompat.getFont(context, R.font.iconfont)
            val dp = 2
            val alphaInt = (255 * (table.widgetItemAlpha.toFloat() / 100)).roundToInt()
            var alphaStr = if (alphaInt != 0) {
                Integer.toHexString(alphaInt)
            } else {
                "00"
            }
            if (alphaStr.length < 2) {
                alphaStr = "0$alphaStr"
            }
            return verticalLayout(R.id.anko_layout) {
                val c = courseList[position]

                add(horizontalLayout {
                    topPadding = dip(dp * 4)
                    bottomPadding = dip(dp * 4)
                    horizontalPadding = dip(dp * 4)
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

                    add(verticalLayout {
                        gravity = Gravity.CENTER
                        // 开始节
                        add(textView {
                            text = c.startNode.toString()
                            alpha = 0.8f
                            setTextColor(table.widgetCourseTextColor)
                            textSize = 12f
                            typeface = Typeface.DEFAULT_BOLD
                        }, lParams(wrapContent, wrapContent) {
                            bottomMargin = dip(dp * 2)
                        })
                        // 结束节
                        add(textView {
                            text = "${c.startNode + c.step - 1}"
                            alpha = 0.8f
                            setTextColor(table.widgetCourseTextColor)
                            textSize = 12f
                            typeface = Typeface.DEFAULT_BOLD
                        }, lParams(wrapContent, wrapContent) {
                            topMargin = dip(dp * 2)
                        })

                    }, lParams(dip(dp * 10), matchParent))

                    add(verticalLayout {
                        gravity = Gravity.CENTER

                        add(textView {
                            alpha = 0.8f
                            text = timeList[c.startNode - 1].startTime
                            setTextColor(table.widgetCourseTextColor)
                            textSize = 12f
                        }, lParams(wrapContent, wrapContent) {
                            margin = dip(dp * 2)
                        })

                        add(textView {
                            text = timeList[c.startNode + c.step - 2].endTime
                            alpha = 0.8f
                            setTextColor(table.widgetCourseTextColor)
                            textSize = 12f
                        }, lParams(wrapContent, wrapContent) {
                            margin = dip(dp * 2)
                        })

                    }, lParams(wrapContent, matchParent))

                    add(verticalLayout {
                        gravity = Gravity.CENTER_VERTICAL

                        add(textView {
                            text = c.courseName
                            setTextColor(table.widgetCourseTextColor)
                            textSize = 14f
                            typeface = Typeface.DEFAULT_BOLD
                        }, lParams(matchParent, wrapContent))

                        if (c.room != "" || c.teacher != "") {
                            add(horizontalLayout {
                                if (c.room != "") {

                                    add(textView {
                                        text = "\uE6B2"
                                        alpha = 0.8f
                                        setTextColor(table.widgetCourseTextColor)
                                        textSize = 12f
                                        typeface = iconFont
                                    }, lParams(wrapContent, wrapContent))

                                    add(textView {
                                        text = c.room
                                        alpha = 0.8f
                                        setTextColor(table.widgetCourseTextColor)
                                        maxLines = 1
                                        textSize = 12f
                                    }, lParams(wrapContent, wrapContent) {
                                        marginStart = dip(dp * 2)
                                        marginEnd = dip(dp * 8)
                                    })
                                }
                                if (c.teacher != "") {
                                    add(textView {
                                        text = "\uE6EB"
                                        alpha = 0.8f
                                        setTextColor(table.widgetCourseTextColor)
                                        textSize = 12f
                                        typeface = iconFont
                                    }, lParams(wrapContent, wrapContent))

                                    add(textView {
                                        alpha = 0.8f
                                        text = c.teacher
                                        setTextColor(table.widgetCourseTextColor)
                                        maxLines = 1
                                        ellipsize = TextUtils.TruncateAt.END
                                        textSize = 12f
                                    }, lParams(wrapContent, wrapContent) {
                                        marginStart = dip(dp * 2)
                                    })
                                }
                            }, lParams(matchParent, wrapContent) {
                                topMargin = dip(dp * 4)
                            })
                        }

                    }, lParams(wrapContent, matchParent) {
                        marginStart = dip(dp)
                    })

                }, lParams(matchParent, wrapContent))

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