package com.suda.yzune.wakeupschedule.main

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.schedule.ScheduleViewModel
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countWeek

private const val weekParam = "week"

class MainFragment : BaseFragment() {

    private var week = 0
    private var weekDay = 1
    private lateinit var weekDate: List<String>
    private lateinit var viewModel: ScheduleViewModel

    private lateinit var navImageButton: TextView
    private lateinit var shareImageButton: TextView
    private lateinit var addImageButton: TextView
    private lateinit var importImageButton: TextView
    private lateinit var moreImageButton: TextView
    private lateinit var dateTextView: TextView
    private lateinit var weekTextView: TextView
    private lateinit var weekdayTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            week = it.getInt(weekParam)
        }
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
        weekDay = CourseUtils.getWeekdayInt()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val iconFont = ResourcesCompat.getFont(context!!, R.font.iconfont)
        val statusBarMargin = viewModel.statusBarMargin
        val outValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

//        return UI {
//            constraintLayout {
//                id = R.id.anko_cl_schedule
//
//                dateTextView = textView {
//                    id = R.id.anko_tv_date
//                    gravity = Gravity.CENTER
//                    textColor = viewModel.table.textColor
//                    textSize = 20f
//                    typeface = Typeface.DEFAULT_BOLD
//                }.lparams {
//                    startToStart = ConstraintSet.PARENT_ID
//                    topToTop = ConstraintSet.PARENT_ID
//                    bottomToBottom = R.id.anko_ib_nav
//                    marginStart = dip(24)
//                    topMargin = statusBarMargin
//                }
//
//                weekTextView = textView {
//                    id = R.id.anko_tv_week
//                    textColor = viewModel.table.textColor
//                }.lparams {
//                    startToStart = R.id.anko_tv_date
//                    topToBottom = R.id.anko_tv_date
//                    topMargin = dip(4)
//                }
//
//                weekdayTextView = textView {
//                    id = R.id.anko_tv_weekday
//                    textColor = viewModel.table.textColor
//                }.lparams {
//                    startToEnd = R.id.anko_tv_week
//                    topToBottom = R.id.anko_tv_date
//                    topMargin = dip(4)
//                    marginStart = dip(8)
//                }
//
//                // 导航按钮
//                navImageButton = textView("\uE6A7") {
//                    id = R.id.anko_ib_nav
//                    backgroundResource = outValue.resourceId
//                    textSize = 20f
//                    gravity = Gravity.CENTER
//                    includeFontPadding = false
//                    typeface = iconFont
//                    textColor = viewModel.table.textColor
//                }.lparams(dip(32), dip(32)) {
//                    topMargin = statusBarMargin
//                    endToStart = R.id.anko_tv_date
//                    topToTop = ConstraintSet.PARENT_ID
//                }
//
//                // 添加按钮
//                addImageButton = textView("\uE6DC") {
//                    id = R.id.anko_ib_add
//                    backgroundResource = outValue.resourceId
//                    textSize = 20f
//                    gravity = Gravity.CENTER
//                    includeFontPadding = false
//                    typeface = iconFont
//                    textColor = viewModel.table.textColor
//                }.lparams(dip(32), dip(32)) {
//                    topMargin = statusBarMargin
//                    endToStart = R.id.anko_ib_import
//                    topToTop = ConstraintSet.PARENT_ID
//                }
//
//                // 导入按钮
//                importImageButton = textView("\uE6E2") {
//                    id = R.id.anko_ib_import
//                    backgroundResource = outValue.resourceId
//                    textSize = 20f
//                    gravity = Gravity.CENTER
//                    includeFontPadding = false
//                    typeface = iconFont
//                    textColor = viewModel.table.textColor
//                }.lparams(dip(32), dip(32)) {
//                    topMargin = statusBarMargin
//                    endToStart = R.id.anko_ib_share
//                    topToTop = ConstraintSet.PARENT_ID
//                }
//
//                // 分享按钮
//                shareImageButton = textView("\uE6BA") {
//                    id = R.id.anko_ib_share
//                    backgroundResource = outValue.resourceId
//                    textSize = 20f
//                    gravity = Gravity.CENTER
//                    includeFontPadding = false
//                    typeface = iconFont
//                    textColor = viewModel.table.textColor
//                }.lparams(dip(32), dip(32)) {
//                    topMargin = statusBarMargin
//                    endToStart = R.id.anko_ib_more
//                    topToTop = ConstraintSet.PARENT_ID
//                }
//
//                moreImageButton = textView("\uE6BF") {
//                    id = R.id.anko_ib_more
//                    backgroundResource = outValue.resourceId
//                    textSize = 20f
//                    gravity = Gravity.CENTER
//                    includeFontPadding = false
//                    typeface = iconFont
//                    textColor = viewModel.table.textColor
//                }.lparams(dip(32), dip(32)) {
//                    topMargin = statusBarMargin
//                    marginEnd = dip(8)
//                    endToEnd = ConstraintSet.PARENT_ID
//                    topToTop = ConstraintSet.PARENT_ID
//                }
//
//                addView((ViewUtils.createScheduleView(context!!, viewModel.table.textColor, weekDay) as ConstraintLayout)
//                        .lparams(matchParent, 0) {
//                            topToBottom = R.id.anko_tv_week
//                            bottomToBottom = ConstraintSet.PARENT_ID
//                            startToStart = ConstraintSet.PARENT_ID
//                            endToEnd = ConstraintSet.PARENT_ID
//                        })
//            }
//        }.view
        return null
    }

    override fun onResume() {
        super.onResume()
        val currentWeek = countWeek(viewModel.table.startDate, viewModel.table.sundayFirst)

        if (currentWeek > 0) {
            weekTextView.text = "第${currentWeek}周"
        } else {
            weekTextView.text = "还没有开学哦"
        }

        weekdayTextView.text = CourseUtils.getWeekday()
        dateTextView.text = CourseUtils.getTodayDate()
    }

//    private fun initWeekPanel(data: List<CourseBean>?, day: Int, table: TableBean) {
//        val ll = find<LinearLayout>(R.id.anko_ll_week_panel_1 + day - 1)
//        ll.removeAllViews()
//        if (day == 7) {
//            find<LinearLayout>(R.id.anko_ll_week_panel_0).removeAllViews()
//        }
//        if (data == null || data.isEmpty()) return
//        var isCovered = false
//        var pre = data[0]
//        for (i in data.indices) {
//            val strBuilder = StringBuilder()
//            val c = data[i]
//
//            if (!table.showOtherWeekCourse) {
//                if (week % 2 == 0 && c.type == 1) {
//                    continue
//                }
//                if (week % 2 == 1 && c.type == 2) {
//                    continue
//                }
//                if (c.startWeek > week || c.endWeek < week) {
//                    continue
//                }
//            }
//
//            val textView = TipTextView(table.courseTextColor, table.itemTextSize, context!!)
//
//            val lp = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    viewModel.itemHeight * c.step + viewModel.marTop * (c.step - 1))
//            if (day == 7 && table.sundayFirst) {
//                if (find<LinearLayout>(R.id.anko_ll_week_panel_0).childCount == 0) {
//                    lp.setMargins(0, (c.startNode - 1) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
//                } else {
//                    lp.setMargins(0, (c.startNode - (pre.startNode + pre.step)) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
//                    isCovered = (pre.startNode == c.startNode)
//                }
//            } else {
//                if (ll.childCount == 0) {
//                    lp.setMargins(0, (c.startNode - 1) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
//                } else {
//                    lp.setMargins(0, (c.startNode - (pre.startNode + pre.step)) * (viewModel.itemHeight + viewModel.marTop) + viewModel.marTop, 0, 0)
//                    isCovered = (pre.startNode == c.startNode)
//                }
//            }
//
//            textView.layoutParams = lp
//            textView.padding = dip(4)
////            textView.textSize = table.itemTextSize.toFloat()
////            textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
////            textView.setPadding(dip(4), dip(4), dip(4), dip(4))
////            textView.setTextColor(table.courseTextColor)
//
//            textView.background = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.course_item_bg)
//            val myGrad = textView.background as GradientDrawable
//            myGrad.setStroke(dip(2), table.strokeColor)
//
//            if (c.color == "") {
//                c.color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(activity!!, c.id % 9))}"
//            }
//
//            try {
//                if (c.color.length == 7) {
//                    myGrad.setColor(Color.parseColor("#${viewModel.alphaStr}${c.color.substring(1, 7)}"))
//                } else {
//                    myGrad.setColor(Color.parseColor("#${viewModel.alphaStr}${c.color.substring(3, 9)}"))
//                }
//            } catch (e: Exception) {
//                myGrad.setColor(Color.parseColor(c.color))
//            }
//
//            strBuilder.append(c.courseName)
//
//            if (c.room != "") {
//                strBuilder.append("\n@${c.room}")
//            }
//
//            when (c.type) {
//                1 -> {
//                    if (week % 2 == 0) {
//                        if (table.showOtherWeekCourse) {
//                            strBuilder.append("\n单周[非本周]")
//                            textView.visibility = View.VISIBLE
//                            textView.alpha = 0.6f
//                            myGrad.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
//                        } else {
//                            textView.visibility = View.INVISIBLE
//                        }
//                    } else {
//                        strBuilder.append("\n单周")
//                    }
//                }
//                2 -> {
//                    if (week % 2 != 0) {
//                        if (table.showOtherWeekCourse) {
//                            textView.alpha = 0.6f
//                            strBuilder.append("\n双周[非本周]")
//                            textView.visibility = View.VISIBLE
//                            myGrad.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
//                        } else {
//                            textView.visibility = View.INVISIBLE
//                        }
//                    } else {
//                        strBuilder.append("\n双周")
//                    }
//                }
//            }
//
//            if (c.startWeek > week || c.endWeek < week) {
//                if (table.showOtherWeekCourse) {
//                    textView.alpha = 0.6f
//                    if (!strBuilder.endsWith("[非本周]")) {
//                        strBuilder.append("[非本周]")
//                    }
//                    textView.visibility = View.VISIBLE
//                    myGrad.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
//                } else {
//                    textView.visibility = View.INVISIBLE
//                }
//            }
//
//            if (!strBuilder.endsWith("[非本周]") && ll.findViewWithTag<TextView?>("第${c.startNode}节") == null) {
//                textView.tag = "第${c.startNode}节"
//            }
//
//            if (isCovered) {
//                if (ll.getChildAt(ll.childCount - 1) != null) {
//                    if (ll.getChildAt(ll.childCount - 1).alpha < 0.8f) {
//                        ll.getChildAt(ll.childCount - 1).visibility = View.INVISIBLE
//                    }
//                }
//            }
//
//            if (ll.findViewWithTag<TextView?>("第${c.startNode}节") != null) {
//                textView.visibility = View.INVISIBLE
//                val tv = ll.findViewWithTag<TipTextView>("第${c.startNode}节")
//                if (tv.tipVisibility == TipTextView.TIP_INVISIBLE) {
//                    tv.tipVisibility = TipTextView.TIP_VISIBLE
//                    tv.setOnClickListener {
//                        MultiCourseFragment.newInstance(week, c.day, c.startNode).show(fragmentManager!!, "multi")
//                    }
//                }
//            }
//
//            if (table.showTime && viewModel.timeList.isNotEmpty()) {
//                strBuilder.insert(0, viewModel.timeList[c.startNode - 1].startTime + "\n")
//            }
//            textView.text = strBuilder.toString()
//
//            textView.setOnClickListener {
//                try {
//                    val detailFragment = CourseDetailFragment.newInstance(c)
//                    detailFragment.show(fragmentManager!!, "courseDetail")
//                } catch (e: Exception) {
//                    //TODO: 提示是否要删除异常的数据
//                    Toasty.error(activity!!.applicationContext, "哎呀>_<差点崩溃了").show()
//                }
//            }
//
//            if (day == 7) {
//                if (table.sundayFirst) {
//                    find<LinearLayout>(R.id.anko_ll_week_panel_0).addView(textView)
//                } else {
//                    ll.addView(textView)
//                }
//            } else {
//                ll.addView(textView)
//            }
//            pre = c
//        }
//    }
//
//    private fun initEvent() {
//        addImageButton.setOnClickListener {
//            context?.startActivity<AddCourseActivity>(
//                    "tableId" to viewModel.table.id,
//                    "maxWeek" to viewModel.table.maxWeek,
//                    "nodes" to viewModel.table.nodes,
//                    "id" to -1)
//        }
//
//        moreImageButton.setOnClickListener {
//            (activity as MainActivity).viewPager.currentItem = 2
//        }
//
//        navImageButton.setOnClickListener {
//            (activity as MainActivity).viewPager.currentItem = 0
//        }
//
//        shareImageButton.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
//            } else {
//                ExportSettingsFragment().show(activity!!.supportFragmentManager, "share")
//            }
//        }
//
//        importImageButton.setOnClickListener {
//            ImportChooseFragment().show(activity!!.supportFragmentManager, "importDialog")
//        }
//    }

    companion object {
        @JvmStatic
        fun newInstance(arg0: Int) =
                MainFragment().apply {
                    arguments = Bundle().apply {
                        putInt(weekParam, arg0)
                    }
                }
    }
}
