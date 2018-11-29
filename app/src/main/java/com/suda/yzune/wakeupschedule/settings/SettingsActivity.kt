package com.suda.yzune.wakeupschedule.settings

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseTitleActivity
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.design.longSnackbar

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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            s_nav_bar_color.isEnabled = false
        } else {
            s_nav_bar_color.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_nav_bar_color", true)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            s_nav_bar_blur.isEnabled = false
        } else {
            s_nav_bar_blur.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_nav_bar_blur", false)
        }
    }

    private fun initEvent() {
        s_update.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_update", isChecked)
        }
        s_nav_bar_color.setOnCheckedChangeListener { view, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_nav_bar_color", isChecked)
            view.longSnackbar("重启App后生效哦~")
        }
        s_nav_bar_blur.setOnCheckedChangeListener { view, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_nav_bar_blur", isChecked)
            view.longSnackbar("重启App后生效哦~")
        }
    }
}
