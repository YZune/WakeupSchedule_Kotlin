package com.suda.yzune.wakeupschedule.schedule


import android.app.Service
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Vibrator
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login_web.*
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
        time.text = "第${course.startNode} - ${course.startNode + course.step - 1}节"
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
            startActivity(intent)
        }

        ib_delete_course.setOnClickListener {
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

        ib_delete_course.setOnLongClickListener {
            mVibrator.vibrate(100)
            viewModel.deleteCourseBaseBean(course)
            Toasty.success(context!!.applicationContext, "删除成功").show()
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
