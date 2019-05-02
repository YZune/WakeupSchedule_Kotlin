package com.suda.yzune.wakeupschedule.schedule

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.suda.yzune.wakeupschedule.widget.TipTextView
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.find

private const val weekParam = "week"

class ScheduleFragment : BaseFragment() {

    private var week = 0
    private var weekDay = 1
    private lateinit var weekDate: List<String>
    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            week = it.getInt(weekParam)
        }
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ViewUtils.createScheduleView(context!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weekDay = CourseUtils.getWeekdayInt()
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

        weekDate = CourseUtils.getDateStringFromWeek(CourseUtils.countWeek(viewModel.table.startDate, viewModel.table.sundayFirst), week, viewModel.table.sundayFirst)
        find<TextView>(R.id.anko_tv_title0).setTextColor(viewModel.table.textColor)
        find<TextView>(R.id.anko_tv_title0).text = weekDate[0] + "\n月"
        var textView: TextView
        if (viewModel.table.sundayFirst) {
            for (i in 0..6) {
                textView = find(R.id.anko_tv_title0_1 + i)
                textView.setTextColor(viewModel.table.textColor)
                if (weekDay == 7 && i == 0) {
                    textView.onShineEffect(viewModel.table.textColor)
                }
                if (weekDay == i) {
                    textView.onShineEffect(viewModel.table.textColor)
                }
                textView.text = viewModel.daysArray[i] + "\n${weekDate[i + 1]}"
            }
        } else {
            for (i in 0..6) {
                textView = find(R.id.anko_tv_title1 + i)
                textView.setTextColor(viewModel.table.textColor)
                if (i == weekDay - 1) {
                    textView.onShineEffect(viewModel.table.textColor)
                }
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

        for (i in 0 until 30) {
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
                initWeekPanel(it, i, viewModel.table)
            })
        }
    }

    private fun TextView.onShineEffect(colorInt: Int) {
        //this.setShadowLayer(12f, 0f, 0f, Color.WHITE)
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
        val ll = find<LinearLayout>(R.id.anko_ll_week_panel_1 + day - 1)
        ll.removeAllViews()
        if (day == 7) {
            find<LinearLayout>(R.id.anko_ll_week_panel_0).removeAllViews()
        }
        if (data == null || data.isEmpty()) return
        var isCovered = false
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

            val textView = TipTextView(context!!)

            if (day == weekDay) {
                textView.onShineEffect(viewModel.table.courseTextColor)
            }

            val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    viewModel.itemHeight * c.step + viewModel.marTop * (c.step - 1))
            if (day == 7 && table.sundayFirst) {
                if (find<LinearLayout>(R.id.anko_ll_week_panel_0).childCount == 0) {
                    lp.setMargins(0, (c.startNode - 1) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
                } else {
                    lp.setMargins(0, (c.startNode - (pre.startNode + pre.step)) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
                    isCovered = (pre.startNode == c.startNode)
                }
            } else {
                if (ll.childCount == 0) {
                    lp.setMargins(0, (c.startNode - 1) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
                } else {
                    lp.setMargins(0, (c.startNode - (pre.startNode + pre.step)) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
                    isCovered = (pre.startNode == c.startNode)
                }
            }

            textView.layoutParams = lp
            textView.textSize = table.itemTextSize.toFloat()
            textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            textView.setPadding(dip(4), dip(4), dip(4), dip(4))
            textView.setTextColor(table.courseTextColor)

            textView.background = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.course_item_bg)
            val myGrad = textView.background as GradientDrawable
            myGrad.setStroke(dip(2), table.strokeColor)

            if (c.color == "") {
                c.color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(activity!!, c.id % 9))}"
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
                    if (!strBuilder.endsWith("[非本周]")) {
                        strBuilder.append("[非本周]")
                    }
                    textView.visibility = View.VISIBLE
                    myGrad.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
                } else {
                    textView.visibility = View.INVISIBLE
                }
            }

            if (!strBuilder.endsWith("[非本周]") && ll.findViewWithTag<TextView?>("第${c.startNode}节") == null) {
                textView.tag = "第${c.startNode}节"
            }

            if (isCovered) {
                if (ll.getChildAt(ll.childCount - 1) != null) {
                    if (ll.getChildAt(ll.childCount - 1).alpha < 0.8f) {
                        ll.getChildAt(ll.childCount - 1).visibility = View.INVISIBLE
                    }
                }
            }

            if (ll.findViewWithTag<TextView?>("第${c.startNode}节") != null) {
                textView.visibility = View.INVISIBLE
                val tv = ll.findViewWithTag<TipTextView>("第${c.startNode}节")
                if (tv.tipVisibility == TipTextView.TIP_INVISIBLE) {
                    tv.tipVisibility = TipTextView.TIP_VISIBLE
                    tv.setOnClickListener {
                        MultiCourseFragment.newInstance(week, c.day, c.startNode).show(fragmentManager!!, "multi")
                    }
                }
            }

            if (table.showTime) {
                strBuilder.insert(0, viewModel.timeList[c.startNode - 1].startTime + "\n")
            }
            textView.text = strBuilder

            textView.setOnClickListener {
                try {
                    val detailFragment = CourseDetailFragment.newInstance(c)
                    detailFragment.show(fragmentManager!!, "courseDetail")
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

}