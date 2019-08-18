package com.suda.yzune.wakeupschedule.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class MyViewPager(context: Context, attributeSet: AttributeSet?) : ViewPager(context, attributeSet) {
    constructor(context: Context) : this(context, null)

    var mDownPosX = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val x = ev!!.x

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownPosX = x
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}