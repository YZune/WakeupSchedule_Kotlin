package com.suda.yzune.wakeupschedule.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.BuildConfig
import com.suda.yzune.wakeupschedule.DonateActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseListActivity
import com.suda.yzune.wakeupschedule.dao.TableDao
import com.suda.yzune.wakeupschedule.schedule_settings.ScheduleSettingsActivity
import com.suda.yzune.wakeupschedule.settings.items.*
import com.suda.yzune.wakeupschedule.utils.Const
import com.suda.yzune.wakeupschedule.utils.getPrefer
import splitties.activities.start
import splitties.resources.color
import splitties.snackbar.longSnack
import splitties.snackbar.snack

class SettingsActivity : BaseListActivity() {

    private lateinit var dataBase: AppDatabase
    private lateinit var tableDao: TableDao
    private val dayNightTheme by lazy(LazyThreadSafetyMode.NONE) {
        resources.getStringArray(R.array.day_night_setting)
    }
    private var dayNightIndex = 2

    private val mAdapter = SettingItemAdapter()

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        return if (BuildConfig.CHANNEL == "google" || BuildConfig.CHANNEL == "huawei") {
            null
        } else {
            tvButton.text = "捐赠"
            tvButton.setTextColor(color(R.color.colorAccent))
            tvButton.setOnClickListener {
                start<DonateActivity>()
            }
            tvButton
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBase = AppDatabase.getDatabase(application)
        tableDao = dataBase.tableDao()
        dayNightIndex = getPrefer().getInt(Const.KEY_DAY_NIGHT_THEME, 2)

        val items = mutableListOf<BaseSettingItem>()
        onItemsCreated(items)
        mAdapter.data = items
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.itemAnimator?.changeDuration = 250
        mRecyclerView.adapter = mAdapter
        mAdapter.addChildClickViewIds(R.id.anko_check_box)
        mAdapter.setOnItemChildClickListener { _, view, position ->
            when (val item = items[position]) {
                is SwitchItem -> onSwitchItemCheckChange(item, view.findViewById<AppCompatCheckBox>(R.id.anko_check_box).isChecked)
            }
        }
        mAdapter.setOnItemClickListener { _, view, position ->
            when (val item = items[position]) {
                is HorizontalItem -> onHorizontalItemClick(item, position)
                is VerticalItem -> onVerticalItemClick(item)
                is SwitchItem -> view.findViewById<AppCompatCheckBox>(R.id.anko_check_box).performClick()
            }
        }
    }

    private fun onItemsCreated(items: MutableList<BaseSettingItem>) {
        items.add(CategoryItem("高级", true))
        when (BuildConfig.CHANNEL) {
            "google" -> items.add(VerticalItem("看看都有哪些高级功能", "如果想支持一下社团和开发者\n请去支付宝18862196504\n高级功能会持续更新~\n采用诚信授权模式ヾ(=･ω･=)o"))
            "huawei" -> items.add(VerticalItem("看看都有哪些高级功能", "高级功能会持续更新~"))
            else -> items.add(VerticalItem("解锁高级功能", "解锁赞助一下社团和开发者ヾ(=･ω･=)o\n高级功能会持续更新~\n采用诚信授权模式"))
        }

        items.add(CategoryItem("常规", false))
        items.add(HorizontalItem("设置当前课表", "点这里！"))
        items.add(SwitchItem("自动检查更新", getPrefer().getBoolean(Const.KEY_CHECK_UPDATE, true)))
        items.add(SwitchItem("节数栏显示具体时间", getPrefer().getBoolean(Const.KEY_SCHEDULE_DETAIL_TIME, true), ""))
        items.add(SwitchItem("页面预加载", getPrefer().getBoolean(Const.KEY_SCHEDULE_PRE_LOAD, true), "开启后，滑动界面后会马上显示课表。关闭后，滑动界面后需要短暂的时间加载课表，不过理论上内存占用会更小，App启动速度也会更快。"))
        items.add(SwitchItem("课表下方增加留白区域", getPrefer().getBoolean(Const.KEY_SCHEDULE_BLANK_AREA, true), "开启后，课表下方会多出一段空白区域，便于将底部的课程滑动至屏幕中间查看。"))
        items.add(SwitchItem("显示日视图背景", getPrefer().getBoolean(Const.KEY_DAY_WIDGET_COLOR, false)))
        items.add(SwitchItem("显示空视图图片", getPrefer().getBoolean(Const.KEY_SHOW_EMPTY_VIEW, true)))
        items.add(SwitchItem("显示侧栏「苏大生活」", getPrefer().getBoolean(Const.KEY_SHOW_SUDA_LIFE, true)))
        items.add(HorizontalItem("显示主题", dayNightTheme[dayNightIndex]))
        items.add(VerticalItem("", "\n\n\n"))
    }

    private fun onSwitchItemCheckChange(item: SwitchItem, isChecked: Boolean) {
        when (item.title) {
            "自动检查更新" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_CHECK_UPDATE, isChecked)
                }
            }
            "页面预加载" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SCHEDULE_PRE_LOAD, isChecked)
                }
                mRecyclerView.snack("重启App后生效哦")
            }
            "课表下方增加留白区域" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SCHEDULE_BLANK_AREA, isChecked)
                }
                mRecyclerView.snack("重启App后生效哦")
            }
            "节数栏显示具体时间" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SCHEDULE_DETAIL_TIME, isChecked)
                }
                mRecyclerView.snack("重启App后生效哦")
            }
            "显示空视图图片" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SHOW_EMPTY_VIEW, isChecked)
                }
                mRecyclerView.snack("切换页面后生效哦")
            }
            "显示日视图背景" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_DAY_WIDGET_COLOR, isChecked)
                }
                mRecyclerView.longSnack("请点击小部件右上角的「切换按钮」查看效果~")
            }
            "显示侧栏「苏大生活」" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SHOW_SUDA_LIFE, isChecked)
                }
                mRecyclerView.snack("重启App后生效哦")
            }
        }
        item.checked = isChecked
    }

    private fun onHorizontalItemClick(item: HorizontalItem, position: Int) {
        when (item.title) {
            "设置当前课表" -> {
                launch {
                    val table = tableDao.getDefaultTable()
                    startActivityForResult(
                            Intent(this@SettingsActivity, ScheduleSettingsActivity::class.java).apply {
                                putExtra("tableData", table)
                            }, 180)
                }
            }
            "显示主题" -> {
                MaterialAlertDialogBuilder(this)
                        .setTitle("显示主题")
                        .setPositiveButton("确定") { _, _ ->
                            getPrefer().edit {
                                putInt(Const.KEY_DAY_NIGHT_THEME, dayNightIndex)
                            }
                            item.value = dayNightTheme[dayNightIndex]
                            mAdapter.notifyItemChanged(position)
                            when (dayNightIndex) {
                                0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                                2 -> {
                                    when {
                                        Build.VERSION.SDK_INT >= 29 -> {
                                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                                        }
                                        Build.VERSION.SDK_INT >= 23 -> {
                                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                                        }
                                        else -> {
                                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                        }
                                    }
                                }
                            }
                        }
                        .setSingleChoiceItems(dayNightTheme, dayNightIndex) { _, which ->
                            dayNightIndex = which
                        }
                        .show()
            }
        }
    }

    private fun onVerticalItemClick(item: VerticalItem) {
        when (item.title) {
            "解锁高级功能" -> {
                start<AdvancedSettingsActivity>()
            }
            "看看都有哪些高级功能" -> {
                start<AdvancedSettingsActivity>()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 180) {
            setResult(RESULT_OK)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
