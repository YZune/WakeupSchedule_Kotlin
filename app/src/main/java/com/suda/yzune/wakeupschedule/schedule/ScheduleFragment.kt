package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintSet.CHAIN_PACKED
import android.support.constraint.ConstraintSet.PARENT_ID
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.find
import kotlin.coroutines.CoroutineContext

private const val weekParam = "week"

class ScheduleFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var week = 0
    private var weekPanels = arrayOfNulls<LinearLayout>(7)
    private lateinit var weekDate: List<String>
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            week = it.getInt(weekParam)
        }
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
        job = Job()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = UI {
            constraintLayout {
                for (i in 0..8) {
                    textView {
                        id = R.id.anko_tv_title0 + i
                        setPadding(0, dip(8), 0, dip(8))
                        textSize = 12f
                        gravity = Gravity.CENTER
                        setLineSpacing(dip(2).toFloat(), 1f)
                        if (i == 0) {
                            typeface = Typeface.DEFAULT_BOLD
                        }
                    }.lparams(0, wrapContent) {
                        when (i) {
                            0 -> {
                                horizontalWeight = 0.5f
                                startToStart = PARENT_ID
                                topToTop = PARENT_ID
                                endToStart = R.id.anko_tv_title0 + i + 1
                            }
                            8 -> {
                                horizontalWeight = 1f
                                startToEnd = R.id.anko_tv_title0 + i - 1
                                endToEnd = PARENT_ID
                                baselineToBaseline = R.id.anko_tv_title0 + i - 1
                            }
                            else -> {
                                horizontalWeight = 1f
                                startToEnd = R.id.anko_tv_title0 + i - 1
                                endToStart = R.id.anko_tv_title0 + i + 1
                                baselineToBaseline = R.id.anko_tv_title0 + i - 1
                            }
                        }
                    }
                }
                scrollView {
                    id = R.id.anko_sv_schedule
                    constraintLayout {
                        id = R.id.anko_cl_content_panel
                        for (i in 1..20) {
                            textView(i.toString()) {
                                id = R.id.anko_tv_node1 + i - 1
                                textSize = 12f
                                gravity = Gravity.CENTER
                            }.lparams(0, dip(56)) {
                                topMargin = dip(2)
                                when (i) {
                                    1 -> {
                                        bottomToTop = R.id.anko_tv_node1 + i
                                        endToStart = R.id.anko_ll_week_panel_0
                                        horizontalWeight = 0.5f
                                        startToStart = PARENT_ID
                                        topToTop = PARENT_ID
                                        verticalBias = 0f
                                        verticalChainStyle = CHAIN_PACKED
                                    }
                                    20 -> {
                                        bottomToTop = R.id.anko_navigation_bar_view
                                        endToStart = R.id.anko_ll_week_panel_0
                                        horizontalWeight = 0.5f
                                        startToStart = PARENT_ID
                                        topToBottom = R.id.anko_tv_node1 + i - 2
                                    }
                                    else -> {
                                        bottomToTop = R.id.anko_tv_node1 + i
                                        endToStart = R.id.anko_ll_week_panel_0
                                        horizontalWeight = 0.5f
                                        startToStart = PARENT_ID
                                        topToBottom = R.id.anko_tv_node1 + i - 2
                                    }
                                }
                            }
                        }
                        val barHeight = if (ViewUtils.getVirtualBarHeigh(context) != 0) {
                            ViewUtils.getVirtualBarHeigh(context)
                        } else {
                            dip(48)
                        }
                        val navBar = view {
                            id = R.id.anko_navigation_bar_view
                        }.lparams(matchParent, barHeight) {
                            topToBottom = R.id.anko_tv_node992
                            bottomToBottom = PARENT_ID
                            startToStart = PARENT_ID
                            endToEnd = PARENT_ID
                        }
                        if ((PreferenceUtils.getBooleanFromSP(context, "s_nav_bar_blur", false) || ViewUtils.checkDeviceHasNavigationBar(context)) && Build.VERSION.SDK_INT >= 21) {
                            navBar.visibility = View.VISIBLE
                        } else {
                            navBar.visibility = View.GONE
                        }
                        for (i in 0..7) {
                            verticalLayout {
                                id = R.id.anko_ll_week_panel_0 + i
                            }.lparams(0, wrapContent) {
                                marginStart = dip(1)
                                marginEnd = dip(1)
                                horizontalWeight = 1f
                                when (i) {
                                    0 -> {
                                        startToEnd = R.id.anko_tv_node1
                                        endToStart = R.id.anko_ll_week_panel_0 + i + 1
                                    }
                                    7 -> {
                                        startToEnd = R.id.anko_ll_week_panel_0 + i - 1
                                        endToEnd = PARENT_ID
                                    }
                                    else -> {
                                        startToEnd = R.id.anko_ll_week_panel_0 + i - 1
                                        endToStart = R.id.anko_ll_week_panel_0 + i + 1
                                    }
                                }
                            }
                        }
                    }
                }.lparams(matchParent, 0) {
                    bottomToBottom = PARENT_ID
                    topToBottom = R.id.anko_tv_title0
                }
            }
        }.view

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.table.showSun) {
            if (viewModel.table.sundayFirst) {
                find<View>(R.id.anko_tv_title7).visibility = View.GONE
                find<View>(R.id.anko_ll_week_panel_7).visibility = View.GONE
                find<View>(R.id.anko_tv_title0_1).visibility = View.VISIBLE
                find<View>(R.id.anko_ll_week_panel_0).visibility = View.VISIBLE
            } else {
                find<View>(R.id.anko_tv_title7).visibility = View.VISIBLE
                find<View>(R.id.anko_ll_week_panel_7).visibility = View.VISIBLE
                find<View>(R.id.anko_tv_title0_1).visibility = View.GONE
                find<View>(R.id.anko_ll_week_panel_0).visibility = View.GONE
            }
        } else {
            find<View>(R.id.anko_tv_title7).visibility = View.GONE
            find<View>(R.id.anko_ll_week_panel_7).visibility = View.GONE
            find<View>(R.id.anko_tv_title0_1).visibility = View.GONE
            find<View>(R.id.anko_ll_week_panel_0).visibility = View.GONE
        }

        weekDate = CourseUtils.getDateStringFromWeek(CourseUtils.countWeek(viewModel.table.startDate), week, viewModel.table.sundayFirst)
        find<TextView>(R.id.anko_tv_title0).setTextColor(viewModel.table.textColor)
        find<TextView>(R.id.anko_tv_title0).text = weekDate[0] + "\n月"
        var textView: TextView
        if (viewModel.table.sundayFirst) {
            for (i in 0..6) {
                textView = find(R.id.anko_tv_title0_1 + i)
                textView.setTextColor(viewModel.table.textColor)
                textView.text = viewModel.daysArray[i] + "\n${weekDate[i + 1]}"
            }
        } else {
            for (i in 0..6) {
                textView = find(R.id.anko_tv_title1 + i)
                textView.setTextColor(viewModel.table.textColor)
                textView.text = viewModel.daysArray[i + 1] + "\n${weekDate[i + 1]}"
            }
        }

        if (viewModel.table.showSat) {
            find<View>(R.id.anko_tv_title6).visibility = View.VISIBLE
            find<View>(R.id.anko_ll_week_panel_6).visibility = View.VISIBLE
        } else {
            find<View>(R.id.anko_tv_title6).visibility = View.GONE
            find<View>(R.id.anko_ll_week_panel_6).visibility = View.GONE
        }

        for (i in 0 until 20) {
            textView = find(R.id.anko_tv_node1 + i)
            val lp = textView.layoutParams
            lp.height = viewModel.itemHeight
            textView.layoutParams = lp
            textView.setTextColor(viewModel.table.textColor)
            if (i >= viewModel.table.nodes) {
                textView.visibility = View.GONE
            } else {
                textView.visibility = View.VISIBLE
            }
        }

        val alphaInt = Math.round(255 * (viewModel.table.itemAlpha.toFloat() / 100))
        viewModel.alphaStr = if (alphaInt != 0) {
            Integer.toHexString(alphaInt)
        } else {
            "00"
        }
        if (viewModel.alphaStr.length < 2) {
            viewModel.alphaStr = "0${viewModel.alphaStr}"
        }

        for (i in 1..7) {
            viewModel.allCourseList[i - 1].observe(this, Observer {
                initWeekPanel(weekPanels, it, i, viewModel.table)
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg0: Int) =
                ScheduleFragment().apply {
                    arguments = Bundle().apply {
                        putInt(weekParam, arg0)
                    }
                }
    }

    private fun initWeekPanel(lls: Array<LinearLayout?>, data: List<CourseBean>?, day: Int, table: TableBean) {
        val llIndex = day - 1
        lls[llIndex] = find(R.id.anko_ll_week_panel_1 + llIndex)
        lls[llIndex]?.removeAllViews()
        find<LinearLayout>(R.id.anko_ll_week_panel_0).removeAllViews()
        if (data == null || data.isEmpty()) return
        val ll = lls[data[0].day - 1] ?: return
        var pre = data[0]
        for (i in data.indices) {
            val strBuilder = StringBuilder()
            val c = data[i]

            if (!table.showOtherWeekCourse) {
                if (week % 2 == 0 && c.type == 1) {
                    continue
                }
                if (week % 2 == 1 && c.type == 2) {
                    continue
                }
                if (c.startWeek > week || c.endWeek < week) {
                    continue
                }
            }

            val textView = TextView(context)
            val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    viewModel.itemHeight * c.step + viewModel.marTop * (c.step - 1))
            if (ll.childCount == 0) {
                lp.setMargins(0, (c.startNode - 1) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
            } else {
                lp.setMargins(0, (c.startNode - (pre.startNode + pre.step)) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
            }
//            if (i > 0) {
//                lp.setMargins(0, (c.startNode - (pre.startNode + pre.step)) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
//            } else {
//                lp.setMargins(0, (c.startNode - 1) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
//            }
            textView.layoutParams = lp
//            tv.gravity = Gravity.CENTER_HORIZONTAL
            textView.textSize = table.itemTextSize.toFloat()
            textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            textView.setPadding(dip(4), dip(4), dip(4), dip(4))
            textView.setTextColor(table.courseTextColor)

            textView.background = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.course_item_bg)
            val myGrad = textView.background as GradientDrawable
            myGrad.setStroke(SizeUtils.dp2px(context!!.applicationContext, 2f), table.strokeColor)

            if (c.color == "") {
                c.color = "#${Integer.toHexString(getCustomizedColor(c.id % 9))}"
                launch(Dispatchers.IO) {
                    viewModel.updateCourseBaseBean(c)
                }
            }

            try {
                if (c.color.length == 7) {
                    myGrad.setColor(Color.parseColor("#${viewModel.alphaStr}${c.color.substring(1, 7)}"))
                } else {
                    myGrad.setColor(Color.parseColor("#${viewModel.alphaStr}${c.color.substring(3, 9)}"))
                }
            } catch (e: Exception) {
                myGrad.setColor(Color.parseColor(c.color))
            }

            strBuilder.append(c.courseName)

            if (c.room != "") {
                strBuilder.append("\n@${c.room}")
            }

            when (c.type) {
                1 -> {
                    if (week % 2 == 0) {
                        if (table.showOtherWeekCourse) {
                            strBuilder.append("\n单周[非本周]")
                            textView.visibility = View.VISIBLE
                            textView.alpha = 0.6f
                            myGrad.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
                        } else {
                            textView.visibility = View.INVISIBLE
                        }
                    } else {
                        strBuilder.append("\n单周")
                    }
                }
                2 -> {
                    if (week % 2 != 0) {
                        if (table.showOtherWeekCourse) {
                            textView.alpha = 0.6f
                            strBuilder.append("\n双周[非本周]")
                            textView.visibility = View.VISIBLE
                            myGrad.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
                        } else {
                            textView.visibility = View.INVISIBLE
                        }
                    } else {
                        strBuilder.append("\n双周")
                    }
                }
            }

            if (c.startWeek > week || c.endWeek < week) {
                if (table.showOtherWeekCourse) {
                    textView.alpha = 0.6f
                    strBuilder.append("[非本周]")
                    textView.visibility = View.VISIBLE
                    myGrad.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
                } else {
                    textView.visibility = View.INVISIBLE
                }
            }

            if (table.showTime) {
                strBuilder.insert(0, viewModel.timeList[c.startNode - 1].startTime + "\n")
                textView.text = strBuilder
            } else {
                textView.text = strBuilder
            }

            textView.setOnClickListener {
                try {
                    val detailFragment = CourseDetailFragment.newInstance(c)
                    detailFragment.show(fragmentManager, "courseDetail")
                } catch (e: Exception) {
                    //TODO: 提示是否要删除异常的数据
                    Toasty.error(activity!!.applicationContext, "哎呀>_<差点崩溃了").show()
                }
            }

            if (day == 7) {
                if (table.sundayFirst) {
                    find<LinearLayout>(R.id.anko_ll_week_panel_0).addView(textView)
                } else {
                    ll.addView(textView)
                }
            } else {
                ll.addView(textView)
            }
            pre = c
        }
    }

    private fun getCustomizedColor(index: Int): Int {
        val customizedColors = resources.getIntArray(R.array.customizedColors)
        return customizedColors[index]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}