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

    var week = 0
    private var weekPanels = arrayOfNulls<LinearLayout>(7)
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

        if (viewModel.showSunday) {
            weekPanel_7.visibility = View.VISIBLE
            title7.visibility = View.VISIBLE
        } else {
            weekPanel_7.visibility = View.GONE
            title7.visibility = View.GONE
        }

        if (viewModel.showSat) {
            weekPanel_6.visibility = View.VISIBLE
            title6.visibility = View.VISIBLE
        } else {
            weekPanel_6.visibility = View.GONE
            title6.visibility = View.GONE
        }

        for (i in 0 until weekPanel_0.childCount) {
            val lp = weekPanel_0.getChildAt(i).layoutParams
            lp.height = viewModel.itemHeight
            weekPanel_0.getChildAt(i).layoutParams = lp
        }
        if (viewModel.showWhite) {
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

        for (i in 3 until viewModel.nodesNum) {
            val tv = weekPanel_0.getChildAt(i) as TextView
            tv.visibility = View.VISIBLE
        }
        for (i in viewModel.nodesNum until weekPanel_0.childCount) {
            val tv = weekPanel_0.getChildAt(i) as TextView
            tv.visibility = View.GONE
        }

        refresh(view!!)

        ll_contentPanel.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    refresh(view!!)
                }
            }
            return@setOnTouchListener false
        }

        ll_contentPanel.setOnLongClickListener {
            refresh(view!!, "lover")
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

    private fun refresh(view: View, tableName: String = "") {
        if (tableName == "lover") {
            for (i in 1..7) {
                viewModel.loverCourseList[i - 1].observe(this, Observer {
                    initWeekPanel(view, weekPanels, it, i)
                })
            }
        } else {
            for (i in 1..7) {
                viewModel.allCourseList[i - 1].observe(this, Observer {
                    initWeekPanel(view, weekPanels, it, i)
                })
            }
        }

        if (viewModel.sundayFirst) {
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
                    viewModel.itemHeight * c.step + viewModel.marTop * (c.step - 1))
            if (i > 0) {
                lp.setMargins(0, (c.startNode - (pre.startNode + pre.step)) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
            } else {
                lp.setMargins(0, (c.startNode - 1) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
            }
            tv.layoutParams = lp
            //tv.gravity = Gravity.CENTER_VERTICAL
            tv.textSize = viewModel.textSize.toFloat()
            tv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            tv.setPadding(8, 8, 8, 8)
            tv.setTextColor(resources.getColor(R.color.white))

            tv.background = resources.getDrawable(R.drawable.course_item_bg)
            val myGrad = tv.background as GradientDrawable
            if (!viewModel.showStroke) {
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
                        if (viewModel.showNone) {
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
                        if (viewModel.showNone) {
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
                if (viewModel.showNone) {
                    tv.alpha = 0.6f
                    //tv.text = c.courseName + "@" + c.room + "[非本周]"
                    tv.text = tv.text.toString() + "[非本周]"
                    tv.visibility = View.VISIBLE
                    myGrad.setColor(resources.getColor(R.color.grey))
                } else {
                    tv.visibility = View.INVISIBLE
                }
            }

            if (viewModel.showTimeDetail) {
                if (viewModel.showSummerTime) {
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