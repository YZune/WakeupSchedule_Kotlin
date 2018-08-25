package com.suda.yzune.wakeupschedule.settings

import android.appwidget.AppWidgetManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.course_add.SelectTimeFragment
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_time_settings.*

class TimeSettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: TimeSettingsViewModel
    private var isExit: Boolean = false
    private val tExit = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            isExit = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_settings)
        ViewUtils.resizeStatusBar(this, v_status)

        viewModel = ViewModelProviders.of(this).get(TimeSettingsViewModel::class.java)
        viewModel.initRepository(applicationContext)
        val nodesNum = PreferenceUtils.getIntFromSP(this.applicationContext, "classNum", 11)

        viewModel.getDetailData().observe(this, Observer {
            viewModel.getTimeList().clear()
            viewModel.getTimeList().addAll(it!!.subList(0, nodesNum))
            initAdapter(TimeSettingsAdapter(R.layout.item_time_detail, viewModel.getTimeList()))
        })

        viewModel.getSummerData().observe(this, Observer {
            viewModel.getSummerTimeList().clear()
            viewModel.getSummerTimeList().addAll(it!!.subList(0, nodesNum))
        })
        initEvent()
    }

    private fun initAdapter(adapter: TimeSettingsAdapter) {
        adapter.setOnItemClickListener { _, _, position ->
            val selectTimeDialog = SelectTimeDetailFragment.newInstance(position, adapter,PreferenceUtils.getBooleanFromSP(this.applicationContext, "s_summer", false))
            selectTimeDialog.isCancelable = false
            selectTimeDialog.show(supportFragmentManager, "selectTimeDetail")
        }
        adapter.addHeaderView(initHeaderView(adapter))
        rv_time_detail.adapter = adapter
        rv_time_detail.layoutManager = LinearLayoutManager(this)
    }

    private fun initHeaderView(adapter: TimeSettingsAdapter): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_time_detail_header, null)
        val sameSwitch = view.findViewById<Switch>(R.id.s_time_same)
        val summerSwitch = view.findViewById<Switch>(R.id.s_summer)
        val setLL = view.findViewById<LinearLayout>(R.id.ll_set_length)
        sameSwitch.isChecked = PreferenceUtils.getBooleanFromSP(this.applicationContext, "s_time_same", true)
        summerSwitch.isChecked = PreferenceUtils.getBooleanFromSP(this.applicationContext, "s_summer", false)
        if (sameSwitch.isChecked) {
            setLL.visibility = View.VISIBLE
        } else {
            setLL.visibility = View.GONE
        }
        if (summerSwitch.isChecked) {
            adapter.data.clear()
            adapter.data.addAll(viewModel.getSummerTimeList())
//            adapter.replaceData(viewModel.getSummerTimeList())
            adapter.notifyDataSetChanged()
        } else {
            adapter.data.clear()
            adapter.data.addAll(viewModel.getTimeList())
//            adapter.replaceData(viewModel.getTimeList())
            adapter.notifyDataSetChanged()
        }
        sameSwitch.setOnCheckedChangeListener { _, isChecked ->
                PreferenceUtils.saveBooleanToSP(this.applicationContext, "s_time_same", isChecked)
                if (isChecked) {
                    setLL.visibility = View.VISIBLE
                } else {
                    setLL.visibility = View.GONE
                }
        }
        summerSwitch.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(this.applicationContext, "s_summer", isChecked)
            if (isChecked) {
                adapter.data.clear()
                adapter.data.addAll(viewModel.getSummerTimeList())
//                adapter.replaceData(viewModel.getSummerTimeList())
                adapter.notifyDataSetChanged()
            } else {
                adapter.data.clear()
                adapter.data.addAll(viewModel.getTimeList())
//                adapter.replaceData(viewModel.getTimeList())
                adapter.notifyDataSetChanged()
            }
        }

        val seekBar = view.findViewById<SeekBar>(R.id.sb_time_length)
        val textView = view.findViewById<TextView>(R.id.tv_time_length)
        val min = PreferenceUtils.getIntFromSP(this.applicationContext, "classLen", 50)
        seekBar.progress = min - 30
        textView.text = min.toString()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.text = "${progress + 30}"
                PreferenceUtils.saveIntToSP(this@TimeSettingsActivity.applicationContext, "classLen", progress + 30)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        return view
    }

    private fun saveData() {
        viewModel.saveData()
        viewModel.getSaveInfo().observe(this, Observer { s ->
            when (s) {
                "ok" -> {
                    Toasty.success(this.applicationContext, "保存成功").show()
                    finish()
                }
                else -> {
                    Toasty.error(this.applicationContext, s!!, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun initEvent() {
        tv_cancel.setOnClickListener {
            finish()
        }

        tv_save.setOnClickListener { _ ->
            saveData()
        }
    }

    private fun exitBy2Click() {
        if (!isExit) {
            isExit = true // 准备退出
            Toasty.info(this.applicationContext, "再按一次退出编辑").show()
            tExit.start() // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click()  //退出应用的操作
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        tExit.cancel()
    }
}
