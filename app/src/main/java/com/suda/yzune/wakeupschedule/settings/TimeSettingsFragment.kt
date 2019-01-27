package com.suda.yzune.wakeupschedule.settings


import android.app.Dialog
import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.widget.ModifyTableNameFragment
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimeSettingsFragment : BaseFragment() {

    var position = 0
    private lateinit var viewModel: TimeSettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        position = arguments!!.getInt("position")
        viewModel = ViewModelProviders.of(activity!!).get(TimeSettingsViewModel::class.java)
        if (viewModel.timeSelectList.isEmpty()) {
            viewModel.initTimeSelectList()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_time_settings, container, false)
        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_time_detail)
        initAdapter(recyclerView)
        viewModel.getTimeData(viewModel.timeTableList[position].id).observe(this, Observer {
            if (it == null) return@Observer
            if (it.isEmpty()) {
                launch {
                    withContext(Dispatchers.IO) {
                        viewModel.initTimeTableData(viewModel.timeTableList[position].id)
                    }
                }
            } else {
                viewModel.timeList.clear()
                viewModel.timeList.addAll(it)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        })
        return view
    }

    private fun initAdapter(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        val adapter = TimeSettingsAdapter(R.layout.item_time_detail, viewModel.timeList)
        adapter.setOnItemClickListener { _, _, position ->
            val selectTimeDialog = SelectTimeDetailFragment.newInstance(this.position, position)
            selectTimeDialog.setListener(object : SelectTimeDetailFragment.DialogResultListener {
                override fun refreshTimeResult() {
                    adapter.notifyDataSetChanged()
                }
            })
            selectTimeDialog.isCancelable = false
            selectTimeDialog.show(fragmentManager!!, "selectTimeDetail")
        }
        adapter.setHeaderView(initHeaderView(adapter))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
    }

    private fun initHeaderView(adapter: TimeSettingsAdapter): View {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_time_detail_header, null)
        val llLength = view.findViewById<LinearLayout>(R.id.ll_set_length)
        val switch = view.findViewById<Switch>(R.id.s_time_same)
        if (viewModel.timeTableList[position].sameLen) {
            llLength.visibility = View.VISIBLE
        } else {
            llLength.visibility = View.GONE
        }
        switch.isChecked = viewModel.timeTableList[position].sameLen
        switch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.timeTableList[position].sameLen = isChecked
            if (isChecked) {
                llLength.visibility = View.VISIBLE
            } else {
                llLength.visibility = View.GONE
            }
        }

        val tvTimeLen = view.findViewById<TextView>(R.id.tv_time_length)
        val seekBar = view.findViewById<SeekBar>(R.id.sb_time_length)
        seekBar.progress = viewModel.timeTableList[position].courseLen - 30
        tvTimeLen.text = viewModel.timeTableList[position].courseLen.toString()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvTimeLen.text = "${progress + 30}"
                viewModel.refreshEndTime(progress + 30)
                viewModel.timeTableList[position].courseLen = progress + 30
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                adapter.notifyDataSetChanged()
            }

        })

        val tvName = view.findViewById<TextView>(R.id.tv_table_name)
        val llName = view.findViewById<LinearLayout>(R.id.ll_table_name)
        tvName.text = viewModel.timeTableList[position].name
        llName.setOnClickListener {
            if (viewModel.timeTableList[position].id == 1) {
                Toasty.error(activity!!.applicationContext, "默认时间表不能改名呢>_<").show()
                return@setOnClickListener
            }
            ModifyTableNameFragment.newInstance(changeListener = object : ModifyTableNameFragment.TableNameChangeListener {
                override fun writeToParcel(dest: Parcel?, flags: Int) {

                }

                override fun describeContents(): Int {
                    return 0
                }

                override fun onFinish(editText: EditText, dialog: Dialog) {
                    if (!editText.text.toString().isEmpty()) {
                        tvName.text = editText.text.toString()
                        viewModel.timeTableList[position].name = editText.text.toString()
                        dialog.dismiss()
                    } else {
                        Toasty.error(activity!!.applicationContext, "名称不能为空哦>_<").show()
                    }
                }

            },
                    titleStr = "时间表名字",
                    string = viewModel.timeTableList[position].name).show(fragmentManager!!, "timeTableNameDialog")
        }
        return view
    }
}
