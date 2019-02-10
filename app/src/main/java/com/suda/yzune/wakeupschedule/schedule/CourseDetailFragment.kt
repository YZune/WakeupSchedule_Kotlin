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
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.card.MaterialCardView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_course_detail.*
import kotlinx.android.synthetic.main.item_add_course_detail.*
import kotlinx.coroutines.*
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.dip

class CourseDetailFragment : BaseDialogFragment(), CoroutineScope {

    override val layoutId: Int
        get() = R.layout.fragment_course_detail

    private lateinit var course: CourseBean
    private var nested: Boolean = false
    private lateinit var viewModel: ScheduleViewModel

    private var makeSure = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            course = it.getParcelable("course") as CourseBean
            nested = it.getBoolean("nested")
        }
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (!nested) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(dip(280), ViewGroup.LayoutParams.WRAP_CONTENT)
        } else {
            container!!.layoutParams.width = dip(280)
        }
        val root = inflater.inflate(R.layout.fragment_base_dialog, container, false)
        val cardView = root.find<MaterialCardView>(R.id.base_card_view)
        LayoutInflater.from(context).inflate(layoutId, cardView, true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        showData()
        initEvent()
    }

    private fun initView() {
        tv_item.setTextColor(Color.BLACK)
        tv_item.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        et_weeks.setTextColor(Color.BLACK)
        et_time.setTextColor(Color.BLACK)
        et_teacher.setTextColor(Color.BLACK)
        et_room.setTextColor(Color.BLACK)

        et_teacher.isEnabled = false
        et_teacher.isFocusable = false
        et_teacher.isFocusableInTouchMode = false
        et_room.isEnabled = false
        et_room.isFocusable = false
        et_room.isFocusableInTouchMode = false
    }

    private fun showData() {
        tv_item.text = course.courseName
        et_teacher.setText(course.teacher)
        et_room.setText(course.room)
        val type = when (course.type) {
            1 -> "单周"
            2 -> "双周"
            else -> ""
        }
        et_weeks.text = "第${course.startWeek} - ${course.endWeek}周    $type"
        et_time.text = "第${course.startNode} - ${course.startNode + course.step - 1}节    ${viewModel.timeList[course.startNode - 1].startTime} - ${viewModel.timeList[course.startNode + course.step - 2].endTime}"
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
            activity!!.startActivity<AddCourseActivity>(
                    "id" to course.id,
                    "tableId" to course.tableId,
                    "maxWeek" to viewModel.table.maxWeek,
                    "nodes" to viewModel.table.nodes
            )
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
                    val msg = withContext(Dispatchers.IO) {
                        try {
                            viewModel.deleteCourseBean(course)
                            "ok"
                        } catch (e: Exception) {
                            "出现异常>_<\n" + e.message
                        }
                    }
                    if (msg == "ok") {
                        Toasty.success(context!!.applicationContext, "删除成功").show()
                        val appWidgetManager = AppWidgetManager.getInstance(activity!!.applicationContext)
                        val list = withContext(Dispatchers.IO) {
                            viewModel.getScheduleWidgetIds()
                        }
                        list.forEach {
                            when (it.detailType) {
                                0 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_schedule)
                                1 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_course)
                            }
                        }
                        dismiss()
                    } else {
                        Toasty.error(context!!.applicationContext, msg).show()
                    }
                }
            }
        }

        ib_delete_course.setOnLongClickListener {
            launch {
                val msg = withContext(Dispatchers.IO) {
                    try {
                        viewModel.deleteCourseBaseBean(course.id, course.tableId)
                        "ok"
                    } catch (e: Exception) {
                        "出现异常>_<\n" + e.message
                    }
                }
                if (msg == "ok") {
                    Toasty.success(context!!.applicationContext, "删除成功").show()
                    val appWidgetManager = AppWidgetManager.getInstance(activity!!.applicationContext)
                    val list = async(Dispatchers.IO) {
                        viewModel.getScheduleWidgetIds()
                    }.await()
                    list.forEach {
                        when (it.detailType) {
                            0 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_schedule)
                            1 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_course)
                        }
                    }
                    dismiss()
                } else {
                    Toasty.error(context!!.applicationContext, msg).show()
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
