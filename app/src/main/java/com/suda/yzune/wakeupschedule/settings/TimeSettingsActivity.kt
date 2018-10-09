package com.suda.yzune.wakeupschedule.settings

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_time_settings.*

class TimeSettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: TimeSettingsViewModel
    private lateinit var navController: NavController
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

        viewModel.saveInfo.observe(this, Observer { s ->
            when (s) {
                "detail_ok" -> {
                    navController.navigateUp()
                    Toasty.success(this.applicationContext, "保存成功").show()
                }
                else -> {
                    Toasty.error(this.applicationContext, "出现错误>_<", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun initView() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navGraph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_time_settings)
        val fragDestination = navGraph.findNode(R.id.timeTableFragment)!!
        fragDestination.setDefaultArguments(Bundle().apply {
            this.putInt("selectedId", intent.extras!!.getInt("selectedId"))
        })
        navHostFragment.navController.graph = navGraph
        navController = Navigation.findNavController(this, R.id.nav_fragment)
        navController.addOnNavigatedListener { _, destination ->
            tv_title.text = destination.label
        }
    }

    private fun initEvent() {
        tv_cancel.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.timeTableFragment -> {
                    finish()
                }
                R.id.timeSettingsFragment -> {
                    navController.navigateUp()
                }
            }
        }

        tv_save.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.timeTableFragment -> {
                    setResult(Activity.RESULT_OK, Intent().putExtra("selectedId", viewModel.selectedId))
                    finish()
                }
                R.id.timeSettingsFragment -> {
                    viewModel.saveDetailData(viewModel.entryPosition)
                }
            }
        }
    }

    private fun exitBy2Click() {
        if (!isExit) {
            isExit = true // 准备退出
            Toasty.info(this.applicationContext, "再按一次退出编辑").show()
            tExit.start() // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            when (navController.currentDestination?.id) {
                R.id.timeTableFragment -> {
                    finish()
                }
                R.id.timeSettingsFragment -> {
                    navController.navigateUp()
                }
            }
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
