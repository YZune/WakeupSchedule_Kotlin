package com.suda.yzune.wakeupschedule.course_add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.activityViewModels
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.bean.TimeBean
import kotlinx.android.synthetic.main.fragment_select_time.*

class SelectTimeFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_select_time

    var position = -1
    private val dayList = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    private val nodeList = arrayOfNulls<String>(30)
    private val viewModel by activityViewModels<AddCourseViewModel>()
    private lateinit var course: CourseEditBean
    var day = 1
    var start = 1
    var end = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNodeList()
        wp_day.displayedValues = dayList
        wp_start.displayedValues = nodeList
        wp_end.displayedValues = nodeList
        course = viewModel.editList[position]
        day = course.time.value!!.day
        start = if (course.time.value!!.startNode > viewModel.nodes) viewModel.nodes else course.time.value!!.startNode
        end = if (course.time.value!!.endNode > viewModel.nodes) viewModel.nodes else course.time.value!!.endNode
        if (start < 1) start = 1
        if (end < 1) end = 1
        initEvent()
    }

    private fun initNodeList() {
        for (i in 1..30) {
            nodeList[i - 1] = "第 $i 节"
        }
    }

    private fun initEvent() {
        wp_day.minValue = 0
        wp_day.maxValue = dayList.size - 1
        if (day < 1) day = 1
        if (day > 7) day = 7
        wp_day.value = day - 1

        wp_start.minValue = 0
        wp_start.maxValue = viewModel.nodes - 1
        if (start < 1) start = 1
        wp_start.value = start - 1

        wp_end.minValue = 0
        wp_end.maxValue = viewModel.nodes - 1
        if (start < 1) start = 1
        wp_end.value = end - 1

        wp_day.setOnValueChangedListener { _, _, newVal ->
            day = newVal + 1
        }

        wp_start.setOnValueChangedListener { _, _, newVal ->
            start = newVal + 1
            if (end < start) {
                wp_end.smoothScrollToValue(start - 1, false)
                end = start
            }
        }

        wp_end.setOnValueChangedListener { _, _, newVal ->
            end = newVal + 1
            if (end < start) {
                wp_end.smoothScrollToValue(start - 1, false)
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
