package com.suda.yzune.wakeupschedule.main

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.ImageView
import android.widget.OverScroller
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.schedule.ScheduleActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.navigationView
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.drawerLayout
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