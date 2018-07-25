package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.support.design.widget.AppBarLayout
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_schedule.*
import android.widget.RelativeLayout
import com.suda.yzune.wakeupschedule.schedule_import.ImportViewModel
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import es.dmoral.toasty.Toasty


class ScheduleActivity : AppCompatActivity() {

    lateinit var ll_today_courses_container: View
    lateinit var title_bg_container: View
    lateinit var main_bg_container: View

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        viewModel.initRepository(applicationContext)

        initViewStub()
        initViewPage()
        initEvent(viewModel)
    }

    private fun initViewStub() {
        ll_today_courses_container = vs_today_courses.inflate()
        title_bg_container = vs_title_bg.inflate()
        main_bg_container = vs_main_bg.inflate()
    }

    private fun initViewPage() {
        val mAdapter = SchedulePagerAdapter(supportFragmentManager)
        vp_schedule.adapter = mAdapter
        vp_schedule.offscreenPageLimit = 5
        for (i in 1..25) {
            mAdapter.addFragment(ScheduleFragment.newInstance(i))
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun initEvent(viewModel: ScheduleViewModel) {
        val ll_today_courses = ll_today_courses_container.findViewById<LinearLayout>(R.id.ll_today_courses)
        val iv_title_bg = title_bg_container.findViewById<ImageView>(R.id.iv_bg)
        val iv_bg = main_bg_container.findViewById<ImageView>(R.id.iv_bg)
        ab_main.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scrollRange = appBarLayout.totalScrollRange
            val alpha = 1 - 1.0f * Math.abs(verticalOffset) / scrollRange
            ll_today_courses.alpha = alpha
            iv_title_bg.alpha = alpha
            iv_bg.alpha = 1 - alpha
            nsv_schedule.setNeedScroll(alpha != 0f)
        })

    }
}
