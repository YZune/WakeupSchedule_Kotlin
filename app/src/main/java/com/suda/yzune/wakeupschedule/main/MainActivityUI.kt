package com.suda.yzune.wakeupschedule.main

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.suda.yzune.wakeupschedule.R
import splitties.views.dsl.core.*

class MainActivityUI(override val ctx: Context) : Ui {

    override val root = frameLayout {
        add(imageView {
            id = R.id.anko_iv_bg
            scaleType = ImageView.ScaleType.CENTER_CROP
        }, lParams(matchParent, matchParent))

        add(imageView {
            id = R.id.anko_iv_blur
            alpha = 0f
            scaleType = ImageView.ScaleType.CENTER_CROP
        }, lParams(matchParent, matchParent))

        val viewPager: ViewPager = view(::ViewPager, R.id.anko_vp_schedule) {
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        add(viewPager, lParams(matchParent, matchParent))
    }

}