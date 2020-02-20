package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseBlurTitleActivity
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_week_schedule_app_widget_config.*
import splitties.snackbar.longSnack

class WeekScheduleAppWidgetConfigActivity : BaseBlurTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_week_schedule_app_widget_config

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        return null
    }

    private val viewModel by viewModels<WeekScheduleAppWidgetConfigViewModel>()
    private var mAppWidgetId = 0
    private var isTodayType = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        //Log.d("包名", appWidgetManager.getAppWidgetInfo(mAppWidgetId).provider.shortClassName)
        val what = appWidgetManager.getAppWidgetInfo(mAppWidgetId).provider.shortClassName
        isTodayType = (what == ".today_appwidget.TodayCourseAppWidget" || what == "com.suda.yzune.wakeupschedule.today_appwidget.TodayCourseAppWidget")
        if (isTodayType) {
            Glide.with(this)
                    .load("https://ws2.sinaimg.cn/large/0069RVTdgy1fv5ypjuqs1j30u01hcdlt.jpg")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(iv_tip)
        } else {
            tv_got_it.visibility = View.GONE
            iv_tip.visibility = View.GONE
            val list = ArrayList<TableSelectBean>()
            val adapter = WidgetTableListAdapter(R.layout.item_table_list, list)
            adapter.setOnItemClickListener { _, _, position ->
                launch {
                    viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 0, "${list[position].id}"))
                    val table = viewModel.getTableById(list[position].id)

                    if (table == null) {
                        Toasty.error(applicationContext, "该课表读取错误>_<").show()
                        finish()
                    } else {
                        AppWidgetUtils.refreshScheduleWidget(applicationContext, appWidgetManager, mAppWidgetId, table)
                        val resultValue = Intent()
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                        setResult(Activity.RESULT_OK, resultValue)
                        finish()
                    }
                }
            }
            rv_list.adapter = adapter
            rv_list.layoutManager = LinearLayoutManager(this)
            launch {
                list.clear()
                list.addAll(viewModel.getTableList())
                adapter.notifyDataSetChanged()
            }
        }


        tv_got_it.setOnClickListener {
            launch {
                // Log.d("包名", appWidgetManager.getAppWidgetInfo(mAppWidgetId).provider.shortClassName)
                viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 1, ""))
                val table = viewModel.getDefaultTable()
                AppWidgetUtils.refreshTodayWidget(applicationContext, appWidgetManager, mAppWidgetId, table)
                val resultValue = Intent()
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                setResult(Activity.RESULT_OK, resultValue)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        ll_root.longSnack(
                if (isTodayType) {
                    "请阅读文字后点击“我知道啦”"
                } else {
                    "请从列表中选择需要放置的课表"
                })
    }
}
