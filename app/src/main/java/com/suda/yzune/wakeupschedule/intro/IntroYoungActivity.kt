package com.suda.yzune.wakeupschedule.intro

import android.os.Bundle
import android.widget.TextView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.suda.yzune.wakeupschedule.GlideApp
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseBlurTitleActivity
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import kotlinx.android.synthetic.main.activity_intro_young.*

class IntroYoungActivity : BaseBlurTitleActivity() {
    override val layoutId: Int
        get() = R.layout.activity_intro_young

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlideApp.with(this)
                .load("https://ws1.sinaimg.cn/large/006tNbRwgy1fxto1a67fej305c05cwen.jpg")
                .error(R.drawable.net_work_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(iv_logo)

        tv_download.setOnClickListener {
            CourseUtils.openUrl(applicationContext, "https://www.coolapk.com/apk/com.suda.yzune.youngcommemoration")
        }
    }
}
