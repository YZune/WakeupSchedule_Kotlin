package com.suda.yzune.wakeupschedule.main

import android.view.View
import android.view.ViewManager
import android.widget.ImageView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.widget.MyViewPager
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

class MainActivityUI : AnkoComponent<MainActivity> {

    private inline fun ViewManager.myViewPager(init: MyViewPager.() -> Unit) = ankoView({ MyViewPager(it) }, theme = 0) { init() }

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

            myViewPager {
                id = R.id.anko_vp_schedule
                overScrollMode = View.OVER_SCROLL_NEVER
            }.lparams(matchParent, matchParent)
        }

    }.view
}