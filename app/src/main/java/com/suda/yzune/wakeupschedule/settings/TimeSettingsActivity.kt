package com.suda.yzune.wakeupschedule.settings

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.Toast
import com.suda.yzune.wakeupschedule.R
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

        initView()
        initEvent()
    }

    private fun initView() {
        val fragment = TimeTableFragment.newInstance(intent.extras.getInt("selectedId"))
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_time_setting, fragment, "timeTableFragment")
        transaction.commit()
    }

    private fun initEvent() {
        tv_cancel.setOnClickListener {
            finish()
        }

        tv_save.setOnClickListener { _ ->
            saveData()
        }
    }

    private fun saveData() {
        viewModel.saveData()
        viewModel.saveInfo.observe(this, Observer { s ->
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
