package com.suda.yzune.wakeupschedule.course_add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import kotlinx.android.synthetic.main.fragment_add_course_tip.*
import splitties.fragments.start

class AddCourseTipFragment : BaseDialogFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_add_course_tip

    private lateinit var viewModel: AddCourseViewModel
    private lateinit var course: CourseBaseBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            course = it.getParcelable("course")!!
        }
        viewModel = ViewModelProviders.of(activity!!).get(AddCourseViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ib_close.setOnClickListener {
            dismiss()
        }

        tv_modify.setOnClickListener {
            dismiss()
            start<AddCourseActivity> {
                putExtra("id", course.id)
                putExtra("tableId", course.tableId)
                putExtra("maxWeek", viewModel.maxWeek)
                putExtra("nodes", viewModel.nodes)
                putExtra("showTip", true)
            }
            activity!!.finish()
        }

        tv_all_course.setOnClickListener {
            //todo:
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: CourseBaseBean) =
                AddCourseTipFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("course", arg)
                    }
                }
    }
}
