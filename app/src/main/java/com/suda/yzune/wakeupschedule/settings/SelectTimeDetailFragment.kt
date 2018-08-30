package com.suda.yzune.wakeupschedule.settings

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import kotlinx.android.synthetic.main.fragment_select_time_detail.*


class SelectTimeDetailFragment : DialogFragment() {

    var position = -1
    var isSummer = false
    private lateinit var viewModel: TimeSettingsViewModel
    private lateinit var adapter: TimeSettingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        wp_start.data = viewModel.getTimeSelectList()
        wp_end.data = viewModel.getTimeSelectList()
        if (PreferenceUtils.getBooleanFromSP(context!!.applicationContext, "s_time_same", true)) {
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
        if (!isSummer) {
            startIndex = viewModel.getTimeSelectList().indexOf(viewModel.getTimeList()[position].startTime)
            endIndex = viewModel.getTimeSelectList().indexOf(viewModel.getTimeList()[position].endTime)
        } else {
            startIndex = viewModel.getTimeSelectList().indexOf(viewModel.getSummerTimeList()[position].startTime)
            endIndex = viewModel.getTimeSelectList().indexOf(viewModel.getSummerTimeList()[position].endTime)
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
            if (!isSummer) {
                val startStr = viewModel.getTimeSelectList()[startIndex]
                viewModel.getTimeList()[position].startTime = startStr
                if (PreferenceUtils.getBooleanFromSP(context!!.applicationContext, "s_time_same", true)) {
                    viewModel.getTimeList()[position].endTime = CourseUtils.calAfterTime(startStr, PreferenceUtils.getIntFromSP(context!!.applicationContext, "classLen", 50))
                } else {
                    viewModel.getTimeList()[position].endTime = viewModel.getTimeSelectList()[endIndex]
                }
                adapter.notifyDataSetChanged()
                dismiss()
            } else {
                val startStr = viewModel.getTimeSelectList()[startIndex]
                viewModel.getSummerTimeList()[position].startTime = startStr
                if (PreferenceUtils.getBooleanFromSP(context!!.applicationContext, "s_time_same", true)) {
                    viewModel.getSummerTimeList()[position].endTime = CourseUtils.calAfterTime(startStr, PreferenceUtils.getIntFromSP(context!!.applicationContext, "classLen", 50))
                } else {
                    viewModel.getSummerTimeList()[position].endTime = viewModel.getTimeSelectList()[endIndex]
                }
                adapter.notifyDataSetChanged()
                dismiss()
            }
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg1: Int, arg2: TimeSettingsAdapter, arg3: Boolean) =
                SelectTimeDetailFragment().apply {
                    position = arg1
                    adapter = arg2
                    isSummer = arg3
                }
    }
}
