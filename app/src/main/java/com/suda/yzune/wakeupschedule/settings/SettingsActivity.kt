package com.suda.yzune.wakeupschedule.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.suda.yzune.wakeupschedule.AppDatabase
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.textColorResource

class SettingsActivity : BaseListActivity() {

    private lateinit var dataBase: AppDatabase
    private lateinit var tableDao: TableDao

    private val mAdapter: MultiTypeAdapter = MultiTypeAdapter()

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        tvButton.text = "捐赠"
        tvButton.textColorResource = R.color.colorAccent
        tvButton.setOnClickListener {
            startActivity<DonateActivity>()
        }
        return tvButton
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBase = AppDatabase.getDatabase(application)
        tableDao = dataBase.tableDao()

        onAdapterCreated(mAdapter)
        val items = Items()
        onItemsCreated(items)
        mAdapter.items = items
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
    }

    private fun onItemsCreated(items: Items) {
        items.add(CategoryItem("常规", true))
        items.add(SwitchItem("标题栏模糊效果", PreferenceUtils.getBooleanFromSP(applicationContext, "title_blur", true)))
        items.add(SwitchItem("自动检查更新", PreferenceUtils.getBooleanFromSP(applicationContext, "s_update", true)))
        items.add(HorizontalItem("设置当前课表", ""))

        items.add(CategoryItem("高级", false))
        items.add(VerticalItem("解锁高级功能", "解锁赞助一下社团和开发者ヾ(=･ω･=)o\n高级功能会持续更新~\n采用诚信授权模式"))

        items.add(CategoryItem("开发情况", false))
        items.add(VerticalItem("截至2018.12.1", "160次代码提交\n净提交代码17602行\n点击跳转至项目地址\n欢迎star和fork"))
    }

    private fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(CategoryItem::class, CategoryItemViewBinder())
        adapter.register(HorizontalItem::class, HorizontalItemViewBinder { onHorizontalItemClick(it) })
        adapter.register(VerticalItem::class, VerticalItemViewBinder({ onVerticalItemClick(it) }, { false }))
        adapter.register(SwitchItem::class, SwitchItemViewBinder { item, isCheck -> onSwitchItemCheckChange(item, isCheck) })
    }

    private fun onSwitchItemCheckChange(item: SwitchItem, isChecked: Boolean) {
        when (item.title) {
            "标题栏模糊效果" -> {
                PreferenceUtils.saveBooleanToSP(applicationContext, "title_blur", isChecked)
                if (Build.VERSION.SDK_INT < 21) {
                    mRecyclerView.longSnackbar("该设置仅对Android 5.0及以上版本有效>_<")
                }
            }
            "自动检查更新" -> PreferenceUtils.saveBooleanToSP(applicationContext, "s_update", isChecked)
        }
        item.checked = isChecked
    }

    private fun onHorizontalItemClick(item: HorizontalItem) {
        when (item.title) {
            "设置当前课表" -> {
                launch {
                    val table = async(Dispatchers.IO) {
                        tableDao.getDefaultTableInThread()
                    }.await()
                    startActivityForResult<ScheduleSettingsActivity>(180, "tableData" to table)
                }
            }
        }
    }

    private fun onVerticalItemClick(item: VerticalItem) {
        when (item.title) {
            "解锁高级功能" -> {
                startActivity<AdvancedSettingsActivity>()
            }
            "截至2018.12.1" -> {
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
