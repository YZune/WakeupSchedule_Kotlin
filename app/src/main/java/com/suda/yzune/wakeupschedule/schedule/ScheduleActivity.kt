package com.suda.yzune.wakeupschedule.schedule

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_schedule.*
import com.suda.yzune.wakeupschedule.MainActivity
import com.suda.yzune.wakeupschedule.SettingsActivity
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import com.suda.yzune.wakeupschedule.schedule_import.LoginWebActivity
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import es.dmoral.toasty.Toasty


class ScheduleActivity : AppCompatActivity() {

    lateinit var main_bg_container: View

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        viewModel.initRepository(applicationContext)

        Toasty.Config.getInstance()
                .setToastTypeface(Typeface.DEFAULT_BOLD)
                .setTextSize(12)
                .apply()

        initView(viewModel)
        initViewStub()
        initViewPage()
        initEvent(viewModel)
    }

    override fun onStart() {
        super.onStart()
        val uri = PreferenceUtils.getStringFromSP(this.applicationContext, "pic_uri", "")
        if (uri != "") {
            Glide.with(this.applicationContext).load(uri).into(main_bg_container.findViewById(R.id.iv_bg))
        }
        if (PreferenceUtils.getBooleanFromSP(this.applicationContext, "s_color", false)) {
            tv_week.setTextColor(resources.getColor(R.color.white))
            tv_date.setTextColor(resources.getColor(R.color.white))
            tv_weekday.setTextColor(resources.getColor(R.color.white))
            ib_import.setColorFilter(resources.getColor(R.color.white))
            ib_add.setColorFilter(resources.getColor(R.color.white))
            ib_nav.setColorFilter(resources.getColor(R.color.white))
        } else {
            tv_week.setTextColor(resources.getColor(R.color.black))
            tv_date.setTextColor(resources.getColor(R.color.black))
            tv_weekday.setTextColor(resources.getColor(R.color.black))
            ib_import.setColorFilter(resources.getColor(R.color.black))
            ib_add.setColorFilter(resources.getColor(R.color.black))
            ib_nav.setColorFilter(resources.getColor(R.color.black))
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {

    }

    private fun initView(viewModel: ScheduleViewModel) {
        tv_date.text = viewModel.getTodayDate()
        tv_weekday.text = viewModel.getWeekday()

        navigation_view.itemIconTintList = null
        navigation_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_young -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_setting -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    return@setNavigationItemSelectedListener true
                }
            }
        }
    }

    private fun initViewStub() {
        main_bg_container = vs_main_bg.inflate()
    }

    private fun initViewPage() {
        val mAdapter = SchedulePagerAdapter(supportFragmentManager)
        vp_schedule.adapter = mAdapter
        vp_schedule.offscreenPageLimit = 5
        for (i in 1..30) {
            mAdapter.addFragment(ScheduleFragment.newInstance(i))
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun initEvent(viewModel: ScheduleViewModel) {

        ib_import.setOnClickListener {
            startActivity(Intent(this, LoginWebActivity::class.java))
        }

        ib_add.setOnClickListener {
            val intent = Intent(this, AddCourseActivity::class.java)
            startActivity(intent)
        }
    }
}
