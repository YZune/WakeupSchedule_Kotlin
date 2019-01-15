package com.suda.yzune.wakeupschedule.schedule

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.suda.yzune.wakeupschedule.GlideApp
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.UpdateFragment
import com.suda.yzune.wakeupschedule.apply_info.ApplyInfoActivity
import com.suda.yzune.wakeupschedule.base_view.BaseActivity
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import com.suda.yzune.wakeupschedule.bean.UpdateInfoBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import com.suda.yzune.wakeupschedule.intro.AboutActivity
import com.suda.yzune.wakeupschedule.intro.IntroActivity
import com.suda.yzune.wakeupschedule.intro.IntroYoungActivity
import com.suda.yzune.wakeupschedule.schedule_import.LoginWebActivity
import com.suda.yzune.wakeupschedule.schedule_manage.ScheduleManageActivity
import com.suda.yzune.wakeupschedule.schedule_settings.ScheduleSettingsActivity
import com.suda.yzune.wakeupschedule.settings.SettingsActivity
import com.suda.yzune.wakeupschedule.utils.*
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countWeek
import com.suda.yzune.wakeupschedule.utils.UpdateUtils.getVersionCode
import com.suda.yzune.wakeupschedule.widget.ModifyTableNameFragment
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException


class ScheduleActivity : BaseActivity() {

    private lateinit var viewModel: ScheduleViewModel
    private var mAdapter: SchedulePagerAdapter? = null

    private lateinit var scheduleViewPager: androidx.viewpager.widget.ViewPager
    private lateinit var bgImageView: ImageView
    private lateinit var scheduleConstraintLayout: ConstraintLayout
    private var weekSeekBar: VerticalSeekBar? = null
    private lateinit var navImageButton: TextView
    private lateinit var addImageButton: TextView
    private lateinit var importImageButton: TextView
    private lateinit var moreImageButton: TextView
    private lateinit var tableNameRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var dateTextView: TextView
    private lateinit var weekTextView: TextView
    private lateinit var weekdayTextView: TextView
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)

        PreferenceUtils.init(applicationContext)
        super.onCreate(savedInstanceState)
        if (PreferenceUtils.getBooleanFromSP(applicationContext, "hide_main_nav_bar", false) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        ScheduleActivityUI().setContentView(this)

        val json = PreferenceUtils.getStringFromSP(application, "course", "")!!
        if (json != "") {
            launch {
                val task = withContext(Dispatchers.IO) {
                    try {
                        viewModel.updateFromOldVer(json)
                        "ok"
                    } catch (e: Exception) {
                        e.message
                    }
                }
                if (task == "ok") {
                    Toasty.success(applicationContext, "升级成功~").show()
                } else {
                    Toasty.error(applicationContext, "出现异常>_<\n$task").show()
                }
            }
        }

        scheduleViewPager = find(R.id.anko_vp_schedule)
        bgImageView = find(R.id.anko_iv_bg)
        scheduleConstraintLayout = find(R.id.anko_cl_schedule)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            weekSeekBar = find(R.id.anko_sb_week)
        }
        navImageButton = find(R.id.anko_ib_nav)
        addImageButton = find(R.id.anko_ib_add)
        importImageButton = find(R.id.anko_ib_import)
        moreImageButton = find(R.id.anko_ib_more)
        tableNameRecyclerView = find(R.id.anko_rv_table_name)
        dateTextView = find(R.id.anko_tv_date)
        weekTextView = find(R.id.anko_tv_week)
        weekdayTextView = find(R.id.anko_tv_weekday)
        navigationView = find(R.id.anko_nv)
        drawerLayout = find(R.id.anko_drawer_layout)

        initView()
        initNavView()

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

        if (!PreferenceUtils.getBooleanFromSP(applicationContext, "v3.20", false)) {
            try {
                startActivity<IntroActivity>()
            } catch (e: Exception) {
                Toasty.error(applicationContext, "使用教程载入失败>_<请查看侧栏的使用技巧").show()
                PreferenceUtils.saveBooleanToSP(applicationContext, "v3.20", true)
            }
        }

        viewModel.initTableSelectList().observe(this, Observer {
            if (it == null) return@Observer
            viewModel.tableSelectList.clear()
            viewModel.tableSelectList.addAll(it)
            if (tableNameRecyclerView.adapter == null) {
                initTableMenu(viewModel.tableSelectList)
            } else {
                tableNameRecyclerView.adapter?.notifyDataSetChanged()
            }
        })

        //DonateFragment.newInstance().show(supportFragmentManager, "AfterImportTipFragment")
    }

    private fun initTheme() {
        if (viewModel.table.background != "") {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            GlideApp.with(this.applicationContext)
                    .load(viewModel.table.background)
                    .override(x, y)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(bgImageView)
        } else {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            GlideApp.with(this.applicationContext)
                    .load(R.drawable.main_background_2019)
                    .override(x, y)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(bgImageView)
        }

        for (i in 0 until scheduleConstraintLayout.childCount) {
            val view = scheduleConstraintLayout.getChildAt(i)
            when (view) {
                is TextView -> view.setTextColor(viewModel.table.textColor)
                is ImageButton -> view.setColorFilter(viewModel.table.textColor)
            }
        }

        viewModel.itemHeight = dip(viewModel.table.itemHeight)
    }

    private fun initTableMenu(data: List<TableSelectBean>) {
        tableNameRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val fadeOutAni = ObjectAnimator.ofFloat(scheduleViewPager, "alpha", 1f, 0f)
        fadeOutAni.duration = 500
        val adapter = TableNameAdapter(R.layout.item_table_select_main, data)
        adapter.addHeaderView(FrameLayout(this).apply {
            this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(24))
        })
        adapter.addFooterView(initFooterView())
        adapter.setOnItemChildClickListener { _, view, _ ->
            when (view.id) {
                R.id.menu_setting -> {
                    startActivityForResult<ScheduleSettingsActivity>(16, "tableData" to viewModel.table)
                }
                R.id.menu_export -> {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                    } else {
                        ExportSettingsFragment().show(supportFragmentManager, "exportSettingsFragment")
                    }
                }
            }
        }
        adapter.setOnItemClickListener { _, _, position ->
            if (position < data.size) {
                if (data[position].id != viewModel.table.id) {
                    fadeOutAni.start()
                    launch {
                        withContext(Dispatchers.IO) {
                            viewModel.changeDefaultTable(data[position].id)
                        }
                        initView()
                        val list = withContext(Dispatchers.IO) {
                            viewModel.getScheduleWidgetIds()
                        }
                        val table = withContext(Dispatchers.IO) {
                            viewModel.getDefaultTable()
                        }
                        list.forEach {
                            when (it.detailType) {
                                0 -> AppWidgetUtils.refreshScheduleWidget(applicationContext, appWidgetManager, it.id, table)
                                1 -> AppWidgetUtils.refreshTodayWidget(applicationContext, appWidgetManager, it.id, table)
                            }
                        }
                    }
                }
            }
        }
        tableNameRecyclerView.adapter = adapter
    }

    private fun initFooterView(): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_table_add_main, tableNameRecyclerView, false)
        val tableAdd = view.findViewById<TextView>(R.id.nav_table_add)
        tableAdd.setOnClickListener {
            ModifyTableNameFragment.newInstance(object : ModifyTableNameFragment.TableNameChangeListener {
                override fun writeToParcel(dest: Parcel?, flags: Int) {
                }

                override fun describeContents(): Int {
                    return 0
                }

                override fun onFinish(editText: EditText, dialog: Dialog) {
                    if (!editText.text.toString().isEmpty()) {
                        launch {
                            val task = async(Dispatchers.IO) {
                                try {
                                    viewModel.addBlankTableAsync(editText.text.toString())
                                    "ok"
                                } catch (e: Exception) {
                                    e.toString()
                                }
                            }
                            val result = task.await()
                            if (result == "ok") {
                                Toasty.success(applicationContext, "新建成功~").show()
                                dialog.dismiss()
                            } else {
                                Toasty.error(applicationContext, "操作失败>_<").show()
                                dialog.dismiss()
                            }
                        }
                    } else {
                        Toasty.error(applicationContext, "名称不能为空哦>_<").show()
                    }
                }
            }).show(supportFragmentManager, "addTableFragment")
        }
        val tableManage = view.findViewById<TextView>(R.id.nav_table_manage)
        tableManage.setOnClickListener {
            startActivityForResult<ScheduleManageActivity>(16)
        }
        return view
    }

    private fun initIntro() {
        TapTargetSequence(this)
                .targets(
                        TapTarget.forView(addImageButton, "这是手动添加课程的按钮", "新版本中添加课程变得友好很多哦，试试看\n点击白色区域告诉我你get到了")
                                .outerCircleColor(R.color.red)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(16)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(12)
                                .textColor(R.color.white)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60),
                        TapTarget.forView(importImageButton, "这是导入课程的按钮", "现在已经支持采用正方教务系统的学校的课程自动导入了！\n还有别人分享给你的文件也要从这里导入哦~\n点击白色区域告诉我你get到了")
                                .outerCircleColor(R.color.lightBlue)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(16)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(12)
                                .textColor(R.color.white)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60),
                        TapTarget.forView(moreImageButton, "点这里发现更多", "比如可以分享课表给别人哦~\n多点去探索吧\n点击白色区域告诉我你get到了")
                                .outerCircleColor(R.color.blue)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(16)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(12)
                                .textColor(R.color.white)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60),
                        TapTarget.forView(weekdayTextView, "点击此处可快速回到当前周", "主界面左右滑动可以切换周数\n点击这里就可以快速回到当前周啦\n点击白色区域告诉我你get到了")
                                .outerCircleColor(R.color.deepOrange)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(16)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(12)
                                .textColor(R.color.white)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60)
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
        dateTextView.text = CourseUtils.getTodayDate()
    }

    private fun initNavView() {
        navigationView.itemIconTintList = null
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_setting -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    drawerLayout.postDelayed({
                        startActivityForResult<SettingsActivity>(31)
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_explore -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    drawerLayout.postDelayed({
                        startActivity<ApplyInfoActivity>()
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
                        startActivity<AboutActivity>()
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_young -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    drawerLayout.postDelayed({
                        startActivity<IntroYoungActivity>()
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_feedback -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    drawerLayout.postDelayed({
                        BeforeFeedbackFragment.newInstance().apply {
                            isCancelable = false
                        }.show(supportFragmentManager, "BeforeFeedbackFragment")
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ExportSettingsFragment().show(supportFragmentManager, "exportSettingsFragment")
                } else {
                    Toasty.error(applicationContext, "你取消了授权>_<无法导出", Toast.LENGTH_LONG).show()
                }
            }
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity<LoginWebActivity>("type" to "file")
                } else {
                    Toasty.error(applicationContext, "你取消了授权>_<无法从文件导入", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initViewPage(maxWeek: Int, table: TableBean) {
        if (mAdapter == null) {
            mAdapter = SchedulePagerAdapter(maxWeek, supportFragmentManager)
            scheduleViewPager.adapter = mAdapter
            scheduleViewPager.offscreenPageLimit = 1
        }
        mAdapter!!.maxWeek = maxWeek
        mAdapter!!.notifyDataSetChanged()
        if (CourseUtils.countWeek(table.startDate) > 0) {
            scheduleViewPager.currentItem = CourseUtils.countWeek(table.startDate) - 1
        } else {
            scheduleViewPager.currentItem = 0
        }
    }

    private fun initEvent() {
        addImageButton.setOnClickListener {
            startActivity<AddCourseActivity>(
                    "tableId" to viewModel.table.id,
                    "maxWeek" to viewModel.table.maxWeek,
                    "nodes" to viewModel.table.nodes,
                    "id" to -1)
        }

        moreImageButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        weekSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                try {
                    val currentWeek = countWeek(viewModel.table.startDate)
                    if (currentWeek > 0) {
                        if (progress + 1 == currentWeek) {
                            weekTextView.text = "第${progress + 1}周"
                            weekdayTextView.text = CourseUtils.getWeekday()
                        } else {
                            weekTextView.text = "第${progress + 1}周"
                            weekdayTextView.text = "非本周"
                        }
                    } else {
                        weekTextView.text = "还没有开学哦"
                        weekdayTextView.text = CourseUtils.getWeekday()
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                scheduleViewPager.currentItem = seekBar!!.progress
            }
        })

        navImageButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        importImageButton.setOnClickListener {
            //            val alert = AlertView("导入课程", "现在已支持从60+所学校的教务直接导入课程\n也可以去申请适配试试看:)", AlertStyle.DIALOG)
//            alert.addAction(AlertAction("苏大教务导入", AlertActionStyle.DEFAULT) {
//                startActivityForResult<LoginWebActivity>(
//                        32,
//                        "type" to "苏州大学",
//                        "tableId" to viewModel.table.id
//                )
//            })
//            alert.addAction(AlertAction("更多学校及教务导入", AlertActionStyle.DEFAULT) {
//                startActivityForResult<SchoolListActivity>(32)
//            })
//            alert.addAction(AlertAction("从导出文件导入", AlertActionStyle.DEFAULT) {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
//                } else {
//                    startActivityForResult<LoginWebActivity>(32, "type" to "file")
//                }
//            })
//            alert.addAction(AlertAction("申请适配", AlertActionStyle.NEGATIVE) {
//                startActivity<LoginWebActivity>("type" to "apply")
//            })
//            alert.show(this)
            ImportChooseFragment.newInstance().show(supportFragmentManager, "importDialog")
        }

        weekdayTextView.setOnClickListener {
            weekdayTextView.text = CourseUtils.getWeekday()
            val currentWeek = countWeek(viewModel.table.startDate)
            if (currentWeek > 0) {
                scheduleViewPager.currentItem = currentWeek - 1
            } else {
                scheduleViewPager.currentItem = 0
            }
        }

        scheduleViewPager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                viewModel.selectedWeek = position + 1
                weekSeekBar?.progress = position
                val currentWeek = countWeek(viewModel.table.startDate)
                try {
                    if (currentWeek > 0) {
                        if (viewModel.selectedWeek == currentWeek) {
                            weekTextView.text = "第${viewModel.selectedWeek}周"
                            weekdayTextView.text = CourseUtils.getWeekday()
                        } else {
                            weekTextView.text = "第${viewModel.selectedWeek}周"
                            weekdayTextView.text = "非本周"
                        }
                    } else {
                        weekTextView.text = "还没有开学哦"
                        weekdayTextView.text = CourseUtils.getWeekday()
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

    private fun initView() {
        launch {
            viewModel.table = withContext(Dispatchers.IO) {
                viewModel.getDefaultTable()
            }

            val currentWeek = countWeek(viewModel.table.startDate)

            if (currentWeek > 0) {
                weekTextView.text = "第${currentWeek}周"
            } else {
                weekTextView.text = "还没有开学哦"
            }

            weekdayTextView.text = CourseUtils.getWeekday()
            weekSeekBar?.max = viewModel.table.maxWeek - 1

            initTheme()

            val fadeInAni = ObjectAnimator.ofFloat(scheduleViewPager, "alpha", 0f, 1f)
            fadeInAni.duration = 500
            initViewPage(viewModel.table.maxWeek, viewModel.table)
            fadeInAni.start()

            initEvent()

            viewModel.timeList = async(Dispatchers.IO) {
                viewModel.getTimeList(viewModel.table.timeTable)
            }.await()

            for (i in 1..7) {
                viewModel.getRawCourseByDay(i, viewModel.table.id).observe(this@ScheduleActivity, Observer { list ->
                    if (list == null) return@Observer
                    if (list.isNotEmpty() && list[0].tableId != viewModel.table.id) return@Observer
                    viewModel.allCourseList[i - 1].value = list
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 16) {
            initView()
        }
        if (requestCode == 32 && resultCode == RESULT_OK) {
            drawerLayout.openDrawer(GravityCompat.END)
            AfterImportTipFragment.newInstance().show(supportFragmentManager, "AfterImportTipFragment")
        }
        if (requestCode == 31 && resultCode == RESULT_OK) {
            initView()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        AppWidgetUtils.updateWidget(applicationContext)
        super.onDestroy()
    }

}