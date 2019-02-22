package com.suda.yzune.wakeupschedule.settings

import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.BuildConfig
import com.suda.yzune.wakeupschedule.DonateActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseListActivity
import com.suda.yzune.wakeupschedule.dao.AppWidgetDao
import com.suda.yzune.wakeupschedule.settings.bean.CategoryItem
import com.suda.yzune.wakeupschedule.settings.bean.SeekBarItem
import com.suda.yzune.wakeupschedule.settings.bean.SwitchItem
import com.suda.yzune.wakeupschedule.settings.bean.VerticalItem
import com.suda.yzune.wakeupschedule.settings.view_binder.CategoryItemViewBinder
import com.suda.yzune.wakeupschedule.settings.view_binder.SeekBarItemViewBinder
import com.suda.yzune.wakeupschedule.settings.view_binder.SwitchItemViewBinder
import com.suda.yzune.wakeupschedule.settings.view_binder.VerticalItemViewBinder
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.DonateUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.drakeet.multitype.MultiTypeAdapter
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColorResource


class AdvancedSettingsActivity : BaseListActivity() {

    private lateinit var dataBase: AppDatabase
    private lateinit var widgetDao: AppWidgetDao

    private val mAdapter: MultiTypeAdapter = MultiTypeAdapter()

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        return if (BuildConfig.CHANNEL == "google" || BuildConfig.CHANNEL == "huawei") {
            null
        } else {
            tvButton.text = "捐赠"
            tvButton.textColorResource = R.color.colorAccent
            tvButton.setOnClickListener {
                startActivity<DonateActivity>()
            }
            tvButton
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBase = AppDatabase.getDatabase(application)
        widgetDao = dataBase.appWidgetDao()

        onAdapterCreated(mAdapter)
        val items = mutableListOf<Any>()
        onItemsCreated(items)
        mAdapter.items = items
        mRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
    }

    private fun onItemsCreated(items: MutableList<Any>) {
        if (BuildConfig.CHANNEL == "google" || BuildConfig.CHANNEL == "huawei") {
            items.add(CategoryItem("外观", true))
        } else {
            items.add(CategoryItem("愿意为之付费吗？", true))
            items.add(VerticalItem("如何解锁？", "高级功能理论上是可以直接使用的，但是，像无人看守的小卖部，付费后再使用是诚信的表现哦~<br>朋友、校友、亲人，以及在此之前已经捐赠过的用户，已经解锁了高级功能，<b><font color='#fa6278'>无需再花钱</font></b>。<br>其他用户的解锁方式如下，<b><font color='#fa6278'>二选一即可：</font></b><br>1. 应用商店5星 + 支付宝付款2元<br>2. 支付宝付款5元<br><b><font color='#fa6278'>点击此处进行付款，谢谢:)</font></b><br>", true))
            items.add(VerticalItem("解锁后", "解锁后，你可以在你自用的任何设备上安装使用，并且免费使用后续更新的高级功能。<br><b><font color='#fa6278'>放心，无论什么版本，App不会有任何形式的广告。</font></b>", true))
            items.add(CategoryItem("外观", false))
        }

        items.add(VerticalItem("虚拟键颜色", "调整虚拟键的颜色。\n以下关于虚拟键的设置，只对有虚拟键的手机有效哦，是为了有更好的沉浸效果~\n有实体按键或全面屏手势的手机本身就很棒啦~"))
        items.add(SwitchItem("主界面虚拟键沉浸", PreferenceUtils.getBooleanFromSP(applicationContext, "hide_main_nav_bar", false)))

        items.add(CategoryItem("上课提醒", false))
        items.add(VerticalItem("功能说明", "本功能处于<b><font color='#fa6278'>试验性阶段</font></b>。由于国产手机对系统的定制不尽相同，本功能可能会在某些手机上失效。<b><font color='#fa6278'>开启前提：设置好课程时间 + 往桌面添加一个日视图小部件 + 允许App后台运行</font></b>。<br>理论上<b><font color='#fa6278'>每次设置之后</font></b>需要半天以上的时间才会正常工作，理论上不会很耗电。", true))
        items.add(SwitchItem("开启上课提醒", PreferenceUtils.getBooleanFromSP(applicationContext, "course_reminder", false)))
        items.add(SwitchItem("提醒通知常驻", PreferenceUtils.getBooleanFromSP(applicationContext, "reminder_on_going", false)))
        items.add(SeekBarItem("提前几分钟提醒", PreferenceUtils.getIntFromSP(applicationContext, "reminder_min", 20), 0, 90, "分钟"))
        //items.add(SwitchItem("提醒同时将手机静音", PreferenceUtils.getBooleanFromSP(applicationContext, "silence_reminder", false)))

        items.add(CategoryItem("开发情况", false))
        items.add(VerticalItem("截至2018.12.02", "161次代码提交\n净提交代码17935行\n点击跳转至项目地址\n欢迎star和fork"))
    }

    private fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(CategoryItem::class, CategoryItemViewBinder())
        adapter.register(SeekBarItem::class, SeekBarItemViewBinder { item, value -> onSeekBarValueChange(item, value) })
        adapter.register(VerticalItem::class, VerticalItemViewBinder({ onVerticalItemClick(it) }, { false }))
        adapter.register(SwitchItem::class, SwitchItemViewBinder { item, isCheck -> onSwitchItemCheckChange(item, isCheck) })
    }

    private fun onSwitchItemCheckChange(item: SwitchItem, isChecked: Boolean) {
        when (item.title) {
            "主界面虚拟键沉浸" -> {
                PreferenceUtils.saveBooleanToSP(applicationContext, "hide_main_nav_bar", isChecked)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    mRecyclerView.longSnackbar("该设置仅对 Android 4.4 及以上版本有效>_<")
                } else {
                    mRecyclerView.longSnackbar("重启App后生效哦~")
                }
                item.checked = isChecked
            }
            "开启上课提醒" -> {
                launch {
                    val task = async(Dispatchers.IO) {
                        widgetDao.getWidgetsByTypesInThread(0, 1)
                    }.await()
                    if (task.isEmpty()) {
                        mRecyclerView.longSnackbar("好像还没有设置日视图小部件呢>_<")
                        PreferenceUtils.saveBooleanToSP(applicationContext, "course_reminder", false)
                        item.checked = false
                        mAdapter.notifyDataSetChanged()
                    } else {
                        PreferenceUtils.saveBooleanToSP(applicationContext, "course_reminder", isChecked)
                        AppWidgetUtils.updateWidget(applicationContext)
                        item.checked = isChecked
                    }
                }
            }
            "提醒通知常驻" -> {
                PreferenceUtils.saveBooleanToSP(applicationContext, "reminder_on_going", isChecked)
                item.checked = isChecked
                mRecyclerView.longSnackbar("对下一次提醒通知生效哦")
            }
            "提醒同时将手机静音" -> {
                val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted) {
                    val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                    startActivity(intent)
                    item.checked = false
                } else {
                    PreferenceUtils.saveBooleanToSP(applicationContext, "silence_reminder", isChecked)
                    AppWidgetUtils.updateWidget(applicationContext)
                    item.checked = isChecked
                }
            }
        }
    }

    private fun onVerticalItemClick(item: VerticalItem) {
        when (item.title) {
            "如何解锁？" -> {
                if (DonateUtils.isAppInstalled(applicationContext, "com.eg.android.AlipayGphone")) {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val qrCodeUrl = Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=HTTPS://QR.ALIPAY.COM/FKX09148M0LN2VUUZENO9B?_s=web-other")
                    intent.data = qrCodeUrl
                    intent.setClassName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.LauncherActivity")
                    startActivity(intent)
                    Toasty.success(applicationContext, "非常感谢(*^▽^*)").show()
                } else {
                    Toasty.info(applicationContext, "没有检测到支付宝客户端o(╥﹏╥)o").show()
                }
            }
            "虚拟键颜色" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ColorPickerDialogBuilder
                            .with(this)
                            .setTitle("选取颜色")
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setNegativeButton("取消") { _, _ -> }
                            .initialColor(PreferenceUtils.getIntFromSP(applicationContext, "nav_bar_color", ContextCompat.getColor(applicationContext, R.color.colorAccent)))
                            .setPositiveButton("确定") { _, colorInt, _ ->
                                PreferenceUtils.saveIntToSP(applicationContext, "nav_bar_color", colorInt)
                                mRecyclerView.longSnackbar("重启App后生效哦~")
                            }
                            .build()
                            .show()
                } else {
                    mRecyclerView.longSnackbar("该设置仅对Android 5.0及以上版本有效>_<")
                }
            }
            "截至2018.12.02" -> {
                CourseUtils.openUrl(this, "https://github.com/YZune/WakeupSchedule_Kotlin/")
            }
        }
    }

    private fun onSeekBarValueChange(item: SeekBarItem, value: Int) {
        when (item.title) {
            "提前几分钟提醒" -> {
                PreferenceUtils.saveIntToSP(applicationContext, "reminder_min", value + item.min)
                AppWidgetUtils.updateWidget(applicationContext)
            }
        }
        item.valueInt = value + item.min
    }

    override fun onDestroy() {
        AppWidgetUtils.updateWidget(applicationContext)
        super.onDestroy()
    }
}
