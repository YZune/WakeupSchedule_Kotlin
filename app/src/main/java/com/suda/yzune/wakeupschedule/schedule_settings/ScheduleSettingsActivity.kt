package com.suda.yzune.wakeupschedule.schedule_settings

import android.app.DatePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.suda.yzune.wakeupschedule.BuildConfig
import com.suda.yzune.wakeupschedule.DonateActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseListActivity
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import com.suda.yzune.wakeupschedule.schedule.DonateFragment
import com.suda.yzune.wakeupschedule.schedule_manage.ScheduleManageActivity
import com.suda.yzune.wakeupschedule.settings.AdvancedSettingsActivity
import com.suda.yzune.wakeupschedule.settings.SettingItemAdapter
import com.suda.yzune.wakeupschedule.settings.TimeSettingsActivity
import com.suda.yzune.wakeupschedule.settings.items.*
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.widget.colorpicker.ColorPickerFragment
import es.dmoral.toasty.Toasty
import splitties.activities.start
import splitties.dimensions.dip
import splitties.snackbar.longSnack

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

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        val iconFont = ResourcesCompat.getFont(this, R.font.iconfont)
        tvButton.typeface = iconFont
        tvButton.textSize = 20f
        tvButton.text = getString(R.string.icon_heart)
        if (BuildConfig.CHANNEL == "google" || BuildConfig.CHANNEL == "huawei") {
            tvButton.setOnClickListener {
                val dialog = DonateFragment.newInstance()
                dialog.show(supportFragmentManager, "donateDialog")
            }
        } else {
            tvButton.setOnClickListener {
                start<DonateActivity>()
            }
        }
        return tvButton
    }

    private val viewModel by viewModels<ScheduleSettingsViewModel>()
    private val mAdapter = SettingItemAdapter()
    private val REQUEST_CODE_CHOOSE_BG = 23
    private val REQUEST_CODE_CHOOSE_TABLE = 21
    private val allItems = mutableListOf<BaseSettingItem>()
    private val showItems = mutableListOf<BaseSettingItem>()

    private val currentWeekItem by lazy(LazyThreadSafetyMode.NONE) {
        SeekBarItem("当前周", viewModel.getCurrentWeek(), 1, viewModel.table.maxWeek, "周", "第", keys = listOf("学期", "周", "日期", "开学", "开始", "时间"))
    }

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
                        val k = it.keyWords
                        k?.contains(s.toString()) ?: false
                    })
                }
                mRecyclerView.adapter?.notifyDataSetChanged()
                if (showItems.size == 1) {
                    mRecyclerView.longSnack("找不到哦，换个关键词试试看，或者请仔细找找啦，一般都能找到的。")
                }
            }
        }
        super.onCreate(savedInstanceState)
        viewModel.table = intent.extras!!.getParcelable<TableBean>("tableData") as TableBean

        //onAdapterCreated(mAdapter)

        onItemsCreated(allItems)
        showItems.addAll(allItems)
        mAdapter.data = showItems
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.itemAnimator?.changeDuration = 250
        mRecyclerView.adapter = mAdapter
        mAdapter.addChildClickViewIds(R.id.anko_check_box)
        mAdapter.setOnItemChildClickListener { _, view, position ->
            when (val item = showItems[position]) {
                is SwitchItem -> onSwitchItemCheckChange(item, view.findViewById<AppCompatCheckBox>(R.id.anko_check_box).isChecked)
            }
        }
        mAdapter.setOnItemClickListener { _, view, position ->
            when (val item = showItems[position]) {
                is HorizontalItem -> onHorizontalItemClick(item, position)
                is VerticalItem -> onVerticalItemClick(item)
                is SwitchItem -> view.findViewById<AppCompatCheckBox>(R.id.anko_check_box).performClick()
                is SeekBarItem -> onSeekBarItemClick(item, position)
            }
        }
        mAdapter.setOnItemLongClickListener { _, _, position ->
            when (val item = showItems[position]) {
                is VerticalItem -> onVerticalItemLongClick(item)
            }
            true
        }
        viewModel.termStartList = viewModel.table.startDate.split("-")
        viewModel.mYear = Integer.parseInt(viewModel.termStartList[0])
        viewModel.mMonth = Integer.parseInt(viewModel.termStartList[1])
        viewModel.mDay = Integer.parseInt(viewModel.termStartList[2])
        val settingItem = intent?.extras?.getString("settingItem")
        if (settingItem != null) {
            mRecyclerView.postDelayed({
                try {
                    val i = showItems.indexOfFirst {
                        it.title == settingItem
                    }
                    (mRecyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(i, dip(64))
                    when (showItems[i]) {
                        is HorizontalItem -> onHorizontalItemClick(showItems[i] as HorizontalItem, i)
                        is VerticalItem -> onVerticalItemClick(showItems[i] as VerticalItem)
                        is SeekBarItem -> onSeekBarItemClick(showItems[i] as SeekBarItem, i)
                    }
                } catch (e: Exception) {

                }
            }, 100)
        }
    }

    private fun onItemsCreated(items: MutableList<BaseSettingItem>) {
        items.add(CategoryItem("课程数据", true))
        items.add(HorizontalItem("课表名称", viewModel.table.tableName, listOf("名称", "名字", "名", "课表")))
        items.add(HorizontalItem("上课时间", "点击此处更改", listOf("时间")))
        items.add(HorizontalItem("学期开始日期", viewModel.table.startDate, listOf("学期", "周", "日期", "开学", "开始", "时间")))
        items.add(currentWeekItem)
        items.add(HorizontalItem("管理已添加课程", "", keys = listOf("课程", "课")))
        items.add(SeekBarItem("一天课程节数", viewModel.table.nodes, 1, 30, "节", keys = listOf("节数", "数量", "数")))
        items.add(SeekBarItem("学期周数", viewModel.table.maxWeek, 1, 30, "周", keys = listOf("学期", "周", "时间")))
        items.add(SwitchItem("周日为每周第一天", viewModel.table.sundayFirst, keys = listOf("周日", "第一天", "起始", "星期天", "天")))
        items.add(SwitchItem("显示周六", viewModel.table.showSat, keys = listOf("周六", "显示", "星期六", "六")))
        items.add(SwitchItem("显示周日", viewModel.table.showSun, keys = listOf("周日", "显示", "星期日", "日", "星期天", "周天")))

        items.add(CategoryItem("课表外观", false))
        items.add(SwitchItem("在格子内显示上课时间", viewModel.table.showTime, keys = listOf("时间", "显示", "格子", "上课时间")))
        items.add(VerticalItem("课程表背景", "长按可以恢复默认哦~", keys = listOf("背景", "显示", "图片")))
        items.add(VerticalItem("界面文字颜色", "指标题等字体的颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)", keys = listOf("颜色", "显示", "文字", "文字颜色")))
        items.add(VerticalItem("课程文字颜色", "指课程格子内的颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)", keys = listOf("颜色", "显示", "文字", "文字颜色")))
        items.add(VerticalItem("格子边框颜色", "将不透明度调到最低就可以隐藏边框了哦~", keys = listOf("边框", "显示", "边框颜色", "格子", "边")))
        items.add(SeekBarItem("课程格子高度", viewModel.table.itemHeight, 32, 96, "dp", keys = listOf("格子", "高度", "格子高度", "显示")))
        items.add(SeekBarItem("课程格子不透明度", viewModel.table.itemAlpha, 0, 100, "%", keys = listOf("格子", "透明", "格子高度", "显示")))
        items.add(SeekBarItem("课程显示文字大小", viewModel.table.itemTextSize, 8, 16, "sp", keys = listOf("文字", "大小", "文字大小")))
        items.add(SwitchItem("显示非本周课程", viewModel.table.showOtherWeekCourse, keys = listOf("非本周")))

        items.add(CategoryItem("桌面小部件外观", false))
        items.add(SeekBarItem("小部件格子高度", viewModel.table.widgetItemHeight, 32, 96, "dp", keys = listOf("格子", "高度", "格子高度", "显示", "小部件", "小", "插件", "桌面")))
        items.add(SeekBarItem("小部件格子不透明度", viewModel.table.widgetItemAlpha, 0, 100, "%", keys = listOf("格子", "透明", "格子高度", "显示", "小部件", "小", "插件", "桌面")))
        items.add(SeekBarItem("小部件显示文字大小", viewModel.table.widgetItemTextSize, 8, 16, "sp", keys = listOf("文字", "大小", "文字大小", "小部件", "小", "插件", "桌面")))
        items.add(VerticalItem("小部件标题颜色", "指标题等字体的颜色\n对于日视图则是全部文字的颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)", keys = listOf("颜色", "显示", "文字", "文字颜色", "小部件", "小", "插件", "桌面")))
        items.add(VerticalItem("小部件课程颜色", "指课程格子内的文字颜色\n还可以调颜色的透明度哦 (●ﾟωﾟ●)", keys = listOf("颜色", "显示", "文字", "文字颜色", "小部件", "小", "插件", "桌面")))
        items.add(VerticalItem("小部件格子边框颜色", "将不透明度调到最低就可以隐藏边框了哦~", keys = listOf("边框", "显示", "边框颜色", "格子", "边", "小部件", "小", "插件", "桌面")))

        items.add(CategoryItem("高级", false))
        when (BuildConfig.CHANNEL) {
            "google" -> items.add(VerticalItem("看看都有哪些高级功能", "如果想支持一下社团和开发者\n请去支付宝18862196504\n高级功能会持续更新~\n采用诚信授权模式ヾ(=･ω･=)o", keys = listOf("高级")))
            "huawei" -> items.add(VerticalItem("看看都有哪些高级功能", "高级功能会持续更新~", keys = listOf("高级")))
            else -> items.add(VerticalItem("解锁高级功能", "解锁赞助一下社团和开发者ヾ(=･ω･=)o\n高级功能会持续更新~\n采用诚信授权模式", keys = listOf("高级")))
        }

        items.add(VerticalItem("", "\n\n\n"))
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

    private fun onSeekBarItemClick(item: SeekBarItem, position: Int) {
        val dialog = MaterialAlertDialogBuilder(this)
                .setTitle(item.title)
                .setView(R.layout.dialog_edit_text)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.sure, null)
                .setCancelable(false)
                .create()
        dialog.show()
        val inputLayout = dialog.findViewById<TextInputLayout>(R.id.text_input_layout)
        val editText = dialog.findViewById<TextInputEditText>(R.id.edit_text)
        inputLayout?.helperText = "范围 ${item.min} ~ ${item.max}"
        if (item.prefix.isNotEmpty()) {
            inputLayout?.prefixText = item.prefix
        }
        inputLayout?.suffixText = item.unit
        editText?.inputType = InputType.TYPE_CLASS_NUMBER
        if (item.valueInt < item.min) {
            item.valueInt = item.min
        }
        if (item.valueInt > item.max) {
            item.valueInt = item.max
        }
        val valueStr = item.valueInt.toString()
        editText?.setText(valueStr)
        editText?.setSelection(valueStr.length)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val value = editText?.text
            if (value.isNullOrBlank()) {
                inputLayout?.error = "数值不能为空哦>_<"
                return@setOnClickListener
            }
            val valueInt = try {
                value.toString().toInt()
            } catch (e: Exception) {
                inputLayout?.error = "输入异常>_<"
                return@setOnClickListener
            }
            if (valueInt < item.min || valueInt > item.max) {
                inputLayout?.error = "注意范围 ${item.min} ~ ${item.max}"
                return@setOnClickListener
            }
            when (item.title) {
                "一天课程节数" -> viewModel.table.nodes = valueInt
                "学期周数" -> {
                    currentWeekItem.max = valueInt
                    viewModel.table.maxWeek = valueInt
                }
                "当前周" -> {
                    viewModel.setCurrentWeek(valueInt)
                    item.valueInt = valueInt
                    (mAdapter.data[position - 1] as HorizontalItem).value = viewModel.table.startDate
                    mAdapter.notifyItemChanged(position - 1)
                    mAdapter.notifyItemChanged(position)
                    dialog.dismiss()
                }
                "课程格子高度" -> viewModel.table.itemHeight = valueInt
                "课程格子不透明度" -> viewModel.table.itemAlpha = valueInt
                "课程显示文字大小" -> viewModel.table.itemTextSize = valueInt
                "小部件格子高度" -> viewModel.table.widgetItemHeight = valueInt
                "小部件格子不透明度" -> viewModel.table.widgetItemAlpha = valueInt
                "小部件显示文字大小" -> viewModel.table.widgetItemTextSize = valueInt
            }
            item.valueInt = valueInt
            mAdapter.notifyItemChanged(position)
            dialog.dismiss()
        }
    }

    private fun onHorizontalItemClick(item: HorizontalItem, position: Int) {
        when (item.title) {
            "课表名称" -> {
                val dialog = MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.setting_schedule_name)
                        .setView(R.layout.dialog_edit_text)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.sure, null)
                        .create()
                dialog.show()
                val inputLayout = dialog.findViewById<TextInputLayout>(R.id.text_input_layout)
                val editText = dialog.findViewById<TextInputEditText>(R.id.edit_text)
                editText?.setText(item.value)
                editText?.setSelection(item.value.length)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val value = editText?.text
                    if (value.isNullOrBlank()) {
                        inputLayout?.error = "名称不能为空哦>_<"
                        return@setOnClickListener
                    }
                    viewModel.table.tableName = value.toString()
                    item.value = value.toString()
                    mAdapter.notifyItemChanged(position)
                    dialog.dismiss()
                }
            }
            "学期开始日期" -> {
                DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    viewModel.mYear = year
                    viewModel.mMonth = monthOfYear + 1
                    viewModel.mDay = dayOfMonth
                    val mDate = "${viewModel.mYear}-${viewModel.mMonth}-${viewModel.mDay}"
                    item.value = mDate
                    viewModel.table.startDate = mDate
                    currentWeekItem.valueInt = viewModel.getCurrentWeek()
                    mAdapter.notifyItemChanged(position)
                    mAdapter.notifyItemChanged(position + 1)
                }, viewModel.mYear, viewModel.mMonth - 1, viewModel.mDay).show()
                if (viewModel.table.sundayFirst) {
                    Toasty.success(this, "为了周数计算准确，建议选择周日哦", Toast.LENGTH_LONG).show()
                } else {
                    Toasty.success(this, "为了周数计算准确，建议选择周一哦", Toast.LENGTH_LONG).show()
                }
            }
            "上课时间" -> {
                startActivityForResult(Intent(this, TimeSettingsActivity::class.java).apply {
                    putExtra("selectedId", viewModel.table.timeTable)
                }, REQUEST_CODE_CHOOSE_TABLE)
            }
            "管理已添加课程" -> {
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
        }
    }

    private fun onVerticalItemClick(item: VerticalItem) {
        when (item.title) {
            "课程表背景" -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                }
                try {
                    startActivityForResult(intent, REQUEST_CODE_CHOOSE_BG)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
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
                start<AdvancedSettingsActivity>()
            }
            "看看都有哪些高级功能" -> {
                start<AdvancedSettingsActivity>()
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
            viewModel.saveSettings()
            val list = viewModel.getScheduleWidgetIds()
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
