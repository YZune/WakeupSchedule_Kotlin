package com.suda.yzune.wakeupschedule.schedule

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ShareCompat
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.UpdateFragment
import com.suda.yzune.wakeupschedule.apply_info.ApplyInfoActivity
import com.suda.yzune.wakeupschedule.base_view.BaseActivity
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import com.suda.yzune.wakeupschedule.bean.UpdateInfoBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import com.suda.yzune.wakeupschedule.intro.AboutActivity
import com.suda.yzune.wakeupschedule.intro.IntroYoungActivity
import com.suda.yzune.wakeupschedule.schedule_manage.ScheduleManageActivity
import com.suda.yzune.wakeupschedule.schedule_settings.ScheduleSettingsActivity
import com.suda.yzune.wakeupschedule.settings.SettingsActivity
import com.suda.yzune.wakeupschedule.suda_life.SudaLifeActivity
import com.suda.yzune.wakeupschedule.utils.*
import com.suda.yzune.wakeupschedule.utils.UpdateUtils.getVersionCode
import es.dmoral.toasty.Toasty
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.coroutines.delay
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import splitties.activities.start
import splitties.dimensions.dip
import splitties.resources.styledDimenPxSize
import splitties.snackbar.action
import splitties.snackbar.longSnack
import java.text.ParseException
import kotlin.math.roundToInt

class ScheduleActivity : BaseActivity() {

    private val viewModel by viewModels<ScheduleViewModel>()
    private var mAdapter: SchedulePagerAdapter? = null

    private lateinit var ui: ScheduleActivityUI
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val preLoad by lazy(LazyThreadSafetyMode.NONE) {
        getPrefer().getBoolean(Const.KEY_SCHEDULE_PRE_LOAD, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getPrefer().getBoolean(Const.KEY_HIDE_NAV_BAR, false)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        ui = ScheduleActivityUI(this)
        setContentView(ui.root)

        val json = getPrefer().getString(Const.KEY_OLD_VERSION_COURSE, "")
        if (!json.isNullOrEmpty()) {
            launch {
                try {
                    viewModel.updateFromOldVer(json)
                    Toasty.success(applicationContext, "升级成功~").show()
                } catch (e: Exception) {
                    Toasty.error(applicationContext, "出现异常>_<\n${e.message}").show()
                }
            }
        }

        bottomSheetBehavior = BottomSheetBehavior.from(ui.bottomSheet)

        ui.content.postDelayed({
            if (!getPrefer().getBoolean(Const.KEY_HAS_INTRO, false)) {
                initIntro()
            }
        }, 500)

        initView()
        initNavView()

        val openTimes = getPrefer().getInt(Const.KEY_OPEN_TIMES, 0)
        if (openTimes < 10) {
            getPrefer().edit {
                putInt(Const.KEY_OPEN_TIMES, openTimes + 1)
            }
        } else if (openTimes == 10) {
            val dialog = DonateFragment.newInstance()
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "donateDialog")
            getPrefer().edit {
                putInt(Const.KEY_OPEN_TIMES, openTimes + 1)
            }
        }

        if (!getPrefer().getBoolean(Const.KEY_HAS_COUNT, false)) {
            MyRetrofitUtils.instance.addCount(applicationContext)
        }

        if (getPrefer().getBoolean(Const.KEY_CHECK_UPDATE, true)) {
            MyRetrofitUtils.instance.getService().getUpdateInfo().enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {}

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    if (response!!.body() != null) {
                        val gson = Gson()
                        try {
                            val updateInfo = gson.fromJson<UpdateInfoBean>(response.body()!!.string(), UpdateInfoBean::class.java)
                            if (updateInfo.id > getVersionCode(this@ScheduleActivity.applicationContext)) {
                                UpdateFragment.newInstance(updateInfo).show(supportFragmentManager, "updateDialog")
                            }
                        } catch (e: Exception) {

                        }
                    }
                }
            })
        }

        viewModel.initTableSelectList().observe(this, Observer {
            if (it == null) return@Observer
            viewModel.tableSelectList.clear()
            viewModel.tableSelectList.addAll(it)
            if (ui.rvTableName.adapter == null) {
                initTableMenu(viewModel.tableSelectList)
            } else {
                ui.rvTableName.adapter?.notifyDataSetChanged()
            }
        })

        initBottomSheetAction()
        //DonateFragment.newInstance().show(supportFragmentManager, "AfterImportTipFragment")
    }

    private fun initTheme() {
        if (viewModel.table.background != "") {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            Glide.with(this)
                    .load(viewModel.table.background)
                    .override(x, y)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            Toasty.error(this@ScheduleActivity, "无法检索背景图片，可能是它为某个应用私有所致，可以尝试在文件管理器中将它移动到其他位置，或是选择其它图片", Toasty.LENGTH_LONG).show()
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .error(R.drawable.main_background_2020_1)
                    .into(ui.bg)
            Glide.with(this)
                    .load(viewModel.table.background)
                    .override((x * 0.8).toInt(), (y * 0.8).toInt())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.main_background_2020_1)
                    .into(ui.navViewStart.getHeaderView(0).findViewById(R.id.iv_header))
        } else {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            Glide.with(this)
                    .load(R.drawable.main_background_2020_1)
                    .override(x, y)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ui.bg)
            Glide.with(this)
                    .load(R.drawable.main_background_2020_1)
                    .override((x * 0.8).toInt(), (y * 0.8).toInt())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ui.navViewStart.getHeaderView(0).findViewById(R.id.iv_header))
        }

        for (i in 0 until ui.content.childCount) {
            val view = ui.content.getChildAt(i)
            when (view) {
                is AppCompatTextView -> view.setTextColor(viewModel.table.textColor)
                is AppCompatImageButton -> view.setColorFilter(viewModel.table.textColor)
            }
        }

        if (ViewUtils.judgeColorIsLight(viewModel.table.textColor)) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
        }

        viewModel.itemHeight = dip(viewModel.table.itemHeight)
    }

    private fun initTableMenu(data: MutableList<TableSelectBean>) {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val adapter = TableNameAdapter(R.layout.item_table_select_main, data)
        adapter.addChildClickViewIds(R.id.menu_setting)
        adapter.setOnItemChildClickListener { _, view, _ ->
            when (view.id) {
                R.id.menu_setting -> {
                    startActivityForResult(Intent(this,
                            ScheduleSettingsActivity::class.java).apply {
                        putExtra("tableData", viewModel.table)
                    }, Const.REQUEST_CODE_SCHEDULE_SETTING)
                }
            }
        }
        adapter.setOnItemClickListener { _, _, position ->
            if (position < data.size) {
                if (data[position].id != viewModel.table.id) {
                    launch {
                        viewModel.changeDefaultTable(data[position].id)
                        initView()
                        val list = viewModel.getScheduleWidgetIds()
                        val table = viewModel.getDefaultTable()
                        list.forEach {
                            when (it.detailType) {
                                1 -> AppWidgetUtils.refreshTodayWidget(applicationContext, appWidgetManager, it.id, table)
                            }
                        }
                    }
                }
            }
        }
        ui.rvTableName.adapter = adapter
    }

    private fun initBottomSheetAction() {
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    ui.weekScrollView.smoothScrollTo(if (viewModel.selectedWeek > 4) (viewModel.selectedWeek - 4) * dip(56) else 0, 0)
                    if (ui.weekToggleGroup.checkedButtonId != viewModel.selectedWeek) {
                        ui.weekToggleGroup.check(viewModel.selectedWeek)
                    }
                }
            }
        })
        ui.createScheduleBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.setting_schedule_name)
                    .setView(R.layout.dialog_edit_text)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.sure, null)
                    .create()
            dialog.show()
            val inputLayout = dialog.findViewById<TextInputLayout>(R.id.text_input_layout)
            val editText = dialog.findViewById<TextInputEditText>(R.id.edit_text)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val value = editText?.text
                if (value.isNullOrBlank()) {
                    inputLayout?.error = "名称不能为空哦>_<"
                } else {
                    launch {
                        try {
                            viewModel.addBlankTable(editText.text.toString())
                            Toasty.success(this@ScheduleActivity, "新建成功~").show()
                        } catch (e: Exception) {
                            Toasty.error(this@ScheduleActivity, "操作失败>_<").show()
                        }
                        dialog.dismiss()
                    }
                }
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        ui.manageScheduleBtn.setOnClickListener {
            startActivityForResult(
                    Intent(this, ScheduleManageActivity::class.java), Const.REQUEST_CODE_SCHEDULE_SETTING)
        }
        ui.changeWeekBtn.setOnClickListener {
            startActivityForResult(Intent(this,
                    ScheduleSettingsActivity::class.java).apply {
                putExtra("tableData", viewModel.table)
                putExtra("settingItem", "当前周")
            }, Const.REQUEST_CODE_SCHEDULE_SETTING)
        }
        ui.timeBtn.setOnClickListener {
            startActivityForResult(Intent(this,
                    ScheduleSettingsActivity::class.java).apply {
                putExtra("tableData", viewModel.table)
                putExtra("settingItem", "上课时间")
            }, Const.REQUEST_CODE_SCHEDULE_SETTING)
        }
        ui.changeBgBtn.setOnClickListener {
            startActivityForResult(Intent(this,
                    ScheduleSettingsActivity::class.java).apply {
                putExtra("tableData", viewModel.table)
                putExtra("settingItem", "课程表背景")
            }, Const.REQUEST_CODE_SCHEDULE_SETTING)
        }
        ui.courseBtn.setOnClickListener {
            start<ScheduleManageActivity> {
                putExtra("selectedTable", TableSelectBean(
                        id = viewModel.table.id,
                        background = viewModel.table.background,
                        tableName = viewModel.table.tableName,
                        maxWeek = viewModel.table.maxWeek,
                        nodes = viewModel.table.nodes,
                        type = viewModel.table.type
                ))
            }
        }
        ui.qaBtn.setOnClickListener {
            Utils.openUrl(this, "https://support.qq.com/embed/97617/faqs-more")
        }
    }

    private fun initIntro() {
        val builder = Tooltip.Builder(this@ScheduleActivity)
                .overlay(true)
                .maxWidth(dip(240))
                .customView(R.layout.my_tooltip, R.id.tv_tip)
        val navTooltip = builder
                .text("点这里打开左栏")
                .anchor(ui.navBtn)
                .create()
        val jumpTooltip = builder
                .text("点这里快速回到当前周")
                .anchor(ui.weekDayView)
                .create()
        val addBtnTooltip = builder
                .text("点这里手动添加课程")
                .anchor(ui.addBtn)
                .create()
        val importTooltip = builder
                .text("点这里导入课表")
                .anchor(ui.importBtn)
                .create()
        val shareTooltip = builder
                .text("点这里导出、分享课表")
                .anchor(ui.shareBtn)
                .create()
        val moreTooltip = builder
                .text("点这里查看更多设置")
                .anchor(ui.moreBtn)
                .create()
        navTooltip.doOnHidden {
            jumpTooltip.doOnHidden {
                addBtnTooltip.doOnHidden {
                    importTooltip.doOnHidden {
                        shareTooltip.doOnHidden {
                            moreTooltip.doOnHidden {
                                getPrefer().edit {
                                    putBoolean(Const.KEY_HAS_INTRO, true)
                                }
                                showBottomSheetDialog()
                            }.show(ui.content, Tooltip.Gravity.LEFT)
                            moreTooltip.contentView?.findViewById<TextView>(R.id.btn_next)?.apply {
                                text = "完成教程"
                                setOnClickListener {
                                    moreTooltip.hide()
                                }
                            }
                        }.show(ui.content, Tooltip.Gravity.LEFT)
                        shareTooltip.contentView?.findViewById<TextView>(R.id.btn_next)?.setOnClickListener {
                            shareTooltip.hide()
                        }
                    }.show(ui.content, Tooltip.Gravity.LEFT)
                    importTooltip.contentView?.findViewById<TextView>(R.id.btn_next)?.setOnClickListener {
                        importTooltip.hide()
                    }
                }.show(ui.content, Tooltip.Gravity.BOTTOM)
                addBtnTooltip.contentView?.findViewById<TextView>(R.id.btn_next)?.setOnClickListener {
                    addBtnTooltip.hide()
                }
            }.show(ui.content, Tooltip.Gravity.BOTTOM)
            jumpTooltip.contentView?.findViewById<TextView>(R.id.btn_next)?.setOnClickListener {
                jumpTooltip.hide()
            }
        }.show(ui.content, Tooltip.Gravity.RIGHT)
        navTooltip.contentView?.findViewById<TextView>(R.id.btn_next)?.setOnClickListener {
            navTooltip.hide()
        }
    }

    override fun onStart() {
        super.onStart()
        ui.dateView.text = CourseUtils.getTodayDate()
    }

    private fun initNavView() {
        ui.navViewStart.menu.findItem(R.id.nav_suda).isVisible = getPrefer().getBoolean(Const.KEY_SHOW_SUDA_LIFE, true)
        ui.navViewStart.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_setting -> {
                    ui.drawerLayout.closeDrawer(GravityCompat.START)
                    ui.drawerLayout.postDelayed({
                        startActivityForResult(Intent(this, SettingsActivity::class.java), Const.REQUEST_CODE_SCHEDULE_SETTING)
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_explore -> {
                    ui.drawerLayout.closeDrawer(GravityCompat.START)
                    ui.drawerLayout.postDelayed({
                        start<ApplyInfoActivity>()
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_course -> {
                    ui.drawerLayout.closeDrawer(GravityCompat.START)
                    ui.drawerLayout.postDelayed({
                        start<ScheduleManageActivity> {
                            putExtra("selectedTable", TableSelectBean(
                                    id = viewModel.table.id,
                                    background = viewModel.table.background,
                                    tableName = viewModel.table.tableName,
                                    maxWeek = viewModel.table.maxWeek,
                                    nodes = viewModel.table.nodes,
                                    type = viewModel.table.type
                            ))
                        }
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_feedback -> {
                    ui.drawerLayout.closeDrawer(GravityCompat.START)
                    ui.drawerLayout.postDelayed({
                        Utils.openUrl(this, "https://support.qq.com/product/97617")
                        Toasty.info(this, "吐槽后隔天记得回来看看回复哦~", Toasty.LENGTH_LONG).show()
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_about -> {
                    ui.drawerLayout.closeDrawer(GravityCompat.START)
                    ui.drawerLayout.postDelayed({
                        start<AboutActivity>()
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_young -> {
                    ui.drawerLayout.closeDrawer(GravityCompat.START)
                    ui.drawerLayout.postDelayed({
                        start<IntroYoungActivity>()
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_empty_room -> {
                    ui.drawerLayout.closeDrawer(GravityCompat.START)
                    ui.drawerLayout.postDelayed({
                        start<SudaLifeActivity> {
                            putExtra("type", "空教室")
                        }
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_bathroom -> {
                    ui.drawerLayout.closeDrawer(GravityCompat.START)
                    ui.drawerLayout.postDelayed({
                        start<SudaLifeActivity> {
                            putExtra("type", "澡堂")
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
            mAdapter = SchedulePagerAdapter(maxWeek, preLoad, supportFragmentManager)
            ui.viewPager.adapter = mAdapter
            ui.viewPager.offscreenPageLimit = 1
        }
        mAdapter!!.maxWeek = maxWeek
        mAdapter!!.notifyDataSetChanged()
        if (CourseUtils.countWeek(table.startDate, table.sundayFirst) > 0) {
            ui.viewPager.currentItem = CourseUtils.countWeek(table.startDate, table.sundayFirst) - 1
        } else {
            ui.viewPager.currentItem = 0
        }
    }

    private fun initEvent() {
        ui.addBtn.setOnClickListener {
            start<AddCourseActivity> {
                putExtra("tableId", viewModel.table.id)
                putExtra("maxWeek", viewModel.table.maxWeek)
                putExtra("nodes", viewModel.table.nodes)
                putExtra("id", -1)
            }
        }

        ui.moreBtn.setOnClickListener {
            showBottomSheetDialog()
            //ui.drawerLayout.openDrawer(Gravity.END)
        }

        ui.bottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        ui.weekToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                ui.viewPager.currentItem = checkedId - 1
            }
        }

        ui.navBtn.setOnClickListener { ui.drawerLayout.openDrawer(GravityCompat.START) }

        ui.shareBtn.setOnClickListener {
            ExportSettingsFragment().show(supportFragmentManager, null)
        }

        ui.importBtn.setOnClickListener {
            ImportChooseFragment().show(supportFragmentManager, "importDialog")
        }

        ui.weekDayView.setOnClickListener {
            ui.weekDayView.text = CourseUtils.getWeekday()
            if (viewModel.currentWeek > 0) {
                ui.viewPager.currentItem = viewModel.currentWeek - 1
            } else {
                ui.viewPager.currentItem = 0
            }
        }

        ui.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                viewModel.selectedWeek = position + 1
                try {
                    if (viewModel.currentWeek > 0) {
                        if (viewModel.selectedWeek == viewModel.currentWeek) {
                            ui.weekView.text = "第${viewModel.selectedWeek}周"
                            ui.weekDayView.text = CourseUtils.getWeekday()
                        } else {
                            ui.weekView.text = "第${viewModel.selectedWeek}周"
                            ui.weekDayView.text = "非本周"
                        }
                    } else {
                        ui.weekView.text = "第${viewModel.selectedWeek}周"
                        ui.weekDayView.text = "还没有开学哦"
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

    private fun showBottomSheetDialog() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

    }

    private fun initView() {
        launch {
            viewModel.table = viewModel.getDefaultTable()
            viewModel.currentWeek = CourseUtils.countWeek(viewModel.table.startDate, viewModel.table.sundayFirst)
            viewModel.selectedWeek = viewModel.currentWeek
            if (viewModel.currentWeek > 0) {
                if (viewModel.currentWeek <= viewModel.table.maxWeek) {
                    ui.weekView.text = "第${viewModel.currentWeek}周"
                } else {
                    ui.weekView.text = "当前周已超出设定范围"
                    MaterialAlertDialogBuilder(this@ScheduleActivity)
                            .setTitle("提示")
                            .setMessage("发现当前周已超出设定的周数范围，是否去设置修改「当前周」或「开学日期」？")
                            .setPositiveButton("打开设置") { _, _ ->
                                startActivityForResult(Intent(this@ScheduleActivity,
                                        ScheduleSettingsActivity::class.java).apply {
                                    putExtra("tableData", viewModel.table)
                                }, Const.REQUEST_CODE_SCHEDULE_SETTING)
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .show()
                }
            } else {
                ui.weekView.text = "还没有开学哦"
            }

            ui.weekToggleGroup.removeAllViews()
            ui.weekToggleGroup.clearChecked()
            for (i in 1..viewModel.table.maxWeek) {
                ui.weekToggleGroup.addView(ui.createOutlineButton().apply {
                    id = i
                    text = i.toString()
                    textSize = 12f
                }, dip(48), dip(48))
            }

            launch {
                delay(1000)
                if (ui.weekToggleGroup.checkedButtonId != viewModel.selectedWeek) {
                    ui.weekToggleGroup.check(viewModel.selectedWeek)
                }
                ui.weekScrollView.smoothScrollTo(if (viewModel.selectedWeek > 4) (viewModel.selectedWeek - 4) * dip(56) else 0, 0)
            }

            ui.weekDayView.text = CourseUtils.getWeekday()

            initTheme()

            viewModel.timeList = viewModel.getTimeList(viewModel.table.timeTable)

            viewModel.alphaInt = (255 * (viewModel.table.itemAlpha.toFloat() / 100)).roundToInt()

            initViewPage(viewModel.table.maxWeek, viewModel.table)

            initEvent()

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
        if (resultCode != RESULT_OK) {
            when (requestCode) {
                Const.REQUEST_CODE_EXPORT -> {
                    ui.content.longSnack("导出是否遇到了问题？") {
                        action("查看教程") {
                            Utils.openUrl(this@ScheduleActivity, "https://support.qq.com/embed/phone/97617/faqs/59883")
                        }
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        when (requestCode) {
            Const.REQUEST_CODE_SCHEDULE_SETTING -> initView()
            Const.REQUEST_CODE_IMPORT -> {
                showBottomSheetDialog()
                //ui.drawerLayout.openDrawer(Gravity.END)
                MaterialAlertDialogBuilder(this)
                        .setTitle("温馨提示")
                        .setView(AppCompatTextView(this).apply {
                            text = ViewUtils.getHtmlSpannedString("记得<b><font color='#fa6278'>仔细检查</font></b>有没有少课、课程信息对不对哦，不要到时候<b><font color='#fa6278'>一不小心就翘课</font></b>啦<br>解析算法不是100%可靠的哦<br>但会朝这个方向努力")
                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            val space = styledDimenPxSize(R.attr.dialogPreferredPadding)
                            setPadding(space, dip(8), space, 0)
                        })
                        .setCancelable(false)
                        .setPositiveButton("我知道啦", null)
                        .show()
            }
            Const.REQUEST_CODE_EXPORT -> {
                val uri = data?.data
                launch {
                    try {
                        viewModel.exportData(uri)
                        showShareDialog("分享课程文件", uri!!)
                    } catch (e: Exception) {
                        Toasty.error(this@ScheduleActivity, "导出失败>_<${e.message}")
                    }
                }
            }
            Const.REQUEST_CODE_EXPORT_ICS -> {
                val uri = data?.data
                launch {
                    try {
                        viewModel.exportICS(uri)
                        showShareDialog("分享日历文件", uri!!)
                    } catch (e: Exception) {
                        Toasty.error(this@ScheduleActivity, "导出失败>_<${e.message}")
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showShareDialog(title: String, uri: Uri) {
        MaterialAlertDialogBuilder(this)
                .setTitle("分享")
                .setMessage("成功导出至你指定的路径啦，是否还要分享出去呢？")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton("分享") { _, _ ->
                    val shareIntent = ShareCompat.IntentBuilder.from(this)
                            .setChooserTitle(title)
                            .setStream(uri)
                            .setType("*/*")
                            .createChooserIntent()
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(shareIntent)
                }
                .setCancelable(false)
                .show()
    }

    override fun onBackPressed() {
        when {
            ui.drawerLayout.isDrawerOpen(GravityCompat.START) -> ui.drawerLayout.closeDrawer(GravityCompat.START)
            ui.drawerLayout.isDrawerOpen(GravityCompat.END) -> ui.drawerLayout.closeDrawer(GravityCompat.END)
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            else -> super.onBackPressed()
        }
    }

    override fun onDestroy() {
        ui.viewPager.clearOnPageChangeListeners()
        ui.weekToggleGroup.clearOnButtonCheckedListeners()
        AppWidgetUtils.updateWidget(applicationContext)
        super.onDestroy()
    }

}