package com.suda.yzune.wakeupschedule.settings

import android.Manifest
import android.app.DatePickerDialog
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import android.widget.Toast
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.dao.AppWidgetDao
import com.suda.yzune.wakeupschedule.dao.TimeDetailDao
import com.suda.yzune.wakeupschedule.utils.GlideAppEngine
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_settings.*
import kotlin.concurrent.thread

class SettingsActivity : AppCompatActivity() {

    private val REQUEST_CODE_CHOOSE = 23
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private val timeList = arrayListOf<TimeDetailBean>()
    private lateinit var dataBase: AppDatabase
    private lateinit var widgetDao: AppWidgetDao
    private lateinit var timeDao: TimeDetailDao
    private val scheduleIdList = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ViewUtils.resizeStatusBar(this, v_status)

        dataBase = AppDatabase.getDatabase(applicationContext)
        widgetDao = dataBase.appWidgetDao()
        timeDao = dataBase.timeDetailDao()

        initView()
        initEvent()
//        widgetDao.getLiveIdsByTypes(0, 0).observe(this, Observer {
//            scheduleIdList.clear()
//            scheduleIdList.addAll(it!!)
//            //Log.d("小部件", "看看有没有被触发呢")
//        })

        initSudaTime(this)
    }

    private fun initSudaTime(context: Context) {
        if (!PreferenceUtils.getBooleanFromSP(context.applicationContext, "isInitTimeTable", false)) {
            timeList.add(TimeDetailBean(1, "08:00", "08:50", 0))
            timeList.add(TimeDetailBean(2, "09:00", "09:50", 0))
            timeList.add(TimeDetailBean(3, "10:10", "11:00", 0))
            timeList.add(TimeDetailBean(4, "11:10", "12:00", 0))
            timeList.add(TimeDetailBean(5, "13:30", "14:20", 0))
            timeList.add(TimeDetailBean(6, "14:30", "15:20", 0))
            timeList.add(TimeDetailBean(7, "15:40", "16:30", 0))
            timeList.add(TimeDetailBean(8, "16:40", "17:30", 0))
            timeList.add(TimeDetailBean(9, "18:30", "19:20", 0))
            timeList.add(TimeDetailBean(10, "19:30", "20:20", 0))
            timeList.add(TimeDetailBean(11, "20:30", "21:20", 0))
            timeList.add(TimeDetailBean(12, "00:00", "00:00", 0))
            timeList.add(TimeDetailBean(13, "00:00", "00:00", 0))
            timeList.add(TimeDetailBean(14, "00:00", "00:00", 0))
            timeList.add(TimeDetailBean(15, "00:00", "00:00", 0))
            timeList.add(TimeDetailBean(16, "00:00", "00:00", 0))
            timeList.add(TimeDetailBean(17, "00:00", "00:00", 0))
            timeList.add(TimeDetailBean(18, "00:00", "00:00", 0))
            timeList.add(TimeDetailBean(19, "00:00", "00:00", 0))
            timeList.add(TimeDetailBean(20, "00:00", "00:00", 0))
            thread(name = "initTimeTableThread") {
                try {
                    timeDao.insertTimeList(timeList)
                } catch (e: SQLiteConstraintException) {

                }
                PreferenceUtils.saveBooleanToSP(context.applicationContext, "isInitTimeTable", true)
                PreferenceUtils.saveBooleanToSP(context.applicationContext, "isInitSummerTimeTable", true)
            }
        }
    }

    private fun initView() {
        s_sunday_first.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_sunday_first", false)
        s_stroke.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_stroke", true)
        s_update.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_update", true)
        s_show_time_detail.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_show_time_detail", false)
        s_show.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_show", false)
        s_show_sat.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_show_sat", true)
        s_show_weekend.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_show_weekend", true)
        s_text_white.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_color", false)
        s_widget_text_white.isChecked = PreferenceUtils.getBooleanFromSP(applicationContext, "s_widget_color", false)
        val itemHeight = PreferenceUtils.getIntFromSP(applicationContext, "item_height", 56)
        val widgetItemHeight = PreferenceUtils.getIntFromSP(applicationContext, "widget_item_height", 56)
        val nodesNum = PreferenceUtils.getIntFromSP(applicationContext, "classNum", 11)
        val itemAlpha = PreferenceUtils.getIntFromSP(applicationContext, "sb_alpha", 60)
        val textSize = PreferenceUtils.getIntFromSP(applicationContext, "sb_text_size", 12)
        val widgetItemAlpha = PreferenceUtils.getIntFromSP(applicationContext, "sb_widget_alpha", 60)
        val widgetTextSize = PreferenceUtils.getIntFromSP(applicationContext, "sb_widget_text_size", 12)
        val weeksNum = PreferenceUtils.getIntFromSP(applicationContext, "sb_weeks", 30)
        sb_weeks.progress = weeksNum - 10
        sb_text_size.progress = textSize - 11
        sb_widget_text_size.progress = widgetTextSize - 11
        sb_widget_item_height.progress = widgetItemHeight - 32
        sb_height.progress = itemHeight - 32
        sb_nodes.progress = nodesNum - 4
        sb_alpha.progress = itemAlpha
        sb_widget_item_alpha.progress = widgetItemAlpha
        tv_text_size.text = textSize.toString()
        tv_widget_text_size.text = widgetTextSize.toString()
        tv_height.text = itemHeight.toString()
        tv_widget_item_height.text = widgetItemHeight.toString()
        tv_nodes.text = nodesNum.toString()
        tv_alpha.text = itemAlpha.toString()
        tv_widget_item_alpha.text = widgetItemAlpha.toString()
        tv_weeks.text = weeksNum.toString()

        val termStart = PreferenceUtils.getStringFromSP(applicationContext, "termStart", "2018-09-03")
        tv_term_start.text = termStart
        val termStartList = termStart!!.split("-")
        mYear = Integer.parseInt(termStartList[0])
        mMonth = Integer.parseInt(termStartList[1])
        mDay = Integer.parseInt(termStartList[2])
    }

    private fun initEvent() {
        ib_back.setOnClickListener {
            finish()
        }

        ll_course_time.setOnClickListener {
            startActivity(Intent(this, TimeSettingsActivity::class.java))
        }

        sb_widget_item_alpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_widget_item_alpha.text = "$progress"
                PreferenceUtils.saveIntToSP(this@SettingsActivity.applicationContext, "sb_widget_alpha", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_alpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_alpha.text = "$progress"
                PreferenceUtils.saveIntToSP(this@SettingsActivity.applicationContext, "sb_alpha", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_widget_item_height.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_widget_item_height.text = "${progress + 32}"
                PreferenceUtils.saveIntToSP(this@SettingsActivity.applicationContext, "widget_item_height", progress + 32)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_text_size.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_text_size.text = "${progress + 11}"
                PreferenceUtils.saveIntToSP(this@SettingsActivity.applicationContext, "sb_text_size", progress + 11)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_widget_text_size.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_widget_text_size.text = "${progress + 11}"
                PreferenceUtils.saveIntToSP(this@SettingsActivity.applicationContext, "sb_widget_text_size", progress + 11)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_height.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_height.text = "${progress + 32}"
                PreferenceUtils.saveIntToSP(this@SettingsActivity.applicationContext, "item_height", progress + 32)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_nodes.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_nodes.text = "${progress + 4}"
                PreferenceUtils.saveIntToSP(this@SettingsActivity.applicationContext, "classNum", progress + 4)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_weeks.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_weeks.text = "${progress + 10}"
                PreferenceUtils.saveIntToSP(this@SettingsActivity.applicationContext, "sb_weeks", progress + 10)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        s_show_time_detail.setOnCheckedChangeListener { _, isChecked ->
            if (!PreferenceUtils.getBooleanFromSP(applicationContext, "isInitTimeTable", false)) {
                s_show_time_detail.isChecked = false
                startActivity(Intent(this, TimeSettingsActivity::class.java))
                Toasty.info(applicationContext, "首先要进行上课时间的设置哦").show()
            } else {
                PreferenceUtils.saveBooleanToSP(applicationContext, "s_show_time_detail", isChecked)
            }
        }

        s_show.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_show", isChecked)
        }

        s_sunday_first.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_sunday_first", isChecked)
        }

        s_update.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_update", isChecked)
        }

        s_show_weekend.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_show_weekend", isChecked)
        }

        s_show_sat.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_show_sat", isChecked)
        }

        s_stroke.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_stroke", isChecked)
        }

        s_text_white.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_color", isChecked)
        }

        s_widget_text_white.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(applicationContext, "s_widget_color", isChecked)
        }

        ll_term_start.setOnClickListener {
            DatePickerDialog(this, mDateListener, mYear, mMonth - 1, mDay).show()
            Toasty.success(applicationContext, "为了周数计算准确，建议选择周一哦", Toast.LENGTH_LONG).show()
        }

        ll_schedule_bg.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                Matisse.from(this)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(1)
                        .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(GlideAppEngine())
                        .forResult(REQUEST_CODE_CHOOSE)
            }
        }
    }

    private val mDateListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        mYear = year
        mMonth = monthOfYear + 1
        mDay = dayOfMonth
        val mDate = "$mYear-$mMonth-$mDay"
        tv_term_start.text = mDate
        PreferenceUtils.saveStringToSP(applicationContext, "termStart", mDate)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Matisse.from(this)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(1)
                            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(GlideAppEngine())
                            .forResult(REQUEST_CODE_CHOOSE)

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toasty.error(this, "你取消了授权，无法更换背景", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            PreferenceUtils.saveStringToSP(applicationContext, "pic_uri", Matisse.obtainResult(data)[0].toString())
        }
    }

    override fun onDestroy() {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        for (i in scheduleIdList) {
            //AppWidgetUtils.refreshScheduleWidget(applicationContext, appWidgetManager, i)
        }
        super.onDestroy()
    }
}
