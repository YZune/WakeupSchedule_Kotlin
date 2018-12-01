package com.suda.yzune.wakeupschedule.settings

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Parcel
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.Navigation
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.widget.ModifyTableNameFragment
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.design.longSnackbar

class TimeTableFragment : BaseFragment() {

    private lateinit var viewModel: TimeSettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        viewModel = ViewModelProviders.of(activity!!).get(TimeSettingsViewModel::class.java)
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

        viewModel.deleteInfo.observe(this, Observer {
            if (it == null) return@Observer
            when (it) {
                "ok" -> Toasty.success(activity!!.applicationContext, "删除成功~").show()
                "error" -> Toasty.error(activity!!.applicationContext, "该时间表仍被使用中>_<请确保它不被使用再删除哦").show()
            }
        })
        return view
    }

    private fun initRecyclerView(recyclerView: RecyclerView, fragmentView: View) {
        val adapter = TimeTableAdapter(R.layout.item_time_table, viewModel.timeTableList, viewModel.selectedId)
        adapter.bindToRecyclerView(recyclerView)
        adapter.addFooterView(initFooterView())
        adapter.setOnItemClickListener { _, _, position ->
            adapter.selectedId = viewModel.timeTableList[position].id
            viewModel.selectedId = viewModel.timeTableList[position].id
            adapter.notifyDataSetChanged()
        }
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
        adapter.setOnItemChildLongClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_delete -> {
                    if (viewModel.timeTableList[position].id == viewModel.selectedId) {
                        view.longSnackbar("不能删除已选中的时间表哦>_<")
                    } else {
                        launch {
                            val task = async(Dispatchers.IO) {
                                try {
                                    viewModel.deleteTimeTable(viewModel.timeTableList[position])
                                    "ok"
                                } catch (e: Exception) {
                                    "删除错误>_<${e.message}"
                                }
                            }.await()
                            if (task == "ok") {
                                view.longSnackbar("删除成功~")
                            } else {
                                view.longSnackbar("该时间表仍被使用中>_<请确保它不被使用再删除哦")
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
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private fun initFooterView(): View {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_add_course_btn, null)
        val tvBtn = view.findViewById<TextView>(R.id.tv_add)
        tvBtn.text = "新建时间表"
        tvBtn.setOnClickListener {
            ModifyTableNameFragment.newInstance(changeListener = object : ModifyTableNameFragment.TableNameChangeListener {
                override fun writeToParcel(dest: Parcel?, flags: Int) {

                }

                override fun describeContents(): Int {
                    return 0
                }

                override fun onFinish(editText: EditText, dialog: Dialog) {
                    if (!editText.text.toString().isEmpty()) {
                        launch {
                            val task = async(Dispatchers.IO) {
                                try {
                                    viewModel.addNewTimeTable(editText.text.toString())
                                    "ok"
                                } catch (e: Exception) {
                                    "发生异常>_<${e.message}"
                                }
                            }.await()
                            if (task == "ok") {
                                Toasty.success(activity!!.applicationContext, "新建成功~").show()
                                dialog.dismiss()
                            } else {
                                Toasty.error(activity!!.applicationContext, task).show()
                            }
                        }
                    } else {
                        Toasty.error(activity!!.applicationContext, "名称不能为空哦>_<").show()
                    }
                }

            },
                    titleStr = "时间表名字").show(fragmentManager, "timeTableNameDialog")
        }
        return view
    }

}
