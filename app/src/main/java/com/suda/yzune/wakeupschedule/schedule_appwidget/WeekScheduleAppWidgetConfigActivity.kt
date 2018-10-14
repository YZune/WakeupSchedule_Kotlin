package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.suda.yzune.wakeupschedule.GlideApp
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_week_schedule_app_widget_config.*

class WeekScheduleAppWidgetConfigActivity : AppCompatActivity() {

    private lateinit var viewModel: WeekScheduleAppWidgetConfigViewModel
    private var mAppWidgetId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_schedule_app_widget_config)
        ViewUtils.resizeStatusBar(this, v_status)

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

        ib_back.setOnClickListener {
            Toasty.info(applicationContext, "请阅读文字后点击“我知道啦”").show()
        }

        tv_got_it.setOnClickListener { _ ->
            viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 0, ""))
            viewModel.getDefaultTable().observe(this, Observer {
                if (it == null) return@Observer
                val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                AppWidgetUtils.refreshScheduleWidget(this.applicationContext, appWidgetManager, mAppWidgetId, it)
                val resultValue = Intent()
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                setResult(Activity.RESULT_OK, resultValue)
                finish()
            })
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Toasty.info(applicationContext, "请阅读文字后点击“我知道啦”").show()
        return false
    }
}
