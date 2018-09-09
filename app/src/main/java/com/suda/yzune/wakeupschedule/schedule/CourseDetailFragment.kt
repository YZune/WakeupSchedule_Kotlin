package com.suda.yzune.wakeupschedule.schedule


import android.app.Service
import android.appwidget.AppWidgetManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
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
    private val scheduleWidgetIds = arrayListOf<Int>()
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
        viewModel.getScheduleWidgetIds().observe(this, Observer {
            scheduleWidgetIds.clear()
            scheduleWidgetIds.addAll(it!!)
        })
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

        title.setTextColor(resources.getColor(R.color.black))
        title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        weeks.setTextColor(resources.getColor(R.color.black))
        time.setTextColor(resources.getColor(R.color.black))
        teacher.setTextColor(resources.getColor(R.color.black))
        room.setTextColor(resources.getColor(R.color.black))

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
        if (PreferenceUtils.getBooleanFromSP(context!!.applicationContext, "isInitTimeTable", false)) {
            if (viewModel.showSummerTime) {
                time.text = "第${course.startNode} - ${course.startNode + course.step - 1}节    ${viewModel.summerTimeList[course.startNode - 1].startTime} - ${viewModel.summerTimeList[course.startNode + course.step - 2].endTime}"
            } else {
                time.text = "第${course.startNode} - ${course.startNode + course.step - 1}节    ${viewModel.timeList[course.startNode - 1].startTime} - ${viewModel.timeList[course.startNode + course.step - 2].endTime}"
            }
        } else {
            time.text = "第${course.startNode} - ${course.startNode + course.step - 1}节"
        }
    }

    private fun initEvent() {
        val mVibrator = context!!.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        close.setOnClickListener {
            dismiss()
        }

        ib_edit.setOnClickListener {
            dismiss()
            val intent = Intent(activity, AddCourseActivity::class.java)
            intent.putExtra("id", course.id)
            intent.putExtra("tableName", course.tableName)
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
                val appWidgetManager = AppWidgetManager.getInstance(context!!.applicationContext)
                scheduleWidgetIds.forEach {
                    AppWidgetUtils.refreshScheduleWidget(context!!.applicationContext, appWidgetManager, it)
                }
                dismiss()
            }
        }

        ib_delete_course.setOnLongClickListener { _ ->
            mVibrator.vibrate(100)
            viewModel.deleteCourseBaseBean(course)
            Toasty.success(context!!.applicationContext, "删除成功").show()
            val appWidgetManager = AppWidgetManager.getInstance(context!!.applicationContext)
            scheduleWidgetIds.forEach {
                AppWidgetUtils.refreshScheduleWidget(context!!.applicationContext, appWidgetManager, it)
            }
            dismiss()
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
