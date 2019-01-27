package com.suda.yzune.wakeupschedule.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import kotlinx.android.synthetic.main.fragment_select_time_detail.*

class SelectTimeDetailFragment : BaseDialogFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_select_time_detail

    var position = -1
    var tablePosition = 0
    private lateinit var viewModel: TimeSettingsViewModel
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
        viewModel = ViewModelProviders.of(activity!!).get(TimeSettingsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wp_start.data = viewModel.timeSelectList
        wp_end.data = viewModel.timeSelectList
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
        if (startIndex == -1) {
            startIndex = 0
        }
        if (endIndex == -1) {
            endIndex = 0
        }
        wp_start.selectedItemPosition = startIndex
        wp_end.selectedItemPosition = endIndex

        wp_start.setOnItemSelectedListener { _, _, position ->
            startIndex = position
            if (endIndex < startIndex) {
                wp_end.selectedItemPosition = startIndex
                endIndex = startIndex
            }
        }
        wp_end.setOnItemSelectedListener { _, _, position ->
            endIndex = position
            if (endIndex < startIndex) {
                wp_end.selectedItemPosition = startIndex
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
