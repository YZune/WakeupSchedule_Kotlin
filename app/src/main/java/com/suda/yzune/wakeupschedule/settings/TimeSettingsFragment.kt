package com.suda.yzune.wakeupschedule.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import es.dmoral.toasty.Toasty

class TimeSettingsFragment : BaseFragment() {

    var position = 0
    private val viewModel by activityViewModels<TimeSettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        position = arguments!!.getInt("position")
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
                launch {
                    viewModel.initTimeTableData(viewModel.timeTableList[position].id)
                }
            } else {
                viewModel.timeList.clear()
                viewModel.timeList.addAll(it)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        })
        return view
    }

    private fun initAdapter(recyclerView: RecyclerView) {
        val adapter = TimeSettingsAdapter(R.layout.item_time_detail, viewModel.timeList)
        adapter.setOnItemClickListener { _, _, position ->
            val selectTimeDialog = SelectTimeDetailFragment.newInstance(this.position, position)
            selectTimeDialog.setListener(object : SelectTimeDetailFragment.DialogResultListener {
                override fun refreshTimeResult() {
                    adapter.notifyDataSetChanged()
                }
            })
            selectTimeDialog.isCancelable = false
            selectTimeDialog.show(parentFragmentManager, "selectTimeDetail")
        }
        adapter.setHeaderView(initHeaderView(adapter))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun initHeaderView(adapter: TimeSettingsAdapter): View {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_time_detail_header, null)
        val llLength = view.findViewById<LinearLayoutCompat>(R.id.ll_set_length)
        val switch = view.findViewById<SwitchCompat>(R.id.s_time_same)
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

        val tvTimeLen = view.findViewById<AppCompatTextView>(R.id.tv_time_length)
        val seekBar = view.findViewById<AppCompatSeekBar>(R.id.sb_time_length)
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

        val tvName = view.findViewById<AppCompatTextView>(R.id.tv_table_name)
        val llName = view.findViewById<LinearLayoutCompat>(R.id.ll_table_name)
        tvName.text = viewModel.timeTableList[position].name
        llName.setOnClickListener {
            if (viewModel.timeTableList[position].id == 1) {
                Toasty.error(activity!!, "默认时间表不能改名呢>_<").show()
                return@setOnClickListener
            }
            val dialog = MaterialAlertDialogBuilder(context)
                    .setTitle("时间表名字")
                    .setView(R.layout.dialog_edit_text)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.sure, null)
                    .create()
            dialog.show()
            val inputLayout = dialog.findViewById<TextInputLayout>(R.id.text_input_layout)
            val editText = dialog.findViewById<TextInputEditText>(R.id.edit_text)
            editText?.setText(viewModel.timeTableList[position].name)
            editText?.setSelection(viewModel.timeTableList[position].name.length)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val value = editText?.text
                if (value.isNullOrBlank()) {
                    inputLayout?.error = "名称不能为空哦>_<"
                } else {
                    tvName.text = editText.text.toString()
                    viewModel.timeTableList[position].name = editText.text.toString()
                    dialog.dismiss()
                }
            }
        }
        return view
    }
}
