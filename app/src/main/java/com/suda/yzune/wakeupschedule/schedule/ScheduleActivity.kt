package com.suda.yzune.wakeupschedule.schedule

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.AboutActivity
import com.suda.yzune.wakeupschedule.GlideApp
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.UpdateFragment
import com.suda.yzune.wakeupschedule.apply_info.ApplyInfoActivity
import com.suda.yzune.wakeupschedule.bean.UpdateInfoBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import com.suda.yzune.wakeupschedule.settings.SettingsActivity
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countWeek
import com.suda.yzune.wakeupschedule.utils.CourseUtils.isQQClientAvailable
import com.suda.yzune.wakeupschedule.utils.MyRetrofitUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.UpdateUtils.getVersionCode
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_schedule.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException


class ScheduleActivity : AppCompatActivity() {

    var whichWeek = 1
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var clipboardManager: ClipboardManager
    private lateinit var mAdapter: SchedulePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        viewModel.initRepository(applicationContext)
        PreferenceUtils.init(applicationContext)
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        clipboardManager = applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        Toasty.Config.getInstance()
                .setToastTypeface(Typeface.DEFAULT_BOLD)
                .setTextSize(12)
                .apply()

        viewModel.updateFromOldVer(applicationContext)
        initView()
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
                        try {
                            val updateInfo = gson.fromJson<UpdateInfoBean>(response.body()!!.string(), object : TypeToken<UpdateInfoBean>() {
                            }.type)
                            if (updateInfo.id > getVersionCode(this@ScheduleActivity.applicationContext)) {
                                UpdateFragment.newInstance(updateInfo).show(supportFragmentManager, "updateDialog")
                            }
                        } catch (e: JsonSyntaxException) {

                        }
                    }
                }

            })
        }

        if (!PreferenceUtils.getBooleanFromSP(applicationContext, "has_intro", false)) {
            initIntro()
        }

        initCourseData()
        initViewPage()
    }

    private fun initCourseData() {
        viewModel.getTimeDetailLiveList().observe(this, Observer {
            viewModel.timeList.addAll(it!!)
            for (i in 1..7) {
                viewModel.getRawCourseByDay(i, "").observe(this, Observer { list ->
                    viewModel.allCourseList[i - 1].value = list
                })

                viewModel.getRawCourseByDay(i, "lover").observe(this, Observer { list ->
                    viewModel.loverCourseList[i - 1].value = list
                })
            }
        })

        viewModel.getSummerTimeLiveList().observe(this, Observer {
            viewModel.summerTimeList.addAll(it!!)
        })
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
        if (clipboardManager.primaryClip != null) {
            if (clipboardManager.primaryClip.itemCount > 0) {
                val clipStr = clipboardManager.primaryClip.getItemAt(0).text.toString()
                if (clipStr.startsWith("来自WakeUp课程表的分享：")) {
                    viewModel.tranClipboardStr(clipStr)
                    ClipboardImportFragment.newInstance().show(supportFragmentManager, "ClipboardImportFragment")
                }
            }
        }

        viewModel.refreshViewData(applicationContext)

        whichWeek = if (viewModel.savedWeek == -1) {
            countWeek(this)
        } else {
            viewModel.savedWeek
        }

        if (whichWeek == countWeek(this)) {
            tv_weekday.text = CourseUtils.getWeekday()
        } else {
            tv_weekday.text = "非本周  点击此处以回到本周"
        }

        tv_week.text = "第${whichWeek}周"
        tv_date.text = CourseUtils.getTodayDate()

        if (whichWeek >= 1) {
            tv_week.text = "第" + whichWeek + "周"
        } else {
            tv_week.text = "还没有开学哦"
            whichWeek = 1
        }
        vp_schedule.currentItem = whichWeek - 1

        val uri = PreferenceUtils.getStringFromSP(this.applicationContext, "pic_uri", "")
        if (uri != "") {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            GlideApp.with(this.applicationContext)
                    .load(uri)
                    .override(x, y)
                    .into(iv_bg)
        } else {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            GlideApp.with(this.applicationContext)
                    .load(R.drawable.main_bg)
                    .override(x, y)
                    .into(iv_bg)
        }

        if (viewModel.showWhite) {
            for (i in 0 until cl_schedule.childCount) {
                val view = cl_schedule.getChildAt(i)
                when (view) {
                    is TextView -> view.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    is ImageButton -> view.setColorFilter(ContextCompat.getColor(applicationContext, R.color.white))
                }
            }
        } else {
            for (i in 0 until cl_schedule.childCount) {
                val view = cl_schedule.getChildAt(i)
                when (view) {
                    is TextView -> view.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
                    is ImageButton -> view.setColorFilter(ContextCompat.getColor(applicationContext, R.color.black))
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {

    }

    private fun initView() {
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
                R.id.nav_explore -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    drawerLayout.postDelayed({
                        startActivity(Intent(this, ApplyInfoActivity::class.java))
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

    private fun initViewPage() {
        mAdapter = SchedulePagerAdapter(supportFragmentManager)
        vp_schedule.adapter = mAdapter
        vp_schedule.offscreenPageLimit = 1

        for (i in 1..PreferenceUtils.getIntFromSP(this.applicationContext, "sb_weeks", 30)) {
            mAdapter.addFragment(ScheduleFragment.newInstance(i))
        }

        mAdapter.notifyDataSetChanged()
    }

    private fun initEvent() {
        ib_nav.setOnClickListener { drawerLayout.openDrawer(Gravity.START) }

        ib_import.setOnClickListener {
            ImportChooseFragment.newInstance().show(supportFragmentManager, "importDialog")
        }

        ib_add.setOnClickListener {
            val intent = Intent(this, AddCourseActivity::class.java)
            startActivity(intent)
        }

        ib_share.setOnClickListener { _ ->
            viewModel.getCourse().observe(this, Observer {
                val gson = Gson()
                val course = gson.toJson(it)
                val clipData = ClipData.newPlainText("WakeUpSchedule", "来自WakeUp课程表的分享：$course")
                when {
                    course != "" -> {
                        clipboardManager.primaryClip = clipData
                        Toasty.success(this, "课程已经复制到剪贴板啦，快原封不动地发给小伙伴吧~", Toast.LENGTH_LONG).show()
                    }
                    course == "" -> Toasty.error(this, "看起来你的课表还是空的哦w(ﾟДﾟ)w", Toast.LENGTH_LONG).show()
                    else -> Toasty.error(this, "分享失败w(ﾟДﾟ)w", Toast.LENGTH_LONG).show()
                }
            })
        }

        tv_weekday.setOnClickListener {
            try {
                whichWeek = countWeek(this)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            tv_weekday.text = CourseUtils.getWeekday()
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

    override fun onPause() {
        super.onPause()
        viewModel.savedWeek = whichWeek
    }

    override fun onDestroy() {
        mAdapter.removeAll()
        super.onDestroy()
    }
}