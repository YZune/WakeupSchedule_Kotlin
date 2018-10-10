package com.suda.yzune.wakeupschedule.schedule_manage

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_schedule_settings.*

class ScheduleManageActivity : AppCompatActivity() {

    private lateinit var viewModel: ScheduleManageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ScheduleManageViewModel::class.java)
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_manage)
        ViewUtils.resizeStatusBar(this, v_status)


    }
}
