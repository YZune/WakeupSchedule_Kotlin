package com.suda.yzune.wakeupschedule.schedule

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.suda.yzune.wakeupschedule.widget.TipTextView
import es.dmoral.toasty.Toasty
import splitties.dimensions.dip

private const val weekParam = "week"

class ScheduleFragment : BaseFragment() {

    private var week = 0
    private var weekDay = 1
    private lateinit var weekDate: List<String>
    private val viewModel by activityViewModels<ScheduleViewModel>()
    private lateinit var ui: ScheduleUI
    private var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            week = it.getInt(weekParam)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        weekDay = CourseUtils.getWeekdayInt()
        ui = ScheduleUI(context!!, viewModel.table, weekDay)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weekDate = CourseUtils.getDateStringFromWeek(CourseUtils.countWeek(viewModel.table.startDate, viewModel.table.sundayFirst), week, viewModel.table.sundayFirst)
        view.findViewById<TextView>(R.id.anko_tv_title0).text = weekDate[0] + "\n月"
        var textView: TextView
        for (i in 1..7) {
            if (ui.dayMap[i] == -1) continue
            textView = view.findViewById(R.id.anko_tv_title0 + ui.dayMap[i])
            if (i == 7 && !viewModel.table.showSat && !viewModel.table.sundayFirst) {
                textView.text = viewModel.daysArray[i] + "\n${weekDate[7]}"
            } else {
                textView.text = viewModel.daysArray[i] + "\n${weekDate[ui.dayMap[i]]}"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isLoaded) {
            for (i in 1..7) {
                viewModel.allCourseList[i - 1].observe(this, Observer {
                    initWeekPanel(it, i, viewModel.table)
                })
            }
            isLoaded = true
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

    private fun initWeekPanel(data: List<CourseBean>?, day: Int, table: TableBean) {
        val ll = ui.content.findViewById<FrameLayout>(R.id.anko_ll_week_panel_0 + ui.dayMap[day] - 1)
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

            val strBuilder = StringBuilder()
            if (c.step <= 0) {
                c.step = 1
                Toasty.info(context!!, R.string.error_course_data, Toast.LENGTH_LONG).show()
            }
            if (c.startNode <= 0) {
                c.startNode = 1
                Toasty.info(context!!, R.string.error_course_data, Toast.LENGTH_LONG).show()
            }
            if (c.startNode > table.nodes) {
                c.startNode = table.nodes
                Toasty.info(context!!, R.string.error_course_node, Toast.LENGTH_LONG).show()
            }
            if (c.startNode + c.step - 1 > table.nodes) {
                c.step = table.nodes - c.startNode + 1
                Toasty.info(context!!, R.string.error_course_node, Toast.LENGTH_LONG).show()
            }

            val textView = TipTextView(context!!)

            if (ll.childCount != 0) {
                isCovered = (pre.startNode == c.startNode)
            }

            textView.setPadding(context!!.dip(4))

            if (c.color.isEmpty()) {
                c.color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(activity!!, c.id % 9))}"
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
                textView.alpha = 0.2f
            } else {
                when (c.type) {
                    1 -> strBuilder.append("\n单周")
                    2 -> strBuilder.append("\n双周")
                }
            }

            if (isCovered) {
                if (ll.getChildAt(ll.childCount - 1) != null) {
                    if (ll.getChildAt(ll.childCount - 1).alpha < 0.8f) {
                        ll.getChildAt(ll.childCount - 1).visibility = View.INVISIBLE
                    }
                }
            }

            val tv = ll.findViewWithTag<TipTextView?>(c.startNode)
            if (tv != null) {
                textView.visibility = View.INVISIBLE
                if (tv.tipVisibility == TipTextView.TIP_INVISIBLE && !isOtherWeek) {
                    tv.tipVisibility = TipTextView.TIP_VISIBLE
                    tv.setOnClickListener {
                        MultiCourseFragment.newInstance(week, c.day, c.startNode).show(parentFragmentManager, "multi")
                    }
                }
            }

            if (!isOtherWeek) {
                textView.tag = c.startNode
            }

            if (table.showTime && viewModel.timeList.isNotEmpty()) {
                strBuilder.insert(0, viewModel.timeList[c.startNode - 1].startTime + "\n")
            }

            textView.init(
                    text = strBuilder.toString(),
                    txtSize = table.itemTextSize,
                    txtColor = table.courseTextColor,
                    bgColor = Color.parseColor(c.color),
                    bgAlpha = viewModel.alphaInt,
                    stroke = table.strokeColor
            )

            textView.setOnClickListener {
                try {
                    val detailFragment = CourseDetailFragment.newInstance(c)
                    detailFragment.show(parentFragmentManager, "courseDetail")
                } catch (e: Exception) {
                    //TODO: 提示是否要删除异常的数据
                    Toasty.error(activity!!.applicationContext, "哎呀>_<差点崩溃了").show()
                }
            }

            ll.addView(textView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    viewModel.itemHeight * c.step + viewModel.marTop * (c.step - 1)).apply {
                gravity = Gravity.TOP
                topMargin = (c.startNode - 1) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop
            })

            pre = c
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded = false
    }

}