package com.suda.yzune.wakeupschedule

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.suda.yzune.wakeupschedule.schedule.ScheduleActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, ScheduleActivity::class.java))
        finish()
//        UpdateUtils.tranOldData(applicationContext).observe(this, Observer {
//            if (it == 1){
//
//            }
//        })
    }
}
