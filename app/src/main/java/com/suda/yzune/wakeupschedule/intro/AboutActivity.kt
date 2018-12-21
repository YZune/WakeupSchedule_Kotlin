package com.suda.yzune.wakeupschedule.intro

import android.os.Bundle
import android.widget.TextView
import com.suda.yzune.wakeupschedule.BuildConfig
import com.suda.yzune.wakeupschedule.DonateActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseBlurTitleActivity
import com.suda.yzune.wakeupschedule.utils.UpdateUtils
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColorResource

class AboutActivity : BaseBlurTitleActivity() {
    override val layoutId: Int
        get() = R.layout.activity_about

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        return if (BuildConfig.CHANNEL == "google") {
            null
        } else {
            tvButton.text = "捐赠"
            tvButton.textColorResource = R.color.colorAccent
            tvButton.setOnClickListener {
                startActivity<DonateActivity>()
            }
            tvButton
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            tv_version.text = "版本号：${UpdateUtils.getVersionName(this)}"
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
