package com.suda.yzune.wakeupschedule.schedule

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.os.PersistableBundle
import android.support.design.widget.AppBarLayout
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_schedule.*
import android.widget.RelativeLayout
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import com.suda.yzune.wakeupschedule.schedule_import.ImportViewModel
import com.suda.yzune.wakeupschedule.schedule_import.LoginWebActivity
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

        initView(viewModel)
        initViewStub()
        initViewPage()
        initEvent(viewModel)
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {

    }

    private fun initView(viewModel: ScheduleViewModel){
        tv_date.text = viewModel.getTodayDate()
        tv_weekday.text = viewModel.getWeekday()
    }

    private fun initViewStub() {
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
//        val ll_today_courses = ll_today_courses_container.findViewById<LinearLayout>(R.id.ll_today_courses)
//        val iv_title_bg = title_bg_container.findViewById<ImageView>(R.id.iv_bg)
//        val iv_bg = main_bg_container.findViewById<ImageView>(R.id.iv_bg)
//        ab_main.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
//            val scrollRange = appBarLayout.totalScrollRange
//            val alpha = 1 - 1.0f * Math.abs(verticalOffset) / scrollRange
//            ll_today_courses.alpha = alpha
//            iv_title_bg.alpha = alpha
//            iv_bg.alpha = 1 - alpha
//            nsv_schedule.setNeedScroll(alpha != 0f)
//        })

        ib_import.setOnClickListener {
            startActivity(Intent(this, LoginWebActivity::class.java))
        }

        ib_add.setOnClickListener {
            val intent = Intent(this, AddCourseActivity::class.java)
            startActivity(intent)
        }
    }
}
