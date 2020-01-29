package com.suda.yzune.wakeupschedule.schedule

import android.appwidget.AppWidgetManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.card.MaterialCardView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_course_detail.*
import kotlinx.android.synthetic.main.item_add_course_detail.*
import kotlinx.coroutines.delay
import splitties.activities.start
import splitties.dimensions.dip
import splitties.snackbar.longSnack

class CourseDetailFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_course_detail

    private lateinit var course: CourseBean
    private var nested: Boolean = false
    private val viewModel by activityViewModels<ScheduleViewModel>()

    private var makeSure = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            course = it.getParcelable<CourseBean>("course") as CourseBean
            nested = it.getBoolean("nested")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (!nested) {
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.setLayout(context!!.dip(280), ViewGroup.LayoutParams.WRAP_CONTENT)
            val root = inflater.inflate(R.layout.fragment_base_dialog, container, false)
            val cardView = root.findViewById<MaterialCardView>(R.id.base_card_view)
            LayoutInflater.from(context).inflate(layoutId, cardView, true)
            return root
        } else {
            container!!.layoutParams.width = context!!.dip(280)
            val root = inflater.inflate(R.layout.fragment_base_dialog, container, false)
            val cardView = root.findViewById<MaterialCardView>(R.id.base_card_view)
            cardView.setBackgroundColor(Color.TRANSPARENT)
            LayoutInflater.from(context).inflate(layoutId, cardView, true)
            cardView.findViewById<View>(R.id.include_detail).setBackgroundColor(Color.TRANSPARENT)
            return root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        showData()
        initEvent()
    }

    private fun initView() {
        tv_item.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    }

    private fun showData() {
        tv_item.text = course.courseName
        et_teacher.text = course.teacher
        et_room.text = course.room
        val type = when (course.type) {
            1 -> "单周"
            2 -> "双周"
            else -> ""
        }
        et_weeks.text = "第${course.startWeek} - ${course.endWeek}周    $type"
        try {
            et_time.text = "第${course.startNode} - ${course.startNode + course.step - 1}节    ${viewModel.timeList[course.startNode - 1].startTime} - ${viewModel.timeList[course.startNode + course.step - 2].endTime}"
        } catch (e: Exception) {
            et_time.longSnack("该课程似乎有点问题哦>_<请修改一下")
        }
    }

    override fun dismiss() {
        if (nested) {
            (parentFragment as DialogFragment).dismiss()
        } else {
            super.dismiss()
        }
    }

    private fun initEvent() {
        ib_delete.setOnClickListener {
            dismiss()
        }

        ib_edit.setOnClickListener {
            dismiss()
            activity!!.start<AddCourseActivity> {
                putExtra("id", course.id)
                putExtra("tableId", course.tableId)
                putExtra("maxWeek", viewModel.table.maxWeek)
                putExtra("nodes", viewModel.table.nodes)
            }
        }

        ib_delete_course.setOnClickListener {
            if (makeSure == 0) {
                tv_tips.visibility = View.VISIBLE
                makeSure++
                launch {
                    delay(5000)
                    tv_tips.visibility = View.GONE
                    makeSure = 0
                }
            } else {
                launch {
                    try {
                        viewModel.deleteCourseBean(course)
                        Toasty.success(context!!.applicationContext, "删除成功").show()
                        val appWidgetManager = AppWidgetManager.getInstance(activity!!.applicationContext)
                        val list = viewModel.getScheduleWidgetIds()
                        list.forEach {
                            when (it.detailType) {
                                0 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_schedule)
                                1 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_course)
                            }
                        }
                        dismiss()
                    } catch (e: Exception) {
                        Toasty.error(context!!.applicationContext, "出现异常>_<\n" + e.message).show()
                    }
                }
            }
        }

        ib_delete_course.setOnLongClickListener {
            launch {
                try {
                    viewModel.deleteCourseBaseBean(course.id, course.tableId)
                    Toasty.success(context!!.applicationContext, "删除成功").show()
                    val appWidgetManager = AppWidgetManager.getInstance(activity!!.applicationContext)
                    val list = viewModel.getScheduleWidgetIds()
                    list.forEach {
                        when (it.detailType) {
                            0 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_schedule)
                            1 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_course)
                        }
                    }
                    dismiss()
                } catch (e: Exception) {
                    Toasty.error(context!!.applicationContext, "出现异常>_<\n" + e.message).show()
                }
            }
            return@setOnLongClickListener true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: CourseBean, arg1: Boolean = false) =
                CourseDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("course", arg)
                        putBoolean("nested", arg1)
                    }
                }
    }
}
