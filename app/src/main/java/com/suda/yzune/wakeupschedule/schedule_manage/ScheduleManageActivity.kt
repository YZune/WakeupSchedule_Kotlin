package com.suda.yzune.wakeupschedule.schedule_manage

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.suda.yzune.wakeupschedule.BaseActivity
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.activity_schedule_manage.*

class ScheduleManageActivity : BaseActivity() {

    private lateinit var viewModel: ScheduleManageViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ScheduleManageViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_manage)
        resizeStatusBar(v_status)

        initView()
        initEvent()
    }

    private fun initView() {
        navController = Navigation.findNavController(this, R.id.nav_fragment)
        navController.addOnNavigatedListener { _, destination ->
            tv_title.text = destination.label
        }
    }

    private fun initEvent() {
        ib_back.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.scheduleManageFragment -> {
                    finish()
                }
                R.id.courseManageFragment -> {
                    navController.navigateUp()
                }
            }
        }
    }
}
