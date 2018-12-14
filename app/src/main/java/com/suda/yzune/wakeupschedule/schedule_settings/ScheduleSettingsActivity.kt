package com.suda.yzune.wakeupschedule.schedule_settings

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcel
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.suda.yzune.wakeupschedule.DonateActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseListActivity
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.settings.AdvancedSettingsActivity
import com.suda.yzune.wakeupschedule.settings.TimeSettingsActivity
import com.suda.yzune.wakeupschedule.settings.bean.*
import com.suda.yzune.wakeupschedule.settings.view_binder.*
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.GlideAppEngine
import com.suda.yzune.wakeupschedule.widget.ModifyTableNameFragment
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.textColorResource

class ScheduleSettingsActivity : BaseListActivity() {

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        tvButton.text = "捐赠"
        tvButton.textColorResource = R.color.colorAccent
        tvButton.setOnClickListener {
            startActivity<DonateActivity>()
        }
        return tvButton
    }

    private lateinit var viewModel: ScheduleSettingsViewModel
    private val mAdapter: MultiTypeAdapter = MultiTypeAdapter()
    private val REQUEST_CODE_CHOOSE_BG = 23
    private val REQUEST_CODE_CHOOSE_TABLE = 21

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ScheduleSettingsViewModel::class.java)
        viewModel.table = intent.extras!!.getParcelable("tableData") as TableBean

        onAdapterCreated(mAdapter)
        val items = Items()
        onItemsCreated(items)
        mAdapter.items = items
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        viewModel.termStartList = viewModel.table.startDate.split("-")
        viewModel.mYear = Integer.parseInt(viewModel.termStartList[0])
        viewModel.mMonth = Integer.parseInt(viewModel.termStartList[1])
        viewModel.mDay = Integer.parseInt(viewModel.termStartList[2])
    }

    private fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(CategoryItem::class, CategoryItemViewBinder())
        adapter.register(SeekBarItem::class, SeekBarItemViewBinder { item, value -> onSeekBarValueChange(item, value) })
        adapter.register(HorizontalItem::class, HorizontalItemViewBinder { onHorizontalItemClick(it) })
        adapter.register(VerticalItem::class, VerticalItemViewBinder(
                { onVerticalItemClick(it) },
                { onVerticalItemLongClick(it) }
        ))
        adapter.register(SwitchItem::class, SwitchItemViewBinder { item, isCheck -> onSwitchItemCheckChange(item, isCheck) })
    }

    private fun onItemsCreated(items: Items) {
        items.add(CategoryItem("课程数据", true))
        items.add(HorizontalItem("课表名称", viewModel.table.tableName))
        items.add(HorizontalItem("学期开始日期", viewModel.table.startDate))
        items.add(SeekBarItem("一天课程节数", viewModel.table.nodes, 4, 20, "节"))
        items.add(SeekBarItem("学期周数", viewModel.table.maxWeek, 10, 30, "周"))
        items.add(HorizontalItem("上课时间", ""))
        items.add(SwitchItem("周日为每周第一天", viewModel.table.sundayFirst))
        items.add(SwitchItem("显示周六", viewModel.table.showSat))
        items.add(SwitchItem("显示周日", viewModel.table.showSun))

        items.add(CategoryItem("课表外观", false))
        items.add(SwitchItem("在格子内显示上课时间", viewModel.table.showTime))
        items.add(VerticalItem("课程表背景", "长按可以恢复默认哦~"))
        items.add(VerticalItem("界面文字颜色", "指标题等字体的颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)"))
        items.add(VerticalItem("课程文字颜色", "指课程格子内的颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)"))
        items.add(VerticalItem("格子边框颜色", "将不透明度调到最低就可以隐藏边框了哦~"))
        items.add(SeekBarItem("课程格子高度", viewModel.table.itemHeight, 32, 96, "dp"))
        items.add(SeekBarItem("课程格子不透明度", viewModel.table.itemAlpha, 0, 100, "%"))
        items.add(SeekBarItem("课程显示文字大小", viewModel.table.itemTextSize, 11, 16, "sp"))
        items.add(SwitchItem("显示非本周课程", viewModel.table.showOtherWeekCourse))

        items.add(CategoryItem("桌面小部件外观", false))
        items.add(SeekBarItem("小部件格子高度", viewModel.table.widgetItemHeight, 32, 96, "dp"))
        items.add(SeekBarItem("小部件格子不透明度", viewModel.table.widgetItemAlpha, 0, 100, "%"))
        items.add(SeekBarItem("显示文字大小", viewModel.table.widgetItemTextSize, 11, 16, "sp"))
        items.add(VerticalItem("小部件标题颜色", "指标题等字体的颜色\n对于日视图则是全部文字的颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)"))
        items.add(VerticalItem("小部件课程颜色", "指课程格子内的颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)"))
        items.add(VerticalItem("小部件格子边框颜色", "将不透明度调到最低就可以隐藏边框了哦~"))

        items.add(CategoryItem("高级", false))
        items.add(VerticalItem("解锁高级功能", "解锁赞助一下社团和开发者ヾ(=･ω･=)o\n高级功能会持续更新~\n采用诚信授权模式"))
    }

    private fun onSwitchItemCheckChange(item: SwitchItem, isChecked: Boolean) {
        when (item.title) {
            "周日为每周第一天" -> viewModel.table.sundayFirst = isChecked
            "显示周六" -> viewModel.table.showSat = isChecked
            "显示周日" -> viewModel.table.showSun = isChecked
            "在格子内显示上课时间" -> viewModel.table.showTime = isChecked
            "显示非本周课程" -> viewModel.table.showOtherWeekCourse = isChecked
        }
        item.checked = isChecked
    }

    private fun onSeekBarValueChange(item: SeekBarItem, value: Int) {
        when (item.title) {
            "一天课程节数" -> viewModel.table.nodes = value + item.min
            "学期周数" -> viewModel.table.maxWeek = value + item.min
            "课程格子高度" -> viewModel.table.itemHeight = value + item.min
            "课程格子不透明度" -> viewModel.table.itemAlpha = value + item.min
            "课程显示文字大小" -> viewModel.table.itemTextSize = value + item.min
            "小部件格子高度" -> viewModel.table.widgetItemHeight = value + item.min
            "小部件格子不透明度" -> viewModel.table.widgetItemAlpha = value + item.min
            "显示文字大小" -> viewModel.table.widgetItemTextSize = value + item.min
        }
        item.valueInt = value + item.min
    }

    private fun onHorizontalItemClick(item: HorizontalItem) {
        HorizontalItem("课表名称", viewModel.table.tableName)
        HorizontalItem("学期开始日期", viewModel.table.startDate)
        HorizontalItem("上课时间", "")
        when (item.title) {
            "课表名称" -> {
                ModifyTableNameFragment.newInstance(object : ModifyTableNameFragment.TableNameChangeListener {
                    override fun writeToParcel(dest: Parcel?, flags: Int) {

                    }

                    override fun describeContents(): Int {
                        return 0
                    }

                    override fun onFinish(editText: EditText, dialog: Dialog) {
                        if (!editText.text.toString().isEmpty()) {
                            viewModel.table.tableName = editText.text.toString()
                            item.value = editText.text.toString()
                            mRecyclerView.itemAnimator?.changeDuration = 250
                            mAdapter.notifyItemChanged(1)
                            dialog.dismiss()
                        } else {
                            Toasty.error(applicationContext, "名称不能为空哦>_<").show()
                        }
                    }
                }, viewModel.table.tableName).show(supportFragmentManager, "addTableFragment")
            }
            "学期开始日期" -> {
                DatePickerDialog(this, mDateListener, viewModel.mYear, viewModel.mMonth - 1, viewModel.mDay).show()
                Toasty.success(applicationContext, "为了周数计算准确，建议选择周一哦", Toast.LENGTH_LONG).show()
            }
            "上课时间" -> {
                startActivityForResult<TimeSettingsActivity>(REQUEST_CODE_CHOOSE_TABLE,
                        "selectedId" to viewModel.table.timeTable)
            }
        }
    }

    private fun onVerticalItemClick(item: VerticalItem) {
        when (item.title) {
            "课程表背景" -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                } else {
                    Matisse.from(this)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(1)
                            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(GlideAppEngine())
                            .forResult(REQUEST_CODE_CHOOSE_BG)
                }
            }
            "界面文字颜色" -> {
                buildColorPickerDialogBuilder()
                        .initialColor(viewModel.table.textColor)
                        .setPositiveButton("确定") { _, colorInt, _ ->
                            viewModel.table.textColor = colorInt
                        }
                        .build()
                        .show()
            }
            "课程文字颜色" -> {
                buildColorPickerDialogBuilder()
                        .initialColor(viewModel.table.courseTextColor)
                        .setPositiveButton("确定") { _, colorInt, _ ->
                            viewModel.table.courseTextColor = colorInt
                        }
                        .build()
                        .show()
            }
            "格子边框颜色" -> {
                buildColorPickerDialogBuilder()
                        .initialColor(viewModel.table.strokeColor)
                        .setPositiveButton("确定") { _, colorInt, _ ->
                            viewModel.table.strokeColor = colorInt
                        }
                        .build()
                        .show()
            }

            "小部件标题颜色" -> {
                buildColorPickerDialogBuilder()
                        .initialColor(viewModel.table.widgetTextColor)
                        .setPositiveButton("确定") { _, colorInt, _ ->
                            viewModel.table.widgetTextColor = colorInt
                        }
                        .build()
                        .show()
            }
            "小部件课程颜色" -> {
                buildColorPickerDialogBuilder()
                        .initialColor(viewModel.table.widgetCourseTextColor)
                        .setPositiveButton("确定") { _, colorInt, _ ->
                            viewModel.table.widgetCourseTextColor = colorInt
                        }
                        .build()
                        .show()
            }
            "小部件格子边框颜色" -> {
                buildColorPickerDialogBuilder()
                        .initialColor(viewModel.table.widgetStrokeColor)
                        .setPositiveButton("确定") { _, colorInt, _ ->
                            viewModel.table.widgetStrokeColor = colorInt
                        }
                        .build()
                        .show()
            }
            "解锁高级功能" -> {
                startActivity<AdvancedSettingsActivity>()
            }
        }
    }

    private fun onVerticalItemLongClick(item: VerticalItem): Boolean {
        return when (item.title) {
            "课程表背景" -> {
                viewModel.table.background = ""
                Toasty.success(applicationContext, "恢复默认壁纸成功~").show()
                true
            }
            else -> false
        }
    }

    private fun buildColorPickerDialogBuilder(): ColorPickerDialogBuilder {
        return ColorPickerDialogBuilder
                .with(this)
                .setTitle("选取颜色")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setNegativeButton("取消") { _, _ -> }
    }

    private val mDateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        viewModel.mYear = year
        viewModel.mMonth = monthOfYear + 1
        viewModel.mDay = dayOfMonth
        val mDate = "${viewModel.mYear}-${viewModel.mMonth}-${viewModel.mDay}"
        (mAdapter.items[2] as HorizontalItem).value = mDate
        mRecyclerView.itemAnimator?.changeDuration = 250
        mAdapter.notifyItemChanged(2)
        viewModel.table.startDate = mDate
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Matisse.from(this)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(1)
                            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(GlideAppEngine())
                            .forResult(REQUEST_CODE_CHOOSE_BG)

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toasty.error(this, "你取消了授权，无法更换背景", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_BG && resultCode == RESULT_OK) {
            viewModel.table.background = Matisse.obtainResult(data)[0].toString()
        }
        if (requestCode == REQUEST_CODE_CHOOSE_TABLE && resultCode == RESULT_OK) {
            viewModel.table.timeTable = data!!.getIntExtra("selectedId", 1)
        }
    }

    override fun onBackPressed() {
        launch {
            AppWidgetUtils.updateWidget(applicationContext)
            val list = async(Dispatchers.IO) {
                viewModel.saveSettings()
                viewModel.getScheduleWidgetIds()
            }.await()
            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
            list.forEach {
                when (it.detailType) {
                    0 -> AppWidgetUtils.refreshScheduleWidget(applicationContext, appWidgetManager, it.id, viewModel.table)
                    1 -> AppWidgetUtils.refreshTodayWidget(applicationContext, appWidgetManager, it.id, viewModel.table)
                }
            }
            setResult(RESULT_OK)
            finish()
        }
    }
}
