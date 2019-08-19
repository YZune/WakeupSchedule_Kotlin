package com.suda.yzune.wakeupschedule.main

import android.view.View
import android.widget.ImageView
import com.suda.yzune.wakeupschedule.R
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.viewPager

class MainActivityUI : AnkoComponent<MainActivity> {

    override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {

        frameLayout {

            imageView {
                id = R.id.anko_iv_bg
                scaleType = ImageView.ScaleType.CENTER_CROP
            }.lparams(matchParent, matchParent)

            imageView {
                id = R.id.anko_iv_blur
                alpha = 0f
                scaleType = ImageView.ScaleType.CENTER_CROP
            }.lparams(matchParent, matchParent)

            viewPager {
                id = R.id.anko_vp_schedule
                overScrollMode = View.OVER_SCROLL_NEVER
            }.lparams(matchParent, matchParent)
        }

    }.view
}