package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import com.suda.yzune.wakeupschedule.schedule_manage.TableListAdapter
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
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

        viewModel.initTableSelectList().observe(this, Observer {
            if (it == null) return@Observer
            initTableRecyclerView(it)
        })

        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
        }
    }

    private fun initTableRecyclerView(data: List<TableSelectBean>) {
        rv_table.layoutManager = LinearLayoutManager(this)
        val adapter = TableListAdapter(R.layout.item_table_list, data)
        adapter.setOnItemClickListener { _, _, position ->
            viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 0, data[position].id.toString()))
            viewModel.getTableData(data[position].id).observe(this, Observer {
                if (it == null) return@Observer
                val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                AppWidgetUtils.refreshScheduleWidget(this.applicationContext, appWidgetManager, mAppWidgetId, it)
                val resultValue = Intent()
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                setResult(Activity.RESULT_OK, resultValue)
                finish()
            })
        }
        rv_table.adapter = adapter
    }
}
