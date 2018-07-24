package com.suda.yzune.wakeupschedule.schedule

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_schedule.*
import android.widget.RelativeLayout
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import es.dmoral.toasty.Toasty


class ScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        //resizeStatusBar(this)
        initViewPage()
        initEvent()
    }

    private fun initViewPage() {
        val mAdapter = SchedulePagerAdapter(supportFragmentManager)
        vp_schedule.adapter = mAdapter
        vp_schedule.offscreenPageLimit = 5
        for (i in 1..20) {
            mAdapter.addFragment(ScheduleFragment.newInstance())
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun initEvent() {
        ab_main.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scrollRange = appBarLayout.totalScrollRange
            val alpha = 1 - 1.0f * Math.abs(verticalOffset) / scrollRange
            ll_today_courses.alpha = alpha
            iv_title_bg.alpha = alpha
            iv_bg.alpha = 1 - alpha
            nsv_schedule.setNeedScroll(alpha != 0f)
        })
    }

    private fun resizeStatusBar(context: Context) {
        val layoutParams = v_status.layoutParams as RelativeLayout.LayoutParams
        var statusHeight = ViewUtils.getStatusBarHeight(this)
        //Toasty.success(context, "高度为$statusHeight").show()
        if (statusHeight == SizeUtils.dp2px(context, 24f)) {
            statusHeight *= 2
        }
        layoutParams.height = statusHeight
        v_status.layoutParams = layoutParams
    }
}
