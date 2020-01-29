package com.suda.yzune.wakeupschedule.schedule_manage

import android.app.Dialog
import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import com.suda.yzune.wakeupschedule.schedule_settings.ScheduleSettingsActivity
import com.suda.yzune.wakeupschedule.widget.ModifyTableNameFragment
import es.dmoral.toasty.Toasty
import splitties.activities.start

class ScheduleManageFragment : BaseFragment() {

    private val viewModel by activityViewModels<ScheduleManageViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_manage, container, false)
        val rvTableList = view.findViewById<RecyclerView>(R.id.rv_table_list)
        viewModel.initTableSelectList().observe(this, Observer {
            if (it == null) return@Observer
            viewModel.tableSelectList.clear()
            viewModel.tableSelectList.addAll(it)
            if (rvTableList.adapter == null) {
                initTableRecyclerView(view, rvTableList, viewModel.tableSelectList)
            } else {
                rvTableList.adapter?.notifyDataSetChanged()
            }
        })
        return view
    }

    private fun initTableRecyclerView(fragmentView: View, rvTableList: RecyclerView, data: MutableList<TableSelectBean>) {
        rvTableList.layoutManager = LinearLayoutManager(context)
        val adapter = TableListAdapter(R.layout.item_table_list, data)
        adapter.setOnItemClickListener { _, _, position ->
            val bundle = Bundle()
            bundle.putInt("position", position)
            Navigation.findNavController(fragmentView).navigate(R.id.scheduleManageFragment_to_courseManageFragment, bundle)
        }
        adapter.addChildClickViewIds(R.id.ib_share, R.id.ib_edit, R.id.ib_delete)
        adapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_share -> {
                }
                R.id.ib_edit -> {
                    launch {
                        val task = viewModel.getTableById(data[position].id)
                        if (task != null) {
                            activity!!.start<ScheduleSettingsActivity> {
                                putExtra("tableData", task)
                            }
                        } else {
                            Toasty.error(context!!.applicationContext, "读取课表异常>_<")
                        }
                    }
                }
                R.id.ib_delete -> {
                    Toasty.info(activity!!.applicationContext, "长按删除课程表哦~").show()
                }
            }
        }
        adapter.addChildLongClickViewIds(R.id.ib_delete)
        adapter.setOnItemChildLongClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_delete -> {
                    launch {
                        viewModel.deleteTable(data[position].id)
                    }
                    return@setOnItemChildLongClickListener true
                }
                else -> {
                    return@setOnItemChildLongClickListener false
                }
            }

        }
        adapter.addFooterView(initFooterView())
        rvTableList.adapter = adapter
    }

    private fun initFooterView(): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_add_course_btn, null)
        val tvBtn = view.findViewById<AppCompatTextView>(R.id.tv_add)
        tvBtn.text = "添加"
        tvBtn.setOnClickListener {
            ModifyTableNameFragment.newInstance(object : ModifyTableNameFragment.TableNameChangeListener {
                override fun writeToParcel(dest: Parcel?, flags: Int) {

                }

                override fun describeContents(): Int {
                    return 0
                }

                override fun onFinish(editText: AppCompatEditText, dialog: Dialog) {
                    if (editText.text.toString().isNotEmpty()) {
                        launch {
                            try {
                                viewModel.addBlankTable(editText.text.toString())
                                Toasty.success(activity!!.applicationContext, "新建成功~").show()
                            } catch (e: Exception) {
                                Toasty.success(activity!!.applicationContext, "操作失败>_<").show()
                            }
                            dialog.dismiss()
                        }
                    } else {
                        Toasty.error(activity!!.applicationContext, "名称不能为空哦>_<").show()
                    }
                }
            }).show(parentFragmentManager, "addTableFragment")
        }
        return view
    }
}
