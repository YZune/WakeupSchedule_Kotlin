package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.suda.yzune.wakeupschedule.GlideApp
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseBlurTitleActivity
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import kotlinx.android.synthetic.main.activity_week_schedule_app_widget_config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.design.longSnackbar

class WeekScheduleAppWidgetConfigActivity : BaseBlurTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_week_schedule_app_widget_config

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        return null
    }

    private lateinit var viewModel: WeekScheduleAppWidgetConfigViewModel
    private var mAppWidgetId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(WeekScheduleAppWidgetConfigViewModel::class.java)

        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        GlideApp.with(this)
                .load("https://ws2.sinaimg.cn/large/0069RVTdgy1fv5ypjuqs1j30u01hcdlt.jpg")
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(iv_tip)

        tv_got_it.setOnClickListener {
            launch {
                val table = async(Dispatchers.IO) {
                    viewModel.getDefaultTable()
                }.await()
                val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                //Log.d("包名", appWidgetManager.getAppWidgetInfo(mAppWidgetId).provider.shortClassName)
                when (appWidgetManager.getAppWidgetInfo(mAppWidgetId).provider.shortClassName) {
                    ".schedule_appwidget.ScheduleAppWidget" -> {
                        async(Dispatchers.IO) {
                            viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 0, ""))
                        }.await()
                        AppWidgetUtils.refreshScheduleWidget(applicationContext, appWidgetManager, mAppWidgetId, table)
                    }
                    ".today_appwidget.TodayCourseAppWidget" -> {
                        async(Dispatchers.IO) {
                            viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 1, ""))
                        }.await()
                        AppWidgetUtils.refreshTodayWidget(applicationContext, appWidgetManager, mAppWidgetId, table)
                    }
                    "com.suda.yzune.wakeupschedule.schedule_appwidget.ScheduleAppWidget" -> {
                        async(Dispatchers.IO) {
                            viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 0, ""))
                        }.await()
                        AppWidgetUtils.refreshScheduleWidget(applicationContext, appWidgetManager, mAppWidgetId, table)
                    }
                    "com.suda.yzune.wakeupschedule.today_appwidget.TodayCourseAppWidget" -> {
                        async(Dispatchers.IO) {
                            viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 1, ""))
                        }.await()
                        AppWidgetUtils.refreshTodayWidget(applicationContext, appWidgetManager, mAppWidgetId, table)
                    }
                }
                val resultValue = Intent()
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                setResult(Activity.RESULT_OK, resultValue)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        ll_root.longSnackbar("请阅读文字后点击“我知道啦”")
    }
}
