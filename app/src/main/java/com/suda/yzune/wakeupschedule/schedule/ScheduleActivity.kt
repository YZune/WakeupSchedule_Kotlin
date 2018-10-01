package com.suda.yzune.wakeupschedule.schedule

import android.annotation.SuppressLint
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
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
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import com.suda.yzune.wakeupschedule.bean.UpdateInfoBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import com.suda.yzune.wakeupschedule.schedule_settings.ScheduleSettingsActivity
import com.suda.yzune.wakeupschedule.settings.SettingsActivity
import com.suda.yzune.wakeupschedule.utils.*
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countWeek
import com.suda.yzune.wakeupschedule.utils.CourseUtils.isQQClientAvailable
import com.suda.yzune.wakeupschedule.utils.UpdateUtils.getVersionCode
import com.suda.yzune.wakeupschedule.widget.ModifyTableNameFragment
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_schedule.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException


class ScheduleActivity : AppCompatActivity() {

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var clipboardManager: ClipboardManager
    private var mAdapter: SchedulePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)

        PreferenceUtils.init(applicationContext)
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        clipboardManager = applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        viewModel.updateFromOldVer()
        viewModel.initViewData().observe(this, Observer { table ->
            if (table == null) return@Observer
            viewModel.initTimeData(table.timeTable)

            if (table.background != "") {
                val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
                val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
                GlideApp.with(this.applicationContext)
                        .load(table.background)
                        .override(x, y)
                        //.apply(bitmapTransform(BlurTransformation(0, 5)))
                        .into(iv_bg)
            } else {
                val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
                val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
                GlideApp.with(this.applicationContext)
                        .load(R.drawable.main_background)
                        .override(x, y)
                        //.apply(bitmapTransform(BlurTransformation(0, 5)))
                        .into(iv_bg)
            }

            for (i in 0 until cl_schedule.childCount) {
                val view = cl_schedule.getChildAt(i)
                when (view) {
                    is TextView -> view.setTextColor(table.textColor)
                    is ImageButton -> view.setColorFilter(table.textColor)
                }
            }

            viewModel.itemHeight = SizeUtils.dp2px(applicationContext, table.itemHeight.toFloat())
            viewModel.currentWeek.value = countWeek(table.startDate)
            initCourseData(table.id)
            sb_week.max = table.maxWeek - 1
            initViewPage(table.maxWeek, table)

            ib_add.setOnClickListener {
                val intent = Intent(this, AddCourseActivity::class.java)
                intent.putExtra("tableId", table.id)
                intent.putExtra("maxWeek", table.maxWeek)
                intent.putExtra("id", -1)
                startActivity(intent)
            }

            ib_more.setOnClickListener { view ->
                val popupMenu = PopupMenu(this, view)
                popupMenu.menuInflater.inflate(R.menu.menu_more, popupMenu.menu)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.ib_settings -> {
                            val gson = Gson()
                            val intent = Intent(this, ScheduleSettingsActivity::class.java)
                            intent.putExtra("tableData", gson.toJson(table))
                            startActivity(intent)
                        }
                        R.id.ib_share -> {
                            viewModel.getCourse(table.id).observe(this, Observer {
                                val gson = Gson()
                                val course = gson.toJson(it)
                                val clipData = ClipData.newPlainText("WakeUpSchedule", "来自WakeUp课程表的分享：$course")
                                when {
                                    course != "" -> {
                                        clipboardManager.primaryClip = clipData
                                        Toasty.success(applicationContext, "课程已经复制到剪贴板啦，快原封不动地发给小伙伴吧~", Toast.LENGTH_LONG).show()
                                    }
                                    course == "" -> Toasty.error(applicationContext, "看起来你的课表还是空的哦w(ﾟДﾟ)w", Toast.LENGTH_LONG).show()
                                    else -> Toasty.error(applicationContext, "分享失败w(ﾟДﾟ)w", Toast.LENGTH_LONG).show()
                                }
                            })
                        }
                        R.id.ib_manage -> {
                            Toasty.info(applicationContext, "很快就能见面啦(￣▽￣)~*", Toast.LENGTH_LONG).show()
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
            }
        })

        viewModel.currentWeek.observe(this, Observer {
            if (it == null) return@Observer
            viewModel.selectedWeek = it
            initEvent(it)
        })

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

        viewModel.initTableSelectList().observe(this, Observer {
            if (it == null) return@Observer
            viewModel.tableSelectList.clear()
            viewModel.tableSelectList.addAll(it)
            if (rv_table_name.adapter == null) {
                initTableMenu(viewModel.tableSelectList)
            } else {
                rv_table_name.adapter.notifyDataSetChanged()
            }
        })
    }

    private fun initTableMenu(data: List<TableSelectBean>) {
        rv_table_name.layoutManager = LinearLayoutManager(this)
        val adapter = TableNameAdapter(R.layout.item_table_select_main, data)
        adapter.addFooterView(initFooterView(adapter))
        adapter.setOnItemClickListener { _, _, position ->
            if (position < data.size) {
                viewModel.changeDefaultTable(data[position].id)
            }
        }
        rv_table_name.adapter = adapter
    }

    private fun initFooterView(adapter: TableNameAdapter): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_table_add_main, rv_table_name, false)
        val tableAdd = view.findViewById<ImageButton>(R.id.nav_table_add)
        tableAdd.setOnClickListener {
            ModifyTableNameFragment.newInstance(object : ModifyTableNameFragment.TableNameChangeListener {
                override fun onFinish(editText: EditText, dialog: Dialog) {
                    if (!editText.text.toString().isEmpty()) {
                        viewModel.addBlankTable(editText.text.toString())
                        viewModel.addBlankTableInfo.observe(this@ScheduleActivity, Observer { info ->
                            if (info == "OK") {
                                Toasty.success(applicationContext, "新建成功~").show()
                                dialog.dismiss()
                            } else {
                                Toasty.success(applicationContext, "操作失败>_<").show()
                                dialog.dismiss()
                            }
                        })
                    } else {
                        Toasty.error(applicationContext, "名称不能为空哦>_<").show()
                    }
                }
            }).show(supportFragmentManager, "addTableFragment")
        }
        val tableManage = view.findViewById<ImageButton>(R.id.nav_table_manage)
        return view
    }

    private fun initCourseData(tableId: Int) {
        for (i in 1..7) {
            viewModel.getRawCourseByDay(i, tableId).observe(this, Observer { list ->
                viewModel.allCourseList[i - 1].value = list
            })
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

        tv_date.text = CourseUtils.getTodayDate()

        if (clipboardManager.primaryClip != null) {
            if (clipboardManager.primaryClip.itemCount > 0) {
                if (clipboardManager.primaryClip.getItemAt(0).text != null) {
                    val clipStr = clipboardManager.primaryClip.getItemAt(0).text.toString()
                    if (clipStr.startsWith("来自WakeUp课程表的分享：")) {
                        viewModel.tranClipboardStr(clipStr)
                        ClipboardImportFragment.newInstance().show(supportFragmentManager, "ClipboardImportFragment")
                    }
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {

    }

    private fun initNavView() {
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

    private fun initViewPage(maxWeek: Int, table: TableBean) {
        if (mAdapter == null) {
            mAdapter = SchedulePagerAdapter(supportFragmentManager)
            vp_schedule.adapter = mAdapter
            vp_schedule.offscreenPageLimit = 1
        }
        mAdapter!!.removeAll()
        for (i in 1..maxWeek) {
            mAdapter!!.addFragment(ScheduleFragment.newInstance(i))
        }
        mAdapter!!.notifyDataSetChanged()
        if (CourseUtils.countWeek(table.startDate) > 0) {
            vp_schedule.currentItem = CourseUtils.countWeek(table.startDate) - 1
        } else {
            vp_schedule.currentItem = 0
        }
    }

    private fun initEvent(currentWeek: Int) {
        sb_week.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_week.text = "第${progress + 1}周"
                try {
                    if (currentWeek > 0) {
                        if (progress + 1 == currentWeek) {
                            tv_week.text = "第${progress + 1}周"
                            tv_weekday.text = CourseUtils.getWeekday()
                            tv_date.text = CourseUtils.getTodayDate()
                        } else {
                            tv_week.text = "第${progress + 1}周"
                            tv_weekday.text = "非本周"
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

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                vp_schedule.currentItem = seekBar!!.progress
            }
        })

        ib_nav.setOnClickListener { drawerLayout.openDrawer(Gravity.START) }

        ib_import.setOnClickListener {
            ImportChooseFragment.newInstance().show(supportFragmentManager, "importDialog")
        }

        tv_weekday.setOnClickListener {
            tv_weekday.text = CourseUtils.getWeekday()
            if (currentWeek > 0) {
                vp_schedule.currentItem = currentWeek - 1
            } else {
                vp_schedule.currentItem = 0
            }
        }

        vp_schedule.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                viewModel.selectedWeek = position + 1
                try {
                    if (currentWeek > 0) {
                        if (viewModel.selectedWeek == currentWeek) {
                            tv_week.text = "第${viewModel.selectedWeek}周"
                            tv_weekday.text = CourseUtils.getWeekday()
                            tv_date.text = CourseUtils.getTodayDate()
                        } else {
                            tv_week.text = "第${viewModel.selectedWeek}周"
                            tv_weekday.text = "非本周"
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