package com.suda.yzune.wakeupschedule.schedule_manage


import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Parcel
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.Navigation
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import com.suda.yzune.wakeupschedule.schedule_settings.ScheduleSettingsActivity
import com.suda.yzune.wakeupschedule.widget.ModifyTableNameFragment
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.startActivity

class ScheduleManageFragment : Fragment() {

    private lateinit var viewModel: ScheduleManageViewModel
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleManageViewModel::class.java)
    }

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

    private fun initTableRecyclerView(fragmentView: View, rvTableList: RecyclerView, data: List<TableSelectBean>) {
        rvTableList.layoutManager = LinearLayoutManager(context)
        val adapter = TableListAdapter(R.layout.item_table_list, data)
        adapter.setOnItemClickListener { _, _, position ->
            val bundle = Bundle()
            bundle.putInt("position", position)
            Navigation.findNavController(fragmentView).navigate(R.id.scheduleManageFragment_to_courseManageFragment, bundle)
        }
        adapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_share -> {
                }
                R.id.ib_edit -> {
                    job = GlobalScope.launch(Dispatchers.Main) {
                        val task = async(Dispatchers.IO) {
                            viewModel.getTableById(data[position].id)
                        }
                        startActivity<ScheduleSettingsActivity>("tableData" to task.await())
                    }
                }
                R.id.ib_delete -> {
                    Toasty.info(activity!!.applicationContext, "长按删除课程表哦~").show()
                }
            }
        }
        adapter.setOnItemChildLongClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_delete -> {
                    job = GlobalScope.launch(Dispatchers.Main) {
                        async(Dispatchers.IO) {
                            viewModel.deleteTable(data[position].id)
                        }.await()
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
        val tvBtn = view.findViewById<TextView>(R.id.tv_add)
        tvBtn.text = "添加"
        tvBtn.setOnClickListener {
            ModifyTableNameFragment.newInstance(object : ModifyTableNameFragment.TableNameChangeListener {
                override fun writeToParcel(dest: Parcel?, flags: Int) {

                }

                override fun describeContents(): Int {
                    return 0
                }

                override fun onFinish(editText: EditText, dialog: Dialog) {
                    if (!editText.text.toString().isEmpty()) {
                        job = GlobalScope.launch(Dispatchers.Main) {
                            val task = async(Dispatchers.IO) {
                                try {
                                    viewModel.addBlankTable(editText.text.toString())
                                    "ok"
                                } catch (e: Exception) {
                                    e.toString()
                                }
                            }
                            val result = task.await()
                            if (result == "ok") {
                                Toasty.success(activity!!.applicationContext, "新建成功~").show()
                                dialog.dismiss()
                            } else {
                                Toasty.success(activity!!.applicationContext, "操作失败>_<").show()
                                dialog.dismiss()
                            }
                        }
                    } else {
                        Toasty.error(activity!!.applicationContext, "名称不能为空哦>_<").show()
                    }
                }
            }).show(fragmentManager, "addTableFragment")
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }
}
