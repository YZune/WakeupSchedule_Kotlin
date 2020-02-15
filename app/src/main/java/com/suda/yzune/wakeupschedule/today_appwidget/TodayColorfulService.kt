package com.suda.yzune.wakeupschedule.today_appwidget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.Const
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.suda.yzune.wakeupschedule.utils.getPrefer
import splitties.dimensions.dip
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
        private var showColor = false

        override fun onCreate() {

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
            showColor = getPrefer().getBoolean(Const.KEY_DAY_WIDGET_COLOR, false)
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
            val mRemoteViews = RemoteViews(applicationContext.packageName, R.layout.item_schedule_widget)
            if (!this::table.isInitialized) {
                table = tableDao.getDefaultTableSync()
            }
            if (courseList.isNotEmpty()) {
                val view = initView(applicationContext, position)
                val contentView = view.findViewById<LinearLayout>(R.id.anko_layout)
                val info = ViewUtils.getScreenInfo(applicationContext)
                ViewUtils.layoutView(contentView, info[0], info[1])
                val bitmap = ViewUtils.getViewBitmap(contentView, true, dip(2))
                mRemoteViews.setImageViewBitmap(R.id.iv_schedule, bitmap)
                contentView.removeAllViews()
            } else {
                val img = ImageView(applicationContext).apply {
                    setImageResource(R.drawable.ic_schedule_empty)
                }
                val view = LinearLayout(applicationContext).apply {
                    id = R.id.anko_empty_view
                    orientation = LinearLayout.VERTICAL
                    if (context.getPrefer().getBoolean(Const.KEY_SHOW_EMPTY_VIEW, true)) {
                        addView(img, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dip(180)).apply {
                            topMargin = dip(16)
                        })
                    }
                    addView(TextView(context).apply {
                        text = if (nextDay) {
                            "明天没有课哦"
                        } else {
                            "今天没有课哦"
                        }
                        setTextColor(table.widgetTextColor)
                        gravity = Gravity.CENTER
                    }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        topMargin = dip(16)
                    })
                }
                val info = ViewUtils.getScreenInfo(applicationContext)
                ViewUtils.layoutView(view, info[0], info[1])
                mRemoteViews.setImageViewBitmap(R.id.iv_schedule, view.drawToBitmap(Bitmap.Config.ARGB_4444))
            }
            return mRemoteViews
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
            val widgetTextSize = table.widgetItemTextSize.toFloat()
            return LinearLayout(context).apply {
                id = R.id.anko_layout
                orientation = LinearLayout.VERTICAL
                val c = courseList[position]

                addView(LinearLayout(context).apply {
                    setPadding(dip(dp * 4))

                    if (showColor) {
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
                    }

                    addView(LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.CENTER
                        // 开始节
                        addView(TextView(context).apply {
                            text = c.startNode.toString()
                            alpha = 0.8f
                            setTextColor(table.widgetCourseTextColor)
                            textSize = widgetTextSize
                            typeface = Typeface.DEFAULT_BOLD
                        }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            bottomMargin = dip(dp * 2)
                        })
                        // 结束节
                        addView(TextView(context).apply {
                            text = "${c.startNode + c.step - 1}"
                            alpha = 0.8f
                            setTextColor(table.widgetCourseTextColor)
                            textSize = widgetTextSize
                            typeface = Typeface.DEFAULT_BOLD
                        }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            topMargin = dip(dp * 2)
                        })

                    }, LinearLayout.LayoutParams(dip(dp * 10), LinearLayout.LayoutParams.MATCH_PARENT))

                    addView(LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.CENTER

                        addView(TextView(context).apply {
                            alpha = 0.8f
                            text = timeList[c.startNode - 1].startTime
                            setTextColor(table.widgetCourseTextColor)
                            textSize = widgetTextSize
                        }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            setMargins(dip(dp * 2))
                        })

                        addView(TextView(context).apply {
                            text = timeList[c.startNode + c.step - 2].endTime
                            alpha = 0.8f
                            setTextColor(table.widgetCourseTextColor)
                            textSize = widgetTextSize
                        }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            setMargins(dip(dp * 2))
                        })

                    }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT))

                    addView(LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.CENTER_VERTICAL

                        addView(TextView(context).apply {
                            text = c.courseName
                            setTextColor(table.widgetCourseTextColor)
                            textSize = widgetTextSize + 2
                            typeface = Typeface.DEFAULT_BOLD
                        }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

                        if (c.room != "" || c.teacher != "") {
                            addView(LinearLayout(context).apply {
                                if (c.room != "") {

                                    addView(TextView(context).apply {
                                        text = "\uE6B2"
                                        alpha = 0.8f
                                        setTextColor(table.widgetCourseTextColor)
                                        textSize = widgetTextSize + 2
                                        typeface = iconFont
                                    }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))

                                    addView(TextView(context).apply {
                                        text = c.room
                                        alpha = 0.8f
                                        setTextColor(table.widgetCourseTextColor)
                                        maxLines = 1
                                        textSize = widgetTextSize + 2
                                    }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                                        marginStart = dip(dp * 2)
                                        marginEnd = dip(dp * 8)
                                    })
                                }
                                if (c.teacher != "") {
                                    addView(TextView(context).apply {
                                        text = "\uE6EB"
                                        alpha = 0.8f
                                        setTextColor(table.widgetCourseTextColor)
                                        textSize = widgetTextSize + 2
                                        typeface = iconFont
                                    }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))

                                    addView(TextView(context).apply {
                                        alpha = 0.8f
                                        text = c.teacher
                                        setTextColor(table.widgetCourseTextColor)
                                        maxLines = 1
                                        ellipsize = TextUtils.TruncateAt.END
                                        textSize = widgetTextSize + 2
                                    }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                                        marginStart = dip(dp * 2)
                                    })
                                }
                            }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                                topMargin = dip(dp * 4)
                            })
                        }

                    }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                        marginStart = dip(dp)
                    })

                }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

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