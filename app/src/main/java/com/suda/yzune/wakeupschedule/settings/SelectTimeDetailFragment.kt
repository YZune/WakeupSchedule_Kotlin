package com.suda.yzune.wakeupschedule.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import kotlinx.android.synthetic.main.fragment_select_time_detail.*


class SelectTimeDetailFragment : DialogFragment() {

    var position = -1
    var tablePosition = 0
    private lateinit var viewModel: TimeSettingsViewModel
    private lateinit var adapter: TimeSettingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
            tablePosition = it.getInt("tablePosition")
            adapter = it.getParcelable("adapter")!!
        }
        viewModel = ViewModelProviders.of(activity!!).get(TimeSettingsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_select_time_detail, container, false)
    }

    override fun onResume() {
        super.onResume()
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
            adapter.notifyDataSetChanged()
            dismiss()
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg0: Int, arg1: Int, arg2: TimeSettingsAdapter) =
                SelectTimeDetailFragment().apply {
                    arguments = Bundle().apply {
                        putInt("position", arg1)
                        putInt("tablePosition", arg0)
                        putParcelable("adapter", arg2)
                    }
                }
    }
}
