package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import kotlinx.android.synthetic.main.fragment_schedule.*

class ScheduleFragment : Fragment() {

    private var week = 0
    private var itemHeight = 0
    private var marTop = 0
    private var showWhite = true
    private var showSunday = true
    private var showTimeDetail = false
    private var showSummerTime = false
    private var showStroke = true
    private var showNone = true
    private var weekPanels = arrayOfNulls<LinearLayout>(7)
    private var nodesNum = 11
    private var textSize = 12
    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onResume() {
        super.onResume()
        val itemHeightDp = PreferenceUtils.getIntFromSP(context!!.applicationContext, "item_height", 56)
        itemHeight = SizeUtils.dp2px(context, itemHeightDp.toFloat())
        marTop = resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
        textSize = PreferenceUtils.getIntFromSP(context!!.applicationContext, "sb_text_size", 12)
        showSummerTime = PreferenceUtils.getBooleanFromSP(context!!.applicationContext, "s_summer", false)
        showNone = PreferenceUtils.getBooleanFromSP(context!!.applicationContext, "s_show", false)
        showStroke = PreferenceUtils.getBooleanFromSP(context!!.applicationContext, "s_stroke", true)
        showWhite = PreferenceUtils.getBooleanFromSP(context!!.applicationContext, "s_color", true)
        showTimeDetail = PreferenceUtils.getBooleanFromSP(context!!.applicationContext, "s_show_time_detail", false)
        showSunday = PreferenceUtils.getBooleanFromSP(context!!.applicationContext, "s_show_weekend", true)
        val daysEnd = if (showSunday) 7 else 6
        if (showSunday) {
            weekPanel_7.visibility = View.VISIBLE
            title7.visibility = View.VISIBLE
        } else {
            weekPanel_7.visibility = View.GONE
            title7.visibility = View.GONE
        }

        for (i in 0 until weekPanel_0.childCount) {
            val lp = weekPanel_0.getChildAt(i).layoutParams
            lp.height = itemHeight
            weekPanel_0.getChildAt(i).layoutParams = lp
        }
        if (showWhite) {
            for (i in 0 until weekPanel_0.childCount) {
                val tv = weekPanel_0.getChildAt(i) as TextView
                tv.setTextColor(resources.getColor(R.color.white))
            }
            for (i in 0 until weekName.childCount) {
                val tv = weekName.getChildAt(i) as TextView
                tv.setTextColor(resources.getColor(R.color.white))
            }
        } else {
            for (i in 0 until weekPanel_0.childCount) {
                val tv = weekPanel_0.getChildAt(i) as TextView
                tv.setTextColor(resources.getColor(R.color.black))
            }
            for (i in 0 until weekName.childCount) {
                val tv = weekName.getChildAt(i) as TextView
                tv.setTextColor(resources.getColor(R.color.black))
            }
        }

        nodesNum = PreferenceUtils.getIntFromSP(context!!.applicationContext, "classNum", 11)
        for (i in 3 until nodesNum) {
            val tv = weekPanel_0.getChildAt(i) as TextView
            tv.visibility = View.VISIBLE
        }
        for (i in nodesNum until weekPanel_0.childCount) {
            val tv = weekPanel_0.getChildAt(i) as TextView
            tv.visibility = View.GONE
        }

        refresh(view!!, daysEnd)

        ll_contentPanel.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    refresh(view!!, daysEnd)
                }
            }
            return@setOnTouchListener false
        }

        ll_contentPanel.setOnLongClickListener {
            refresh(view!!, daysEnd, "lover")
            return@setOnLongClickListener true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: Int) =
                ScheduleFragment().apply {
                    week = arg
                }
    }

    private fun refresh(view: View, daysEnd: Int, tableName: String = "") {
        for (i in 1..daysEnd) {
            viewModel.getRawCourseByDay(i, tableName).observe(this, Observer { list ->
                initWeekPanel(view, weekPanels, list, i)
            })
        }
        if (PreferenceUtils.getBooleanFromSP(activity!!.applicationContext, "s_sunday_first", false)) {
            val title7 = view.findViewById<TextView>(R.id.title7)
            val weekPanel7 = view.findViewById<LinearLayout>(R.id.weekPanel_7)
            weekName.removeView(title7)
            weekName.addView(title7, 1)
            ll_contentPanel.removeView(weekPanel7)
            ll_contentPanel.addView(weekPanel7, 1)
        }
    }

    private fun initWeekPanel(view: View, lls: Array<LinearLayout?>, data: List<CourseBean>?, day: Int) {
        val llIndex = day - 1
        lls[llIndex] = view.findViewById<View>(R.id.weekPanel_1 + llIndex) as LinearLayout?
        lls[llIndex]?.removeAllViews()
        if (data == null || data.isEmpty()) return
        val ll = lls[data[0].day - 1] ?: return
        var pre = data[0]
        for (i in data.indices) {
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
            tv.setTextColor(resources.getColor(R.color.white))

            tv.background = resources.getDrawable(R.drawable.course_item_bg)
            val myGrad = tv.background as GradientDrawable
            if (!showStroke) {
                myGrad.setStroke(SizeUtils.dp2px(context!!.applicationContext, 2f), resources.getColor(R.color.transparent))
            } else {
                myGrad.setStroke(SizeUtils.dp2px(context!!.applicationContext, 2f), Color.parseColor("#80ffffff"))
            }

            if (c.color == "") {
                myGrad.setColor(getCustomizedColor(c.id % 9))
                c.color = "#${Integer.toHexString(getCustomizedColor(c.id % 9))}"
                viewModel.updateCourseBaseBean(c)
            } else {
                myGrad.setColor(Color.parseColor(c.color))
            }
            myGrad.alpha = Math.round(255 * (PreferenceUtils.getIntFromSP(context!!.applicationContext, "sb_alpha", 60) / 100.0)).toInt()

            if (c.room != "") {
                tv.text = c.courseName + "@" + c.room
            } else {
                tv.text = c.courseName
            }

            when (c.type) {
                1 -> {
                    tv.text = tv.text.toString() + "\n单周"
                    if (week % 2 == 0) {
                        if (showNone) {
                            tv.text = tv.text.toString() + "\n单周[非本周]"
                            tv.visibility = View.VISIBLE
                            tv.alpha = 0.6f
                            myGrad.setColor(resources.getColor(R.color.grey))
                        } else {
                            tv.visibility = View.INVISIBLE
                        }
                    }
                }
                2 -> {
                    tv.text = tv.text.toString() + "\n双周"
                    if (week % 2 != 0) {
                        if (showNone) {
                            tv.alpha = 0.6f
                            tv.text = tv.text.toString() + "\n双周[非本周]"
                            tv.visibility = View.VISIBLE
                            myGrad.setColor(resources.getColor(R.color.grey))
                        } else {
                            tv.visibility = View.INVISIBLE
                        }
                    }
                }
            }

            if (c.startWeek > week || c.endWeek < week) {
                if (showNone) {
                    tv.alpha = 0.6f
                    //tv.text = c.courseName + "@" + c.room + "[非本周]"
                    tv.text = tv.text.toString() + "[非本周]"
                    tv.visibility = View.VISIBLE
                    myGrad.setColor(resources.getColor(R.color.grey))
                } else {
                    tv.visibility = View.INVISIBLE
                }
            }

            if (showTimeDetail) {
                if (showSummerTime) {
                    tv.text = viewModel.getSummerTimeList()[c.startNode - 1].startTime + "\n" + tv.text
                } else {
                    tv.text = viewModel.getTimeList()[c.startNode - 1].startTime + "\n" + tv.text
                }
            }

            tv.setOnClickListener {
                //ViewUtils.saveImg(ViewUtils.getViewBitmap(scrollPanel), activity)
                val detailFragment = CourseDetailFragment.newInstance(c)
                detailFragment.show(fragmentManager, "courseDetail")
            }

            ll.addView(tv)
            pre = c
        }
    }

    private fun getCustomizedColor(index: Int): Int {
        val customizedColors = resources.getIntArray(R.array.customizedColors)
        return customizedColors[index]
    }

}
