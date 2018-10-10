package com.suda.yzune.wakeupschedule.schedule_manage


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import com.suda.yzune.wakeupschedule.schedule_settings.ScheduleSettingsActivity
import es.dmoral.toasty.Toasty

class ScheduleManageFragment : Fragment() {

    private lateinit var viewModel: ScheduleManageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleManageViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_manage, container, false)
        val rvTableList = view.findViewById<RecyclerView>(R.id.rv_table_list)
        val gson = Gson()
        viewModel.initTableSelectList().observe(this, Observer {
            if (it == null) return@Observer
            viewModel.tableSelectList.clear()
            viewModel.tableSelectList.addAll(it)
            if (rvTableList.adapter == null) {
                initTableRecyclerView(rvTableList, viewModel.tableSelectList)
            } else {
                rvTableList.adapter?.notifyDataSetChanged()
            }
        })
        viewModel.editTableLiveData.observe(this, Observer {
            if (it == null) return@Observer
            startActivity(Intent(activity, ScheduleSettingsActivity::class.java).putExtra(
                    "tableData", gson.toJson(it)
            ))
        })
        return view
    }

    private fun initTableRecyclerView(rvTableList: RecyclerView, data: List<TableSelectBean>) {
        rvTableList.layoutManager = LinearLayoutManager(context)
        val adapter = TableListAdapter(R.layout.item_table_list, data)
        adapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_share -> {
                }
                R.id.ib_edit -> {
                    viewModel.getTableById(viewModel.tableSelectList[position].id)
                }
                R.id.ib_delete -> {
                    Toasty.info(activity!!.applicationContext, "长按删除课程表哦~").show()
                }
            }
        }
        adapter.setOnItemChildLongClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_delete -> {
                    viewModel.deleteTable(viewModel.tableSelectList[position].id)
                    return@setOnItemChildLongClickListener true
                }
                else -> {
                    return@setOnItemChildLongClickListener false
                }
            }

        }
        rvTableList.adapter = adapter
    }
}
