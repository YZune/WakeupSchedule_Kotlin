package com.suda.yzune.wakeupschedule.intro

import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseBlurTitleActivity
import com.suda.yzune.wakeupschedule.utils.Utils
import kotlinx.android.synthetic.main.activity_intro_young.*

class IntroYoungActivity : BaseBlurTitleActivity() {
    override val layoutId: Int
        get() = R.layout.activity_intro_young

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Glide.with(this)
                .load("https://ws1.sinaimg.cn/large/006tNbRwgy1fxto1a67fej305c05cwen.jpg")
                .error(R.drawable.net_work_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(iv_logo)

        tv_download.setOnClickListener {
            Utils.openUrl(this, "https://www.coolapk.com/apk/com.suda.yzune.youngcommemoration")
        }
    }
}
