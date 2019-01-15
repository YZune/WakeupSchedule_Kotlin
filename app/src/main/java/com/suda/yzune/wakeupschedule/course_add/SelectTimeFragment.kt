package com.suda.yzune.wakeupschedule.course_add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.bean.TimeBean
import kotlinx.android.synthetic.main.fragment_select_time.*

class SelectTimeFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_select_time

    var position = -1
    private val dayList = listOf<String>("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    private val nodeList = arrayListOf<String>()
    private lateinit var viewModel: AddCourseViewModel
    private lateinit var course: CourseEditBean
    var day = 1
    var start = 1
    var end = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
        viewModel = ViewModelProviders.of(activity!!).get(AddCourseViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNodeList(viewModel.nodes)
        wp_day.data = dayList
        wp_start.data = nodeList
        wp_end.data = nodeList
        course = viewModel.editList[position]
        day = course.time.value!!.day
        start = if (course.time.value!!.startNode > viewModel.nodes) viewModel.nodes else course.time.value!!.startNode
        end = if (course.time.value!!.endNode > viewModel.nodes) viewModel.nodes else course.time.value!!.endNode
        initEvent()
    }

    private fun initNodeList(max: Int) {
        for (i in 1..max) {
            nodeList.add("第 $i 节")
        }
    }

    private fun initEvent() {
        wp_day.selectedItemPosition = day - 1
        wp_start.selectedItemPosition = start - 1
        wp_end.selectedItemPosition = end - 1

        wp_day.setOnItemSelectedListener { _, _, position ->
            day = position + 1
        }
        wp_start.setOnItemSelectedListener { _, _, position ->
            start = position + 1
            if (end < start) {
                wp_end.selectedItemPosition = start - 1
                end = start
            }
        }
        wp_end.setOnItemSelectedListener { _, _, position ->
            end = position + 1
            if (end < start) {
                wp_end.selectedItemPosition = start - 1
                end = start
            }
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }

        btn_save.setOnClickListener {
            val result = TimeBean(day, start, end)
            viewModel.editList[position].time.value = result
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: Int) =
                SelectTimeFragment().apply {
                    arguments = Bundle().apply {
                        putInt("position", arg)
                    }
                }
    }
}
