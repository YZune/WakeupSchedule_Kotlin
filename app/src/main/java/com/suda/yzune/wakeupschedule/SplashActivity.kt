package com.suda.yzune.wakeupschedule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.suda.yzune.wakeupschedule.schedule.ScheduleActivity
import com.suda.yzune.wakeupschedule.utils.UpdateUtils
import kotlinx.coroutines.launch
import splitties.activities.start

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            UpdateUtils.tranOldData(applicationContext)
            start<ScheduleActivity>()
            //startActivity<SudaLifeActivity>("type" to "澡堂")
            finish()
        }
    }

    override fun onBackPressed() {

    }
}
