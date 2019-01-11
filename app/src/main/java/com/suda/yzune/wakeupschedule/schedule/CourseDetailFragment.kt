package com.suda.yzune.wakeupschedule.schedule


import android.appwidget.AppWidgetManager
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_course_detail.*
import kotlinx.coroutines.*
import org.jetbrains.anko.startActivity
import kotlin.coroutines.CoroutineContext

class CourseDetailFragment : DialogFragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var course: CourseBean
    private lateinit var title: TextView
    private lateinit var weeks: TextView
    private lateinit var time: TextView
    private lateinit var teacher: TextView
    private lateinit var room: TextView
    private lateinit var close: TextView
    private lateinit var viewModel: ScheduleViewModel

    private lateinit var job: Job

    private var makeSure = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            course = it.getParcelable("course") as CourseBean
        }
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
        job = Job()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_course_detail, container, false)
    }

    override fun onResume() {
        super.onResume()
        initView()
        showData()
        initEvent()
    }

    private fun initView() {
        title = include_detail.findViewById(R.id.tv_item)
        weeks = include_detail.findViewById(R.id.et_weeks)
        time = include_detail.findViewById(R.id.et_time)
        teacher = include_detail.findViewById(R.id.et_teacher)
        room = include_detail.findViewById(R.id.et_room)
        close = include_detail.findViewById(R.id.ib_delete)

        title.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.black))
        title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        weeks.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.black))
        time.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.black))
        teacher.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.black))
        room.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.black))

        teacher.isFocusable = false
        teacher.isFocusableInTouchMode = false
        room.isFocusable = false
        room.isFocusableInTouchMode = false
    }

    private fun showData() {
        title.text = course.courseName
        teacher.text = course.teacher
        room.text = course.room
        var type = ""
        when (course.type) {
            1 -> type = "单周"
            2 -> type = "双周"
        }
        weeks.text = "第${course.startWeek} - ${course.endWeek}周    $type"
        time.text = "第${course.startNode} - ${course.startNode + course.step - 1}节    ${viewModel.timeList[course.startNode - 1].startTime} - ${viewModel.timeList[course.startNode + course.step - 2].endTime}"
    }

    private fun initEvent() {
        close.setOnClickListener {
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
                    val msg = async(Dispatchers.IO) {
                        try {
                            viewModel.deleteCourseBean(course)
                            "ok"
                        } catch (e: Exception) {
                            "出现异常>_<\n" + e.message
                        }
                    }.await()
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
            }
        }

        ib_delete_course.setOnLongClickListener {
            launch {
                val msg = async(Dispatchers.IO) {
                    try {
                        viewModel.deleteCourseBaseBean(course.id, course.tableId)
                        "ok"
                    } catch (e: Exception) {
                        "出现异常>_<\n" + e.message
                    }
                }.await()
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

    override fun dismiss() {
        super.dismiss()
        job.cancel()
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: CourseBean) =
                CourseDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("course", arg)
                    }
                }
    }
}
