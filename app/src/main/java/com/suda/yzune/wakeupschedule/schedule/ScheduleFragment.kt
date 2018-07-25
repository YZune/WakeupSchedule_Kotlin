package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.util.Xml
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import es.dmoral.toasty.Toasty
import java.util.ArrayList
import kotlin.concurrent.thread

class ScheduleFragment : Fragment() {

    var week = 0
    var itemHeight = 0
    var marTop = 0
    var weekPanels = arrayOfNulls<LinearLayout>(7)
    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        itemHeight = SizeUtils.dp2px(context, 56f)
        marTop = resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
        refresh(view)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: Int) =
                ScheduleFragment().apply {
                    week = arg
                }
    }

    private fun refresh(view: View) {
        for (i in weekPanels.indices) {
            weekPanels[i] = view.findViewById<View>(R.id.weekPanel_1 + i) as LinearLayout?
            weekPanels[i]?.removeAllViews()
            Log.d("空", "${view.findViewById<View>(R.id.weekPanel_1 + i) == null}")
        }

        for (i in 1..7) {
            val a = viewModel.getRawCourseByDay(i)
            a.observe(this, Observer {
                if (it != null) {
                    viewModel.getCourseByDay(it).observe(this, Observer {
                        initWeekPanel(weekPanels, it)
                    })
                }
            })
        }
    }

    private fun initWeekPanel(lls: Array<LinearLayout?>, data: List<CourseBean>?) {
        if (data == null || data.isEmpty()) return
        val ll = lls[data[0].day - 1] ?: return
        var pre = data[0]
        for (i in data.indices) {
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
