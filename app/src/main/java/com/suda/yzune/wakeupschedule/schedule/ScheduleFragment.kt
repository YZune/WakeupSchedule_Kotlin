package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import kotlinx.android.synthetic.main.fragment_schedule.*

class ScheduleFragment : Fragment() {

    private var week = 0
    private var weekPanels = arrayOfNulls<LinearLayout>(7)
    private lateinit var weekDate: List<String>
    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        viewModel.tableData.observe(this, Observer { table ->
            if (table == null) return@Observer
            if (table.showSun) {
                if (table.sundayFirst) {
                    tv_title7.visibility = View.GONE
                    weekPanel_7.visibility = View.GONE
                    tv_title0_1.visibility = View.VISIBLE
                    weekPanel_0.visibility = View.VISIBLE
                } else {
                    tv_title7.visibility = View.VISIBLE
                    weekPanel_7.visibility = View.VISIBLE
                    tv_title0_1.visibility = View.GONE
                    weekPanel_0.visibility = View.GONE
                }
            } else {
                tv_title7.visibility = View.GONE
                weekPanel_7.visibility = View.GONE
                tv_title0_1.visibility = View.GONE
                weekPanel_0.visibility = View.GONE
            }

            weekDate = CourseUtils.getDateStringFromWeek(CourseUtils.countWeek(table.startDate), week, table.sundayFirst)
            tv_title0.text = weekDate[0] + "\n月"
            var tvTitle: TextView
            if (table.sundayFirst) {
                for (i in 0..6) {
                    tvTitle = view.findViewById(R.id.tv_title0_1 + i)
                    tvTitle.text = viewModel.daysArray[i] + "\n${weekDate[i + 1]}"
                }
            } else {
                for (i in 0..6) {
                    tvTitle = view.findViewById(R.id.tv_title1 + i)
                    tvTitle.text = viewModel.daysArray[i + 1] + "\n${weekDate[i + 1]}"
                }
            }

            if (table.showSat) {
                weekPanel_6.visibility = View.VISIBLE
                tv_title6.visibility = View.VISIBLE
            } else {
                weekPanel_6.visibility = View.GONE
                tv_title6.visibility = View.GONE
            }

            for (i in 0 until 16) {
                val tv = view.findViewById<TextView>(R.id.tv_node1 + i)
                val lp = tv.layoutParams
                lp.height = viewModel.itemHeight
                tv.layoutParams = lp
                tv.setTextColor(Color.parseColor(table.textColor))
                if (i >= table.nodes) {
                    tv.visibility = View.GONE
                } else {
                    tv.visibility = View.VISIBLE
                }
            }

            for (i in 0 until 9) {
                val tv = view.findViewById<TextView>(R.id.tv_title0 + i)
                tv.setTextColor(Color.parseColor(table.textColor))
            }

            for (i in 1..7) {
                viewModel.allCourseList[i - 1].observe(this, Observer {
                    initWeekPanel(view, weekPanels, it, i, table)
                })
            }
        })
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(arg0: Int, arg1: TableBean) =
                ScheduleFragment().apply {
                    week = arg0
                }
    }

    private fun initWeekPanel(view: View, lls: Array<LinearLayout?>, data: List<CourseBean>?, day: Int, table: TableBean) {
        val llIndex = day - 1
        lls[llIndex] = view.findViewById<View>(R.id.weekPanel_1 + llIndex) as LinearLayout?
        lls[llIndex]?.removeAllViews()
        weekPanel_0.removeAllViews()
        if (data == null || data.isEmpty()) return
        val ll = lls[data[0].day - 1] ?: return
        var pre = data[0]
        for (i in data.indices) {
            val strBuilder = StringBuilder()
            val c = data[i]
            val tv = TextView(context)
            val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    viewModel.itemHeight * c.step + viewModel.marTop * (c.step - 1))
            if (i > 0) {
                lp.setMargins(0, (c.startNode - (pre.startNode + pre.step)) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
            } else {
                lp.setMargins(0, (c.startNode - 1) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
            }
            tv.layoutParams = lp
            //tv.gravity = Gravity.CENTER_VERTICAL
            tv.textSize = table.itemTextSize.toFloat()
            tv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            tv.setPadding(8, 8, 8, 8)
            tv.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.white))

            tv.background = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.course_item_bg)
            val myGrad = tv.background as GradientDrawable
            myGrad.setStroke(SizeUtils.dp2px(context!!.applicationContext, 2f), Color.parseColor(table.strokeColor))

            if (c.color == "") {
                c.color = "#${Integer.toHexString(getCustomizedColor(c.id % 9))}"
                viewModel.updateCourseBaseBean(c)
            }

            val alphaInt = Math.round(255 * (table.itemAlpha.toFloat() / 100))
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
                        if (table.showOtherWeekCourse) {
                            strBuilder.append("\n单周[非本周]")
                            tv.visibility = View.VISIBLE
                            tv.alpha = 0.6f
                            myGrad.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
                        } else {
                            tv.visibility = View.INVISIBLE
                        }
                    }
                }
                2 -> {
                    strBuilder.append("\n双周")
                    if (week % 2 != 0) {
                        if (table.showOtherWeekCourse) {
                            tv.alpha = 0.6f
                            strBuilder.append("\n双周[非本周]")
                            tv.visibility = View.VISIBLE
                            myGrad.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
                        } else {
                            tv.visibility = View.INVISIBLE
                        }
                    }
                }
            }

            if (c.startWeek > week || c.endWeek < week) {
                if (table.showOtherWeekCourse) {
                    tv.alpha = 0.6f
                    strBuilder.append("[非本周]")
                    tv.visibility = View.VISIBLE
                    myGrad.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
                } else {
                    tv.visibility = View.INVISIBLE
                }
            }

            //todo: 显示具体时间

            tv.setOnClickListener {
                //ViewUtils.saveImg(ViewUtils.getViewBitmap(scrollPanel), activity)
                val detailFragment = CourseDetailFragment.newInstance(c)
                detailFragment.show(fragmentManager, "courseDetail")
            }

            tv.text = strBuilder
            if (day == 7) {
                if (table.sundayFirst) {
                    weekPanel_0.addView(tv)
                } else {
                    ll.addView(tv)
                }
            } else {
                ll.addView(tv)
            }
            pre = c
        }
    }

    private fun getCustomizedColor(index: Int): Int {
        val customizedColors = resources.getIntArray(R.array.customizedColors)
        return customizedColors[index]
    }

}