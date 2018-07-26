package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.utils.SizeUtils

class ScheduleFragment : Fragment() {

    var week = 0
    var itemHeight = 0
    var marTop = 0
    var weekPanels = arrayOfNulls<LinearLayout>(7)
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
        itemHeight = SizeUtils.dp2px(context, 56f)
        marTop = resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
        refresh(view!!)
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: Int) =
                ScheduleFragment().apply {
                    week = arg
                }
    }

    private fun refresh(view: View) {

        for (i in 1..7) {
            viewModel.getRawCourseByDay(i).observe(this, Observer {
                if (it != null) {
                    viewModel.getCourseByDay(it).observe(this, Observer {
                        initWeekPanel(view, weekPanels, it)
                    })
                }
            })
        }
    }

    private fun initWeekPanel(view: View, lls: Array<LinearLayout?>, data: List<CourseBean>?) {
        if (data == null || data.isEmpty()) return
        val llIndex = data[0].day - 1
        lls[llIndex] = view.findViewById<View>(R.id.weekPanel_1 + llIndex) as LinearLayout?
        lls[llIndex]?.removeAllViews()
        val ll = lls[data[0].day - 1] ?: return
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
            myGrad.setColor(getCustomizedColor(c.id % 9))
            myGrad.alpha = Math.round(255 * (60.0 / 100)).toInt()

            when (c.type) {
                0 -> tv.text = c.courseName + "@" + c.room
                1 -> {
                    tv.text = c.courseName + "@" + c.room + "\n单周"
                    if (week % 2 == 0) {
                        tv.visibility = View.INVISIBLE
                    }
                }
                2 -> {
                    tv.text = c.courseName + "@" + c.room + "\n双周"
                    if (week % 2 != 0) {
                        tv.visibility = View.INVISIBLE
                    }
                }
            }

            if (c.startWeek > week || c.endWeek < week) {
                tv.visibility = View.INVISIBLE
            }

            ll.addView(tv)
            pre = c
        }
    }

    private fun getCustomizedColor(index: Int): Int {
        val customizedColors = resources.getIntArray(R.array.customizedColors)
        val customizedColor = customizedColors[index]
        return customizedColor
    }

}
