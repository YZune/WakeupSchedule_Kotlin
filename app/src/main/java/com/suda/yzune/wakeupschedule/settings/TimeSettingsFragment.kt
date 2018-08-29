package com.suda.yzune.wakeupschedule.settings


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_time_settings.*

class TimeSettingsFragment : Fragment() {

    private var isSummer = false
    private var nodesNum = 11
    private lateinit var viewModel: TimeSettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewModel = ViewModelProviders.of(activity!!).get(TimeSettingsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_settings, container, false)
    }

    override fun onResume() {
        super.onResume()

        if (isSummer) {
            viewModel.getSummerData().observe(this, Observer {
                viewModel.getSummerTimeList().clear()
                Log.d("显示", it!!.toString())
                viewModel.getSummerTimeList().addAll(it.subList(0, nodesNum))
                initAdapter(TimeSettingsAdapter(R.layout.item_time_detail, viewModel.getSummerTimeList()))
            })
        } else {
            viewModel.getDetailData().observe(this, Observer {
                viewModel.getTimeList().clear()
                viewModel.getTimeList().addAll(it!!.subList(0, nodesNum))
                initAdapter(TimeSettingsAdapter(R.layout.item_time_detail, viewModel.getTimeList()))
            })
        }
    }

    private fun initAdapter(adapter: TimeSettingsAdapter) {
        adapter.setOnItemClickListener { _, _, position ->
            val selectTimeDialog = SelectTimeDetailFragment.newInstance(position, adapter, isSummer)
            selectTimeDialog.isCancelable = false
            selectTimeDialog.show(fragmentManager, "selectTimeDetail")
        }
        rv_time_detail.isNestedScrollingEnabled = false
        rv_time_detail.adapter = adapter
        rv_time_detail.layoutManager = LinearLayoutManager(activity)
    }

    companion object {
        @JvmStatic
        fun newInstance(param0: Int, param1: Boolean) =
                TimeSettingsFragment().apply {
                    nodesNum = param0
                    isSummer = param1
                }
    }
}
