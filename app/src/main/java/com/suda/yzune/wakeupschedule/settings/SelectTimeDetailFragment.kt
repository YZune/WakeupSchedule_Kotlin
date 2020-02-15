package com.suda.yzune.wakeupschedule.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.activityViewModels
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import kotlinx.android.synthetic.main.fragment_select_time_detail.*

class SelectTimeDetailFragment : BaseDialogFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_select_time_detail

    var position = -1
    var tablePosition = 0
    private val viewModel by activityViewModels<TimeSettingsViewModel>()
    private var mListener: DialogResultListener? = null

    fun setListener(listener: DialogResultListener) {
        mListener = listener
    }

    interface DialogResultListener {
        fun refreshTimeResult()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
            tablePosition = it.getInt("tablePosition")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val valueArray = viewModel.timeSelectList.toTypedArray()

        wp_start.displayedValues = valueArray
        wp_start.minValue = 0
        wp_start.maxValue = valueArray.size - 1

        wp_end.displayedValues = valueArray
        wp_end.minValue = 0
        wp_end.maxValue = valueArray.size - 1

        if (viewModel.timeTableList[tablePosition].sameLen) {
            tv_title.text = "请选择开始时间"
            wp_end.visibility = View.GONE
        } else {
            tv_title.text = "请选择时间"
            wp_end.visibility = View.VISIBLE
        }
        initEvent()
    }

    private fun initEvent() {
        var startIndex: Int
        var endIndex: Int
        startIndex = viewModel.timeSelectList.indexOf(viewModel.timeList[position].startTime)
        endIndex = viewModel.timeSelectList.indexOf(viewModel.timeList[position].endTime)
        if (startIndex < 0) {
            startIndex = 0
        }
        if (endIndex < 0) {
            endIndex = 0
        }

        wp_start.value = startIndex
        wp_end.value = endIndex

        wp_start.setOnValueChangedListener { _, _, newVal ->
            startIndex = newVal
            if (endIndex < startIndex) {
                wp_end.smoothScrollToValue(startIndex, false)
                endIndex = startIndex
            }
        }
        wp_end.setOnValueChangedListener { _, _, newVal ->
            endIndex = newVal
            if (endIndex < startIndex) {
                wp_end.smoothScrollToValue(startIndex, false)
                endIndex = startIndex
            }
        }

        btn_save.setOnClickListener {
            val startStr = viewModel.timeSelectList[startIndex]
            viewModel.timeList[position].startTime = startStr
            if (viewModel.timeTableList[tablePosition].sameLen) {
                viewModel.timeList[position].endTime = CourseUtils.calAfterTime(startStr, viewModel.timeTableList[tablePosition].courseLen)
            } else {
                viewModel.timeList[position].endTime = viewModel.timeSelectList[endIndex]
            }
            mListener?.refreshTimeResult()
            dismiss()
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg0: Int, arg1: Int) =
                SelectTimeDetailFragment().apply {
                    arguments = Bundle().apply {
                        putInt("position", arg1)
                        putInt("tablePosition", arg0)
                    }
                }
    }
}
