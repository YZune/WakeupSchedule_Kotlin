package com.suda.yzune.wakeupschedule.settings

import android.os.Bundle
import android.widget.TextView
import com.suda.yzune.wakeupschedule.BaseTitleActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseTitleActivity() {
    override val layoutId: Int
        get() = R.layout.activity_settings

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initEvent()
    }

    private fun initView() {
        s_update.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_update", true)
    }

    private fun initEvent() {
        s_update.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_update", isChecked)
        }
    }
}
