package com.suda.yzune.wakeupschedule.settings


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suda.yzune.wakeupschedule.R

class TimeSettingsFragment : Fragment() {

    private var position = 0
    private lateinit var viewModel: TimeSettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewModel = ViewModelProviders.of(activity!!).get(TimeSettingsViewModel::class.java)
        if (viewModel.timeSelectList.isEmpty()) {
            viewModel.initTimeSelectList()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_time_settings, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_time_detail)
        initAdapter(recyclerView)
        viewModel.getTimeData(viewModel.timeTableList[position].id).observe(this, Observer {
            if (it == null) return@Observer
            if (it.isEmpty()) {
                viewModel.initTimeTableData(viewModel.timeTableList[position].id)
            } else {
                viewModel.timeList.clear()
                viewModel.timeList.addAll(it)
                recyclerView.adapter.notifyDataSetChanged()
            }
        })
        return view
    }

    private fun initAdapter(recyclerView: RecyclerView) {
        val adapter = TimeSettingsAdapter(R.layout.item_time_detail, viewModel.timeList)
        adapter.setOnItemClickListener { _, _, position ->
            val selectTimeDialog = SelectTimeDetailFragment.newInstance(position, adapter)
            selectTimeDialog.isCancelable = false
            selectTimeDialog.show(fragmentManager, "selectTimeDetail")
        }
        //recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun initHeaderView() {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_add_course_btn, null)
    }

    companion object {
        @JvmStatic
        fun newInstance(param0: Int) =
                TimeSettingsFragment().apply {
                    position = param0
                }
    }
}
