package com.suda.yzune.wakeupschedule

import android.os.Bundle
import com.suda.yzune.wakeupschedule.utils.UpdateUtils
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.startActivity

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        resizeStatusBar(v_status)

        try {
            tv_version.text = "版本号：${UpdateUtils.getVersionName(this)}"
        } catch (e: Exception) {
            e.printStackTrace()
        }


        ib_back.setOnClickListener {
            finish()
        }

        tv_donate.setOnClickListener {
            startActivity<DonateActivity>()
        }
    }
}
