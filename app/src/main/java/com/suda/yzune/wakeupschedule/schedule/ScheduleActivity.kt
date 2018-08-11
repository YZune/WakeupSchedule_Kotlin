package com.suda.yzune.wakeupschedule.schedule

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.Toast
import com.suda.yzune.wakeupschedule.AboutActivity
import com.suda.yzune.wakeupschedule.GlideApp
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_schedule.*
import com.suda.yzune.wakeupschedule.MainActivity
import com.suda.yzune.wakeupschedule.bean.TimeBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.settings.SettingsActivity
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import com.suda.yzune.wakeupschedule.schedule_import.LoginWebActivity
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countWeek
import com.suda.yzune.wakeupschedule.utils.CourseUtils.isQQClientAvailable
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import es.dmoral.toasty.Toasty
import java.text.ParseException


class ScheduleActivity : AppCompatActivity() {

    private lateinit var mainBgContainer: View
    var whichWeek = 1
    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        viewModel.initRepository(applicationContext)

        Toasty.Config.getInstance()
                .setToastTypeface(Typeface.DEFAULT_BOLD)
                .setTextSize(12)
                .apply()

        viewModel.updateFromOldVer(applicationContext)
        initView()
        initViewStub()
        initEvent()

        DonateFragment.newInstance().show(supportFragmentManager, "donateDialog")
    }

    override fun onStart() {
        super.onStart()

        viewModel.getTimeDetailLiveList().observe(this, Observer {
            viewModel.getTimeList().clear()
            viewModel.getTimeList().addAll(it!!)
        })


        whichWeek = countWeek(this)
        tv_week.text = "第${whichWeek}周"

        val uri = PreferenceUtils.getStringFromSP(this.applicationContext, "pic_uri", "")
        if (uri != "") {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            GlideApp.with(this.applicationContext)
                    .load(uri)
                    .override(x, y)
                    .into(mainBgContainer.findViewById(R.id.iv_bg))
        } else {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            GlideApp.with(this.applicationContext)
                    .load(resources.getDrawable(R.drawable.main_bg))
                    .override(x, y)
                    .into(mainBgContainer.findViewById(R.id.iv_bg))
        }
        if (PreferenceUtils.getBooleanFromSP(this.applicationContext, "s_color", true)) {
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

        initViewPage()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {

    }

    private fun initView() {
        tv_date.text = CourseUtils.getTodayDate()
        tv_weekday.text = CourseUtils.getWeekday()

        navigation_view.itemIconTintList = null
        navigation_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_setting -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    drawerLayout.postDelayed({
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_about -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    drawerLayout.postDelayed({
                        startActivity(Intent(this, AboutActivity::class.java))
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_young -> {
                    Toasty.info(this.applicationContext, "咩咩将为你记录倒计时等事件哦，敬请期待").show()
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_feedback -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    drawerLayout.postDelayed({
                        if (isQQClientAvailable(applicationContext)) {
                            val qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=1055614742&version=1"
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)))
                        } else {
                            Toasty.error(applicationContext, "手机上没有安装QQ，无法启动聊天窗口:-(", Toast.LENGTH_LONG).show()
                        }
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    Toasty.info(this.applicationContext, "敬请期待").show()
                    return@setNavigationItemSelectedListener true
                }
            }
        }
    }

    private fun initViewStub() {
        mainBgContainer = vs_main_bg.inflate()
    }

    private fun initViewPage() {
        if (whichWeek >= 1) {
            tv_week.text = "第" + whichWeek + "周"
        } else {
            tv_week.text = "还没有开学哦"
            whichWeek = 1
        }

        val mAdapter = SchedulePagerAdapter(supportFragmentManager)
        vp_schedule.adapter = mAdapter
        vp_schedule.offscreenPageLimit = 5
        for (i in 1..30) {
            mAdapter.addFragment(ScheduleFragment.newInstance(i))
        }
        mAdapter.notifyDataSetChanged()
        vp_schedule.currentItem = whichWeek - 1
    }

    private fun initEvent() {

        ib_import.setOnClickListener {
            startActivity(Intent(this, LoginWebActivity::class.java))
        }

        ib_add.setOnClickListener {
            val intent = Intent(this, AddCourseActivity::class.java)
            startActivity(intent)
        }

        rl_title.setOnClickListener {
            try {
                whichWeek = countWeek(this)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            tv_weekday.text = CourseUtils.getWeekday()
            //tv_weekday.text = ""
            vp_schedule.currentItem = whichWeek - 1
        }

        vp_schedule.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                whichWeek = position + 1
                try {
                    if (countWeek(this@ScheduleActivity) > 0) {
                        if (whichWeek == countWeek(this@ScheduleActivity)) {
                            tv_week.text = "第${whichWeek}周"
                            tv_weekday.text = CourseUtils.getWeekday()
                            tv_date.text = CourseUtils.getTodayDate()
                        } else {
                            tv_week.text = "第${whichWeek}周"
                            tv_weekday.text = "非本周  点击此处以回到本周"
                        }
                    } else {
                        tv_week.text = "还没有开学哦"
                        tv_weekday.text = CourseUtils.getWeekday()
                        tv_date.text = CourseUtils.getTodayDate()
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }

            override fun onPageScrolled(a: Int, b: Float, c: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }
}
