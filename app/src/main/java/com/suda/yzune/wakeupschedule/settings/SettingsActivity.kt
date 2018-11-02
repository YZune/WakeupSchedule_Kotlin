package com.suda.yzune.wakeupschedule.settings

import android.os.Bundle
import com.suda.yzune.wakeupschedule.BaseActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        resizeStatusBar(v_status)

        initView()
        initEvent()
    }

    private fun initView() {
        s_update.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_update", true)
    }

    private fun initEvent() {
        ib_back.setOnClickListener {
            finish()
        }

        s_update.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_update", isChecked)
        }
    }
}
