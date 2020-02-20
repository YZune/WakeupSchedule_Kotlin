package com.suda.yzune.wakeupschedule.schedule_manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import com.suda.yzune.wakeupschedule.schedule_settings.ScheduleSettingsActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_list_manage.*
import splitties.activities.start
import splitties.dimensions.dip

class ScheduleManageFragment : BaseFragment() {

    private val viewModel by activityViewModels<ScheduleManageViewModel>()
    private lateinit var adapter: TableListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list_manage, container, false)
        val rvTableList = view.findViewById<RecyclerView>(R.id.rv_list)
        launch {
            initTableRecyclerView(view, rvTableList, viewModel.initTableSelectList())
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_add.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.setting_schedule_name)
                    .setView(R.layout.dialog_edit_text)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.sure, null)
                    .create()
            dialog.show()
            val inputLayout = dialog.findViewById<TextInputLayout>(R.id.text_input_layout)
            val editText = dialog.findViewById<TextInputEditText>(R.id.edit_text)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val value = editText?.text
                if (value.isNullOrBlank()) {
                    inputLayout?.error = "名称不能为空哦>_<"
                } else {
                    launch {
                        try {
                            val tableName = editText.text.toString()
                            val tableId = viewModel.addBlankTable(tableName)
                            adapter.addData(TableSelectBean(id = tableId.toInt(), tableName = tableName))
                            Toasty.success(context!!, "新建成功~").show()
                        } catch (e: Exception) {
                            Toasty.error(context!!, "操作失败>_<").show()
                        }
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    private fun initTableRecyclerView(fragmentView: View, rvTableList: RecyclerView, data: MutableList<TableSelectBean>) {
        rvTableList.layoutManager = LinearLayoutManager(context)
        adapter = TableListAdapter(R.layout.item_table_list, data)
        adapter.setOnItemClickListener { _, _, position ->
            val bundle = Bundle()
            bundle.putParcelable("selectedTable", data[position])
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
                        adapter.remove(position)
                        Toasty.success(context!!, "删除成功~").show()
                    }
                    return@setOnItemChildLongClickListener true
                }
                else -> {
                    return@setOnItemChildLongClickListener false
                }
            }

        }
        adapter.addFooterView(View(activity).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(240))
        })
        adapter.addHeaderView(AppCompatTextView(context!!).apply {
            text = "点击卡片查看该课表的课程"
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setPadding(0, dip(8), 0, dip(8))
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        })
        rvTableList.adapter = adapter
    }

}
