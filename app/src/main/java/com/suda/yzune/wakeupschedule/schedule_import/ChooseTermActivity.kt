package com.suda.yzune.wakeupschedule.schedule_import

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils

class ChooseTermActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_term)
    }
}
