package com.suda.yzune.wakeupschedule.schedule


import android.appwidget.AppWidgetManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_course_detail.*

class CourseDetailFragment : DialogFragment() {

    lateinit var course: CourseBean
    lateinit var title: TextView
    lateinit var weeks: TextView
    lateinit var time: TextView
    lateinit var teacher: TextView
    lateinit var room: TextView
    lateinit var close: ImageButton
    private lateinit var viewModel: ScheduleViewModel

    var makeSure = 0

    private val timer = object : CountDownTimer(5000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            tv_tips.visibility = View.GONE
            makeSure = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
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
        viewModel.timeData.observe(this, Observer {
            if (it == null) return@Observer
            time.text = "第${course.startNode} - ${course.startNode + course.step - 1}节    ${it[course.startNode - 1].startTime} - ${it[course.startNode + course.step - 2].endTime}"
        })
    }

    private fun initEvent() {
        close.setOnClickListener {
            dismiss()
        }

        ib_edit.setOnClickListener {
            dismiss()
            val intent = Intent(activity, AddCourseActivity::class.java)
            intent.putExtra("id", course.id)
            intent.putExtra("tableId", course.tableId)
            intent.putExtra("maxWeek", viewModel.tableData.value!!.maxWeek)
            startActivity(intent)
        }

        ib_delete_course.setOnClickListener { _ ->
            if (makeSure == 0) {
                tv_tips.visibility = View.VISIBLE
                makeSure++
                timer.start()
            } else {
                viewModel.deleteCourseBean(course)
                Toasty.success(context!!.applicationContext, "删除成功").show()
                dismiss()
            }
        }

        ib_delete_course.setOnLongClickListener { _ ->
            val appWidgetManager = AppWidgetManager.getInstance(activity!!.applicationContext)
            viewModel.deleteCourseBaseBean(course.id, course.tableId)
            Toasty.success(context!!.applicationContext, "删除成功").show()
            viewModel.getScheduleWidgetIds().observe(this, Observer { list ->
                list?.forEach {
                    appWidgetManager.notifyAppWidgetViewDataChanged(it, R.id.lv_schedule)
                }
                dismiss()
            })
            return@setOnLongClickListener true
        }
    }

    override fun dismiss() {
        super.dismiss()
        timer.cancel()
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: CourseBean) =
                CourseDetailFragment().apply {
                    course = arg
                }
    }
}
