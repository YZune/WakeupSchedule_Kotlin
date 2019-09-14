package com.suda.yzune.wakeupschedule.schedule_settings

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcel
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.drakeet.multitype.MultiTypeAdapter
import com.suda.yzune.wakeupschedule.BuildConfig
import com.suda.yzune.wakeupschedule.DonateActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseListActivity
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.schedule.DonateFragment
import com.suda.yzune.wakeupschedule.settings.AdvancedSettingsActivity
import com.suda.yzune.wakeupschedule.settings.TimeSettingsActivity
import com.suda.yzune.wakeupschedule.settings.bean.*
import com.suda.yzune.wakeupschedule.settings.view_binder.*
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.widget.ModifyTableNameFragment
import com.suda.yzune.wakeupschedule.widget.colorpicker.ColorPickerFragment
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

private const val TITLE_COLOR = 1
private const val COURSE_TEXT_COLOR = 2
private const val STROKE_COLOR = 3
private const val WIDGET_TITLE_COLOR = 4
private const val WIDGET_COURSE_TEXT_COLOR = 5
private const val WIDGET_STROKE_COLOR = 6

class ScheduleSettingsActivity : BaseListActivity(), ColorPickerFragment.ColorPickerDialogListener {

    override fun onColorSelected(dialogId: Int, color: Int) {
        when (dialogId) {
            TITLE_COLOR -> viewModel.table.textColor = color
            COURSE_TEXT_COLOR -> viewModel.table.courseTextColor = color
            STROKE_COLOR -> viewModel.table.strokeColor = color
            WIDGET_TITLE_COLOR -> viewModel.table.widgetTextColor = color
            WIDGET_COURSE_TEXT_COLOR -> viewModel.table.widgetCourseTextColor = color
            WIDGET_STROKE_COLOR -> viewModel.table.widgetStrokeColor = color
        }
    }

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        val iconFont = ResourcesCompat.getFont(this, R.font.iconfont)
        tvButton.typeface = iconFont
        tvButton.textSize = 20f
        tvButton.text = "\uE6C2"
        if (BuildConfig.CHANNEL == "google" || BuildConfig.CHANNEL == "huawei") {
            tvButton.setOnClickListener {
                val dialog = DonateFragment.newInstance()
                dialog.show(supportFragmentManager, "donateDialog")
            }
        } else {
            tvButton.setOnClickListener {
                startActivity<DonateActivity>()
            }
        }
        return tvButton
    }

    override fun onPause() {
        super.onPause()
        Toasty.info(applicationContext, "对小部件的编辑需要按「返回键」退出设置页面才能生效哦", Toast.LENGTH_LONG).show()
    }

    private lateinit var viewModel: ScheduleSettingsViewModel
    private val mAdapter: MultiTypeAdapter = MultiTypeAdapter()
    private val REQUEST_CODE_CHOOSE_BG = 23
    private val REQUEST_CODE_CHOOSE_TABLE = 21
    private val allItems = mutableListOf<Any>()
    private val showItems = mutableListOf<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        showSearch = true
        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showItems.clear()
                if (s.isNullOrBlank() || s.isEmpty()) {
                    showItems.addAll(allItems)
                } else {
                    showItems.add(CategoryItem("搜索结果", true))
                    showItems.addAll(allItems.filter {
                        val k = (it as BaseItem).keyWords
                        k?.contains(s.toString()) ?: false
                    })
                }
                mRecyclerView.adapter?.notifyDataSetChanged()
                if (showItems.size == 1) {
                    mRecyclerView.longSnackbar("找不到哦，换个关键词试试看，或者请仔细找找啦，一般都能找到的。")
                }
            }
        }
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScheduleSettingsViewModel::class.java)
        viewModel.table = intent.extras!!.getParcelable<TableBean>("tableData") as TableBean

        onAdapterCreated(mAdapter)

        onItemsCreated(allItems)
        showItems.addAll(allItems)
        mAdapter.items = showItems
        mRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
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

    private fun onItemsCreated(items: MutableList<Any>) {
        items.add(CategoryItem("课程数据", true))
        items.add(HorizontalItem("课表名称", viewModel.table.tableName, listOf("名称", "名字", "名", "课表")))
        items.add(HorizontalItem("学期开始日期", viewModel.table.startDate, listOf("学期", "周", "日期", "开学", "开始", "时间")))
        items.add(SeekBarItem("一天课程节数", viewModel.table.nodes, 4, 30, "节", listOf("节数", "数量", "数")))
        items.add(SeekBarItem("学期周数", viewModel.table.maxWeek, 10, 30, "周", listOf("学期", "周", "时间")))
        items.add(HorizontalItem("上课时间", "", listOf("时间")))
        items.add(SwitchItem("周日为每周第一天", viewModel.table.sundayFirst, listOf("周日", "第一天", "起始", "星期天", "天")))
        items.add(SwitchItem("显示周六", viewModel.table.showSat, listOf("周六", "显示", "星期六", "六")))
        items.add(SwitchItem("显示周日", viewModel.table.showSun, listOf("周日", "显示", "星期日", "日", "星期天", "周天")))

        items.add(CategoryItem("课表外观", false))
        items.add(SwitchItem("在格子内显示上课时间", viewModel.table.showTime, listOf("时间", "显示", "格子", "上课时间")))
        items.add(VerticalItem("课程表背景", "长按可以恢复默认哦~", keys = listOf("背景", "显示", "图片")))
        items.add(VerticalItem("界面文字颜色", "指标题等字体的颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)", keys = listOf("颜色", "显示", "文字", "文字颜色")))
        items.add(VerticalItem("课程文字颜色", "指课程格子内的颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)", keys = listOf("颜色", "显示", "文字", "文字颜色")))
        items.add(VerticalItem("格子边框颜色", "将不透明度调到最低就可以隐藏边框了哦~", keys = listOf("边框", "显示", "边框颜色", "格子", "边")))
        items.add(SeekBarItem("课程格子高度", viewModel.table.itemHeight, 32, 96, "dp", listOf("格子", "高度", "格子高度", "显示")))
        items.add(SeekBarItem("课程格子不透明度", viewModel.table.itemAlpha, 0, 100, "%", listOf("格子", "透明", "格子高度", "显示")))
        items.add(SeekBarItem("课程显示文字大小", viewModel.table.itemTextSize, 8, 16, "sp", listOf("文字", "大小", "文字大小")))
        items.add(SwitchItem("显示非本周课程", viewModel.table.showOtherWeekCourse, listOf("非本周")))

        items.add(CategoryItem("桌面小部件外观", false))
        items.add(SeekBarItem("小部件格子高度", viewModel.table.widgetItemHeight, 32, 96, "dp", listOf("格子", "高度", "格子高度", "显示", "小部件", "小", "插件", "桌面")))
        items.add(SeekBarItem("小部件格子不透明度", viewModel.table.widgetItemAlpha, 0, 100, "%", listOf("格子", "透明", "格子高度", "显示", "小部件", "小", "插件", "桌面")))
        items.add(SeekBarItem("小部件显示文字大小", viewModel.table.widgetItemTextSize, 8, 16, "sp", listOf("文字", "大小", "文字大小", "小部件", "小", "插件", "桌面")))
        items.add(VerticalItem("小部件标题颜色", "指标题等字体的颜色\n对于日视图则是全部文字的颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)", keys = listOf("颜色", "显示", "文字", "文字颜色", "小部件", "小", "插件", "桌面")))
        items.add(VerticalItem("小部件课程颜色", "指课程格子内的文字颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)", keys = listOf("颜色", "显示", "文字", "文字颜色", "小部件", "小", "插件", "桌面")))
        items.add(VerticalItem("小部件格子边框颜色", "将不透明度调到最低就可以隐藏边框了哦~", keys = listOf("边框", "显示", "边框颜色", "格子", "边", "小部件", "小", "插件", "桌面")))

        items.add(CategoryItem("高级", false))
        when (BuildConfig.CHANNEL) {
            "google" -> items.add(VerticalItem("看看都有哪些高级功能", "如果想支持一下社团和开发者\n请去支付宝18862196504\n高级功能会持续更新~\n采用诚信授权模式ヾ(=･ω･=)o", keys = listOf("高级")))
            "huawei" -> items.add(VerticalItem("看看都有哪些高级功能", "高级功能会持续更新~", keys = listOf("高级")))
            else -> items.add(VerticalItem("解锁高级功能", "解锁赞助一下社团和开发者ヾ(=･ω･=)o\n高级功能会持续更新~\n采用诚信授权模式", keys = listOf("高级")))
        }
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
            "小部件显示文字大小" -> viewModel.table.widgetItemTextSize = value + item.min
        }
        item.valueInt = value + item.min
    }

    private fun onHorizontalItemClick(item: HorizontalItem) {
        when (item.title) {
            "课表名称" -> {
                ModifyTableNameFragment.newInstance(object : ModifyTableNameFragment.TableNameChangeListener {
                    override fun writeToParcel(dest: Parcel?, flags: Int) {

                    }

                    override fun describeContents(): Int {
                        return 0
                    }

                    override fun onFinish(editText: EditText, dialog: Dialog) {
                        if (editText.text.toString().isNotEmpty()) {
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
                    val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    try {
                        startActivityForResult(intent, REQUEST_CODE_CHOOSE_BG)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
//                    Matisse.from(this)
//                            .choose(MimeType.ofImage())
//                            .countable(true)
//                            .maxSelectable(1)
//                            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
//                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                            .thumbnailScale(0.85f)
//                            .imageEngine(GlideAppEngine())
//                            .forResult(REQUEST_CODE_CHOOSE_BG)
                }
            }
            "界面文字颜色" -> {
                buildColorPickerDialogBuilder(viewModel.table.textColor, TITLE_COLOR)
            }
            "课程文字颜色" -> {
                buildColorPickerDialogBuilder(viewModel.table.courseTextColor, COURSE_TEXT_COLOR)
            }
            "格子边框颜色" -> {
                buildColorPickerDialogBuilder(viewModel.table.strokeColor, STROKE_COLOR)
            }
            "小部件标题颜色" -> {
                buildColorPickerDialogBuilder(viewModel.table.widgetTextColor, WIDGET_TITLE_COLOR)
            }
            "小部件课程颜色" -> {
                buildColorPickerDialogBuilder(viewModel.table.widgetCourseTextColor, WIDGET_COURSE_TEXT_COLOR)
            }
            "小部件格子边框颜色" -> {
                buildColorPickerDialogBuilder(viewModel.table.widgetStrokeColor, WIDGET_STROKE_COLOR)
            }
            "解锁高级功能" -> {
                startActivity<AdvancedSettingsActivity>()
            }
            "看看都有哪些高级功能" -> {
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

    private fun buildColorPickerDialogBuilder(color: Int, id: Int) {
        ColorPickerFragment.newBuilder()
                .setShowAlphaSlider(true)
                .setColor(color)
                .setDialogId(id)
                .show(this)
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
                    val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    try {
                        startActivityForResult(intent, REQUEST_CODE_CHOOSE_BG)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
//                    Matisse.from(this)
//                            .choose(MimeType.ofImage())
//                            .countable(true)
//                            .maxSelectable(1)
//                            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
//                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                            .thumbnailScale(0.85f)
//                            .imageEngine(GlideAppEngine())
//                            .forResult(REQUEST_CODE_CHOOSE_BG)

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
            //viewModel.table.background = Matisse.obtainResult(data)[0].toString()
            val uri = data?.data
            if (uri != null) {
                viewModel.table.background = uri.toString()
            }
        }
        if (requestCode == REQUEST_CODE_CHOOSE_TABLE && resultCode == RESULT_OK) {
            viewModel.table.timeTable = data!!.getIntExtra("selectedId", 1)
        }
    }

    override fun onBackPressed() {
        launch {
            AppWidgetUtils.updateWidget(applicationContext)
            val list = withContext(Dispatchers.IO) {
                viewModel.saveSettings()
                viewModel.getScheduleWidgetIds()
            }
            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
            list.forEach {
                when (it.detailType) {
                    0 -> {
                        if (it.info == viewModel.table.id.toString()) {
                            AppWidgetUtils.refreshScheduleWidget(applicationContext, appWidgetManager, it.id, viewModel.table)
                        }
                    }
                    1 -> AppWidgetUtils.refreshTodayWidget(applicationContext, appWidgetManager, it.id, viewModel.table, false)
                }
            }
            setResult(RESULT_OK)
            finish()
        }
    }
}
