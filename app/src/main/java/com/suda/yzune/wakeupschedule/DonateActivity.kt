package com.suda.yzune.wakeupschedule

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.suda.yzune.wakeupschedule.utils.ViewUtils

class DonateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)
    }
}
