package com.suda.yzune.wakeupschedule

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.suda.yzune.wakeupschedule.utils.UpdateUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        ViewUtils.resizeStatusBar(this, v_status)

        try {
            tv_version.text = "版本号：${UpdateUtils.getVersionName(this)}"
        } catch (e: Exception) {
            e.printStackTrace()
        }


        ib_back.setOnClickListener {
            finish()
        }

        tv_donate.setOnClickListener {

        }
    }
}
