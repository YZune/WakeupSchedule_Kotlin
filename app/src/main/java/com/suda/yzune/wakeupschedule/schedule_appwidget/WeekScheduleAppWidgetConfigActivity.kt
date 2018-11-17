package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.suda.yzune.wakeupschedule.BaseTitleActivity
import com.suda.yzune.wakeupschedule.GlideApp
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import kotlinx.android.synthetic.main.activity_week_schedule_app_widget_config.*
import org.jetbrains.anko.design.longSnackbar

class WeekScheduleAppWidgetConfigActivity : BaseTitleActivity() {

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

        tv_got_it.setOnClickListener { _ ->
            viewModel.getDefaultTable().observe(this, Observer {
                if (it == null) return@Observer
                val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                when (appWidgetManager.getAppWidgetInfo(mAppWidgetId).provider.shortClassName) {
                    ".schedule_appwidget.ScheduleAppWidget" -> {
                        viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 0, ""))
                        AppWidgetUtils.refreshScheduleWidget(applicationContext, appWidgetManager, mAppWidgetId, it)
                    }
                    ".today_appwidget.TodayCourseAppWidget" -> {
                        viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 1, ""))
                        AppWidgetUtils.refreshTodayWidget(applicationContext, appWidgetManager, mAppWidgetId, it)
                    }
                }
                val resultValue = Intent()
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                setResult(Activity.RESULT_OK, resultValue)
                finish()
            })
        }
    }

    override fun onBackPressed() {
        ll_root.longSnackbar("请阅读文字后点击“我知道啦”")
    }
}
