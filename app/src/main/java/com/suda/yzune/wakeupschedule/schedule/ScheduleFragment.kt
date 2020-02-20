package com.suda.yzune.wakeupschedule.schedule

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.utils.Const
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.suda.yzune.wakeupschedule.utils.getPrefer
import com.suda.yzune.wakeupschedule.widget.TipTextView
import es.dmoral.toasty.Toasty
import splitties.dimensions.dip

class ScheduleFragment : BaseFragment() {

    private var week = 0
    private var preLoad = true
    private var weekDay = 1
    private lateinit var weekDate: List<String>
    private val viewModel by activityViewModels<ScheduleViewModel>()
    private lateinit var ui: ScheduleUI
    private var isLoaded = false
    private lateinit var showCourseNumber: LiveData<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            week = it.getInt("week")
            preLoad = it.getBoolean("preLoad")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        weekDay = CourseUtils.getWeekdayInt()
        ui = ScheduleUI(context!!, viewModel.table, if (week == viewModel.currentWeek) weekDay else -1)
        ui.showTimeDetail = context!!.getPrefer().getBoolean(Const.KEY_SCHEDULE_DETAIL_TIME, true)
        showCourseNumber = viewModel.getShowCourseNumber(week)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weekDate = CourseUtils.getDateStringFromWeek(CourseUtils.countWeek(viewModel.table.startDate, viewModel.table.sundayFirst), week, viewModel.table.sundayFirst)
        ((view as ConstraintLayout).getViewById(R.id.anko_tv_title0) as AppCompatTextView).text = weekDate[0] + "\n月"
        var textView: AppCompatTextView?
        for (i in 1..7) {
            if (ui.dayMap[i] == -1) continue
            textView = view.getViewById(R.id.anko_tv_title0 + ui.dayMap[i]) as AppCompatTextView
            if (i == 7 && !viewModel.table.showSat && !viewModel.table.sundayFirst) {
                textView.text = viewModel.daysArray[i] + "\n${weekDate[7]}"
            } else if (!viewModel.table.showSun && viewModel.table.sundayFirst && i != 7) {
                textView.text = viewModel.daysArray[i] + "\n${weekDate[ui.dayMap[i] + 1]}"
            } else {
                textView.text = viewModel.daysArray[i] + "\n${weekDate[ui.dayMap[i]]}"
            }
        }
        if (viewModel.timeList.isNotEmpty() && ui.showTimeDetail) {
            for (i in 0 until viewModel.table.nodes) {
                (ui.content.getViewById(R.id.anko_tv_node1 + i) as FrameLayout).apply {
                    findViewById<AppCompatTextView>(R.id.tv_start).text = viewModel.timeList[i].startTime
                    findViewById<AppCompatTextView>(R.id.tv_end).text = viewModel.timeList[i].endTime
                }
            }
        }
        if (preLoad) {
            for (i in 1..7) {
                viewModel.allCourseList[i - 1].observe(viewLifecycleOwner, Observer {
                    initWeekPanel(it, i, viewModel.table)
                })
            }
        }
        showCourseNumber.observe(viewLifecycleOwner, Observer {
            if (it == 0) {
                ui.content.visibility = View.GONE
                if (ui.root.getViewById(R.id.anko_empty_view) != null) {
                    return@Observer
                }
                val img = AppCompatImageView(context!!).apply {
                    setImageResource(R.drawable.ic_schedule_empty)
                }
                ui.root.addView(LinearLayoutCompat(context!!).apply {
                    id = R.id.anko_empty_view
                    orientation = LinearLayoutCompat.VERTICAL
                    if (context.getPrefer().getBoolean(Const.KEY_SHOW_EMPTY_VIEW, true)) {
                        addView(img, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, dip(240))
                    }
                    addView(AppCompatTextView(context).apply {
                        text = "本周没有课程哦"
                        setTextColor(viewModel.table.textColor)
                        gravity = Gravity.CENTER
                    }, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT).apply {
                        topMargin = dip(16)
                    })
                }, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                    startToStart = ConstraintSet.PARENT_ID
                    endToEnd = ConstraintSet.PARENT_ID
                    topToBottom = R.id.anko_tv_title0
                    bottomToBottom = ConstraintSet.PARENT_ID
                    marginStart = context!!.dip(32)
                    marginEnd = context!!.dip(32)
                })
            } else {
                ui.content.visibility = View.VISIBLE
                ui.root.getViewById(R.id.anko_empty_view)?.let { emptyView ->
                    ui.root.removeView(emptyView)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (preLoad) return
        if (!isLoaded) {
            for (i in 1..7) {
                viewModel.allCourseList[i - 1].observe(viewLifecycleOwner, Observer {
                    initWeekPanel(it, i, viewModel.table)
                })
            }
            isLoaded = true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(week: Int, preLoad: Boolean) =
                ScheduleFragment().apply {
                    arguments = Bundle().apply {
                        putInt("week", week)
                        putBoolean("preLoad", preLoad)
                    }
                }
    }

    private fun initWeekPanel(data: List<CourseBean>?, day: Int, table: TableBean) {
        val ll = ui.content.getViewById(R.id.anko_ll_week_panel_0 + ui.dayMap[day] - 1) as FrameLayout?
                ?: return
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

            var isError = false

            val strBuilder = StringBuilder()
            if (c.step <= 0) {
                c.step = 1
                isError = true
                Toasty.info(context!!, R.string.error_course_data, Toast.LENGTH_LONG).show()
            }
            if (c.startNode <= 0) {
                c.startNode = 1
                isError = true
                Toasty.info(context!!, R.string.error_course_data, Toast.LENGTH_LONG).show()
            }
            if (c.startNode > table.nodes) {
                c.startNode = table.nodes
                isError = true
                Toasty.info(context!!, R.string.error_course_node, Toast.LENGTH_LONG).show()
            }
            if (c.startNode + c.step - 1 > table.nodes) {
                c.step = table.nodes - c.startNode + 1
                isError = true
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
            } else {
                when (c.type) {
                    1 -> strBuilder.append("\n单周")
                    2 -> strBuilder.append("\n双周")
                }
            }

            if (isCovered) {
                val tv = ll.getChildAt(ll.childCount - 1) as TipTextView?
                if (tv != null) {
                    if (tv.tipVisibility == TipTextView.TIP_OTHER_WEEK) {
                        tv.visibility = View.INVISIBLE
                    }
                }
            }

            val tv = ll.findViewWithTag<TipTextView?>(c.startNode)
            if (tv != null) {
                textView.visibility = View.INVISIBLE
                if (tv.tipVisibility != TipTextView.TIP_VISIBLE && !isOtherWeek) {
                    if (tv.tipVisibility != TipTextView.TIP_ERROR) {
                        tv.tipVisibility = TipTextView.TIP_VISIBLE
                    }
                    tv.setOnClickListener {
                        MultiCourseFragment.newInstance(week, c.day, c.startNode).show(parentFragmentManager, "multi")
                    }
                }
            }

            if (isError) {
                textView.tipVisibility = TipTextView.TIP_ERROR
            }

            if (!isOtherWeek) {
                textView.tag = c.startNode
            } else {
                textView.tipVisibility = TipTextView.TIP_OTHER_WEEK
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