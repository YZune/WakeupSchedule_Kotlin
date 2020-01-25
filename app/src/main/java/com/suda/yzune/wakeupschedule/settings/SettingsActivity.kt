package com.suda.yzune.wakeupschedule.settings

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.BuildConfig
import com.suda.yzune.wakeupschedule.DonateActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseListActivity
import com.suda.yzune.wakeupschedule.dao.TableDao
import com.suda.yzune.wakeupschedule.schedule_settings.ScheduleSettingsActivity
import com.suda.yzune.wakeupschedule.settings.bean.CategoryItem
import com.suda.yzune.wakeupschedule.settings.bean.HorizontalItem
import com.suda.yzune.wakeupschedule.settings.bean.SwitchItem
import com.suda.yzune.wakeupschedule.settings.bean.VerticalItem
import com.suda.yzune.wakeupschedule.settings.view_binder.CategoryItemViewBinder
import com.suda.yzune.wakeupschedule.settings.view_binder.HorizontalItemViewBinder
import com.suda.yzune.wakeupschedule.settings.view_binder.SwitchItemViewBinder
import com.suda.yzune.wakeupschedule.settings.view_binder.VerticalItemViewBinder
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.widget.snackbar.longSnack
import com.suda.yzune.wakeupschedule.widget.snackbar.snack
import splitties.activities.start
import splitties.views.textColorResource

class SettingsActivity : BaseListActivity() {

    private lateinit var dataBase: AppDatabase
    private lateinit var tableDao: TableDao

    private val mAdapter: MultiTypeAdapter = MultiTypeAdapter()

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        return if (BuildConfig.CHANNEL == "google" || BuildConfig.CHANNEL == "huawei") {
            null
        } else {
            tvButton.text = "捐赠"
            tvButton.textColorResource = R.color.colorAccent
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

        onAdapterCreated(mAdapter)
        val items = mutableListOf<Any>()
        onItemsCreated(items)
        mAdapter.items = items
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
    }

    private fun onItemsCreated(items: MutableList<Any>) {
        items.add(CategoryItem("常规", true))
        items.add(SwitchItem("自动检查更新", PreferenceUtils.getBooleanFromSP(applicationContext, "s_update", true)))
        items.add(SwitchItem("显示日视图背景", PreferenceUtils.getBooleanFromSP(applicationContext, "s_colorful_day_widget", false)))
        items.add(SwitchItem("显示侧栏「苏大生活」", PreferenceUtils.getBooleanFromSP(applicationContext, "suda_life", true)))
        items.add(HorizontalItem("设置当前课表", ""))
        items.add(SwitchItem("使用暗黑模式", PreferenceUtils.getBooleanFromSP(applicationContext, "s_night_mode", false)))

        items.add(CategoryItem("高级", false))
        when (BuildConfig.CHANNEL) {
            "google" -> items.add(VerticalItem("看看都有哪些高级功能", "如果想支持一下社团和开发者\n请去支付宝18862196504\n高级功能会持续更新~\n采用诚信授权模式ヾ(=･ω･=)o"))
            "huawei" -> items.add(VerticalItem("看看都有哪些高级功能", "高级功能会持续更新~"))
            else -> items.add(VerticalItem("解锁高级功能", "解锁赞助一下社团和开发者ヾ(=･ω･=)o\n高级功能会持续更新~\n采用诚信授权模式"))
        }

        items.add(CategoryItem("开发情况", false))
        items.add(VerticalItem("截至2018.12.02", "161次代码提交\n净提交代码17935行\n点击跳转至项目地址\n欢迎star和fork"))
    }

    private fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(CategoryItem::class, CategoryItemViewBinder())
        adapter.register(HorizontalItem::class, HorizontalItemViewBinder { onHorizontalItemClick(it) })
        adapter.register(VerticalItem::class, VerticalItemViewBinder({ onVerticalItemClick(it) }, { false }))
        adapter.register(SwitchItem::class, SwitchItemViewBinder { item, isCheck -> onSwitchItemCheckChange(item, isCheck) })
    }

    private fun onSwitchItemCheckChange(item: SwitchItem, isChecked: Boolean) {
        when (item.title) {
            "自动检查更新" -> PreferenceUtils.saveBooleanToSP(applicationContext, "s_update", isChecked)
            "显示日视图背景" -> {
                PreferenceUtils.saveBooleanToSP(applicationContext, "s_colorful_day_widget", isChecked)
                mRecyclerView.longSnack("请点击小部件右上角的「切换按钮」查看效果~")
            }
            "显示侧栏「苏大生活」" -> {
                PreferenceUtils.saveBooleanToSP(applicationContext, "suda_life", isChecked)
                mRecyclerView.snack("重启App后生效哦")
            }
            "使用暗黑模式" -> {
                PreferenceUtils.saveBooleanToSP(applicationContext, "s_night_mode", isChecked)
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
        item.checked = isChecked
    }

    private fun onHorizontalItemClick(item: HorizontalItem) {
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
            "截至2018.12.02" -> {
                CourseUtils.openUrl(this, "https://github.com/YZune/WakeupSchedule_Kotlin/")
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
