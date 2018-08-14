package com.suda.yzune.wakeupschedule.schedule

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.*
import com.suda.yzune.wakeupschedule.GlideOptions.bitmapTransform
import kotlinx.android.synthetic.main.activity_schedule.*
import com.suda.yzune.wakeupschedule.bean.DonateBean
import com.suda.yzune.wakeupschedule.bean.TimeBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.bean.UpdateInfoBean
import com.suda.yzune.wakeupschedule.settings.SettingsActivity
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import com.suda.yzune.wakeupschedule.schedule_import.LoginWebActivity
import com.suda.yzune.wakeupschedule.utils.*
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countWeek
import com.suda.yzune.wakeupschedule.utils.CourseUtils.isQQClientAvailable
import com.suda.yzune.wakeupschedule.utils.UpdateUtils.getVersionCode
import es.dmoral.toasty.Toasty
import jp.wasabeef.glide.transformations.CropCircleTransformation
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

        val openTimes = PreferenceUtils.getIntFromSP(applicationContext, "open_times", 0)
        if (openTimes < 10) {
            PreferenceUtils.saveIntToSP(applicationContext, "open_times", openTimes + 1)
        } else if (openTimes == 10) {
            val dialog = DonateFragment.newInstance()
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "donateDialog")
            PreferenceUtils.saveIntToSP(applicationContext, "open_times", openTimes + 1)
        }

        if (!PreferenceUtils.getBooleanFromSP(applicationContext, "has_count", false)) {
            MyRetrofitUtils.instance.addCount(applicationContext)
        }

        if (PreferenceUtils.getBooleanFromSP(applicationContext, "s_update", true)) {
            MyRetrofitUtils.instance.getService().getUpdateInfo().enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {}

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    if (response!!.body() != null) {
                        val gson = Gson()
                        val updateInfo = gson.fromJson<UpdateInfoBean>(response.body()!!.string(), object : TypeToken<UpdateInfoBean>() {
                        }.type)
                        if (updateInfo.id > getVersionCode(this@ScheduleActivity.applicationContext)) {
                            UpdateFragment.newInstance(updateInfo).show(supportFragmentManager, "updateDialog")
                        }
                    }
                }

            })
        }

        if (!PreferenceUtils.getBooleanFromSP(applicationContext, "has_intro", false)) {
            initIntro()
        }
    }

    private fun initIntro() {
        TapTargetSequence(this)
                .targets(
                        TapTarget.forView(ib_add, "这是手动添加课程的按钮", "新版本中添加课程变得友好很多哦，试试看\n点击白色区域告诉我你get到了")
                                .outerCircleColor(R.color.red)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                .titleTextSize(16)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(12)            // Specify the size (in sp) of the description text
                                .textColor(R.color.white)            // Specify a color for both the title and description text
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60),                  // Specify the target radius (in dp)
                        TapTarget.forView(ib_import, "这是自动导入课程的按钮", "现在已经支持包括苏大在内的采用正方教务系统的学校的课程自动导入了！\n点击白色区域告诉我你get到了")
                                .outerCircleColor(R.color.lightBlue)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                .titleTextSize(16)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(12)            // Specify the size (in sp) of the description text
                                .textColor(R.color.white)            // Specify a color for both the title and description text
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60)                // Specify the target radius (in dp)
                ).listener(object : TapTargetSequence.Listener {
                    override fun onSequenceCanceled(lastTarget: TapTarget?) {

                    }

                    override fun onSequenceFinish() {
                        PreferenceUtils.saveBooleanToSP(this@ScheduleActivity.applicationContext, "has_intro", true)
                    }

                    override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                    }

                }).start()
    }

    override fun onStart() {
        super.onStart()

        viewModel.getTimeDetailLiveList().observe(this, Observer {
            viewModel.getTimeList().clear()
            viewModel.getTimeList().addAll(it!!)
        })


        whichWeek = countWeek(this)
        tv_week.text = "第${whichWeek}周"

        val headerLayout = navigation_view.getHeaderView(0)
        val headerBg = headerLayout.findViewById<ImageView>(R.id.iv_header_bg)

        val uri = PreferenceUtils.getStringFromSP(this.applicationContext, "pic_uri", "")
        if (uri != "") {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            GlideApp.with(this.applicationContext)
                    .load(uri)
                    .override(x, y)
                    .into(mainBgContainer.findViewById(R.id.iv_bg))

            GlideApp.with(this.applicationContext)
                    .load(R.drawable.main_bg)
                    .override(x, y)
                    .into(headerBg)
        } else {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            GlideApp.with(this.applicationContext)
                    .load(R.drawable.main_bg)
                    .override(x, y)
                    .into(mainBgContainer.findViewById(R.id.iv_bg))

            GlideApp.with(this.applicationContext)
                    .load(R.drawable.main_bg)
                    .override(x, y)
                    .into(headerBg)
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

        val headerLayout = navigation_view.getHeaderView(0)
        val ivPersonImage = headerLayout.findViewById(R.id.iv_person_image) as ImageView

        GlideApp.with(this)
                .load(R.mipmap.ic_launcher)
                .apply(bitmapTransform(CropCircleTransformation()))
                .into(ivPersonImage)

        headerLayout.setOnClickListener {
            Toasty.info(this.applicationContext, "敬请期待").show()
        }

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
                R.id.nav_help -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    drawerLayout.postDelayed({
                        CourseUtils.openUrl(this, "https://yzune.github.io/2018/08/13/WakeUp%E8%AF%BE%E7%A8%8B-%E9%97%AE%E7%AD%94-+-%E6%8A%80%E5%B7%A7/")
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
        ib_nav.setOnClickListener(View.OnClickListener { drawerLayout.openDrawer(Gravity.START) })

        ib_import.setOnClickListener {
            //viewModel.removeCourseData()
            ImportChooseFragment.newInstance().show(supportFragmentManager, "importDialog")
            //startActivity(Intent(this, LoginWebActivity::class.java))
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
