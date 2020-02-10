package com.suda.yzune.wakeupschedule.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import es.dmoral.toasty.Toasty
import splitties.snackbar.longSnack

class TimeTableFragment : BaseFragment() {

    private val viewModel by activityViewModels<TimeSettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        viewModel.selectedId = arguments!!.getInt("selectedId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.time_table_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_time_table)
        initRecyclerView(recyclerView, view)

        viewModel.getTimeTableList().observe(this, Observer {
            if (it == null) return@Observer
            viewModel.timeTableList.clear()
            viewModel.timeTableList.addAll(it)
            recyclerView.adapter?.notifyDataSetChanged()
        })
        return view
    }

    private fun initRecyclerView(recyclerView: RecyclerView, fragmentView: View) {
        val adapter = TimeTableAdapter(R.layout.item_time_table, viewModel.timeTableList, viewModel.selectedId)
        recyclerView.adapter = adapter
        adapter.addFooterView(initFooterView())
        adapter.setOnItemClickListener { _, _, position ->
            adapter.selectedId = viewModel.timeTableList[position].id
            viewModel.selectedId = viewModel.timeTableList[position].id
            adapter.notifyDataSetChanged()
        }
        adapter.addChildClickViewIds(R.id.ib_edit, R.id.ib_delete)
        adapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_edit -> {
                    viewModel.entryPosition = position
                    val bundle = Bundle()
                    bundle.putInt("position", position)
                    Navigation.findNavController(fragmentView).navigate(R.id.timeTableFragment_to_timeSettingsFragment, bundle)
                }
                R.id.ib_delete -> {
                    Toasty.info(activity!!.applicationContext, "长按确认删除哦~").show()
                }
            }
        }
        adapter.addChildLongClickViewIds(R.id.ib_delete)
        adapter.setOnItemChildLongClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_delete -> {
                    if (viewModel.timeTableList[position].id == viewModel.selectedId) {
                        view.longSnack("不能删除已选中的时间表哦>_<")
                    } else {
                        launch {
                            try {
                                viewModel.deleteTimeTable(viewModel.timeTableList[position])
                                view.longSnack("删除成功~")
                            } catch (e: Exception) {
                                view.longSnack("该时间表仍被使用中>_<请确保它不被使用再删除哦")
                            }
                        }
                    }
                    return@setOnItemChildLongClickListener true
                }
                else -> {
                    true
                }
            }
        }
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private fun initFooterView(): View {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_add_course_btn, null)
        val tvBtn = view.findViewById<MaterialButton>(R.id.tv_add)
        tvBtn.text = "新建时间表"
        tvBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(context!!)
                    .setTitle("时间表名字")
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
                            viewModel.addNewTimeTable(editText.text.toString())
                            Toasty.success(activity!!.applicationContext, "新建成功~").show()
                        } catch (e: Exception) {
                            Toasty.error(activity!!.applicationContext, "发生异常>_<${e.message}").show()
                        }
                        dialog.dismiss()
                    }
                }
            }
        }
        return view
    }

}
