package com.suda.yzune.wakeupschedule.settings

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.SizeUtils
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

        initView(nodesNum)
        initEvent()
    }

    private fun initView(nodesNum: Int) {
        s_time_same.isChecked = PreferenceUtils.getBooleanFromSP(this.applicationContext, "s_time_same", true)
        s_summer.isChecked = PreferenceUtils.getBooleanFromSP(this.applicationContext, "s_summer", false)

        if (s_time_same.isChecked) {
            ll_set_length.visibility = View.VISIBLE
        } else {
            ll_set_length.visibility = View.GONE
        }

        val min = PreferenceUtils.getIntFromSP(this.applicationContext, "classLen", 50)
        sb_time_length.progress = min - 30
        tv_time_length.text = min.toString()

        vp_time_list.adapter = TimeListTabAdapter(supportFragmentManager, nodesNum)
        vp_time_list.layoutParams.height = SizeUtils.dp2px(this.applicationContext, 65f * nodesNum)
        //vp_time_list.minimumHeight = SizeUtils.dp2px(this.applicationContext, 17f * nodesNum)
        tl_time_list.setupWithViewPager(vp_time_list)
    }

    private fun initEvent() {
        tv_cancel.setOnClickListener {
            finish()
        }

        tv_save.setOnClickListener { _ ->
            saveData()
        }

        s_time_same.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(this.applicationContext, "s_time_same", isChecked)
            if (isChecked) {
                ll_set_length.visibility = View.VISIBLE
            } else {
                ll_set_length.visibility = View.GONE
            }
        }

        s_summer.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(this.applicationContext, "s_summer", isChecked)
        }

        sb_time_length.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_time_length.text = "${progress + 30}"
                viewModel.refreshEndTime(progress + 30)
                PreferenceUtils.saveIntToSP(this@TimeSettingsActivity.applicationContext, "classLen", progress + 30)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun saveData() {
        viewModel.saveData(PreferenceUtils.getBooleanFromSP(this.applicationContext, "s_summer", false))
        viewModel.getSaveInfo().observe(this, Observer { s ->
            when (s) {
                "ok" -> {
                    Toasty.success(this.applicationContext, "保存成功，下次启动App生效").show()
                    finish()
                }
                else -> {
                    Toasty.error(this.applicationContext, s!!, Toast.LENGTH_LONG).show()
                }
            }
        })
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
