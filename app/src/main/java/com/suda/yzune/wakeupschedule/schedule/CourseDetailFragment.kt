package com.suda.yzune.wakeupschedule.schedule


import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import kotlinx.android.synthetic.main.fragment_course_detail.*

class CourseDetailFragment : DialogFragment() {

    lateinit var course: CourseBean
    lateinit var title: TextView
    lateinit var weeks: TextView
    lateinit var time: TextView
    lateinit var teacher: TextView
    lateinit var room: TextView
    lateinit var close: ImageButton

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

    private fun initEvent(){
        close.setOnClickListener {
            dismiss()
        }

        ib_edit.setOnClickListener {
            dismiss()
            val intent = Intent(activity, AddCourseActivity::class.java)
            intent.putExtra("id", course.id)
            startActivity(intent)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(arg: CourseBean) =
                CourseDetailFragment().apply {
                    course = arg
                }
    }
}
