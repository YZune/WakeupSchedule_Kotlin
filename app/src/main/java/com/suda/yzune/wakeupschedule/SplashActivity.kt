package com.suda.yzune.wakeupschedule

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.suda.yzune.wakeupschedule.schedule.ScheduleActivity
import com.suda.yzune.wakeupschedule.utils.UpdateUtils
import es.dmoral.toasty.Toasty

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdateUtils.tranOldData(applicationContext)
        Toasty.Config.getInstance()
                .setToastTypeface(Typeface.DEFAULT_BOLD)
                .setTextSize(12)
                .apply()
        startActivity(Intent(this, ScheduleActivity::class.java))
        finish()
    }
}
