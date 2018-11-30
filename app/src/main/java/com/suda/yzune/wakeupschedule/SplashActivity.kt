package com.suda.yzune.wakeupschedule

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.suda.yzune.wakeupschedule.schedule.ScheduleActivity
import com.suda.yzune.wakeupschedule.utils.UpdateUtils
import org.jetbrains.anko.startActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdateUtils.tranOldData(applicationContext)
        startActivity<ScheduleActivity>()
//       startActivity<MainActivity>()
        finish()
    }

    override fun onBackPressed() {

    }
}
