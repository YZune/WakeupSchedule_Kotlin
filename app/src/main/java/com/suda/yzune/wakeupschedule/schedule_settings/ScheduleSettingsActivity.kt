package com.suda.yzune.wakeupschedule.schedule_settings

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.GlideAppEngine
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.suda.yzune.wakeupschedule.widget.ModifyTableNameFragment
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_schedule_settings.*

class ScheduleSettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: ScheduleSettingsViewModel
    private val REQUEST_CODE_CHOOSE = 23

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_settings)
        ViewUtils.resizeStatusBar(this, v_status)

        viewModel = ViewModelProviders.of(this).get(ScheduleSettingsViewModel::class.java)
        viewModel.initTableData(intent.extras.getString("tableData"))

        initView()
        initEvent()
    }

    private fun initView() {
        tv_table_name.text = viewModel.table.tableName
        s_sunday_first.isChecked = viewModel.table.sundayFirst
        s_show_time_detail.isChecked = viewModel.table.showTime
        s_show.isChecked = viewModel.table.showOtherWeekCourse
        s_show_sat.isChecked = viewModel.table.showSat
        s_show_sunday.isChecked = viewModel.table.showSun
        sb_weeks.progress = viewModel.table.maxWeek - 10
        sb_text_size.progress = viewModel.table.itemTextSize - 11
        sb_widget_text_size.progress = viewModel.table.widgetItemTextSize - 11
        sb_widget_item_height.progress = viewModel.table.widgetItemHeight - 32
        sb_height.progress = viewModel.table.itemHeight - 32
        sb_nodes.progress = viewModel.table.nodes - 4
        sb_alpha.progress = viewModel.table.itemAlpha
        sb_widget_item_alpha.progress = viewModel.table.widgetItemAlpha
        tv_text_size.text = viewModel.table.itemTextSize.toString()
        tv_widget_text_size.text = viewModel.table.widgetItemTextSize.toString()
        tv_height.text = viewModel.table.itemHeight.toString()
        tv_widget_item_height.text = viewModel.table.widgetItemHeight.toString()
        tv_nodes.text = viewModel.table.nodes.toString()
        tv_alpha.text = viewModel.table.itemAlpha.toString()
        tv_widget_item_alpha.text = viewModel.table.widgetItemAlpha.toString()
        tv_weeks.text = viewModel.table.maxWeek.toString()
        tv_term_start.text = viewModel.table.startDate
        viewModel.termStartList = viewModel.table.startDate.split("-")
        viewModel.mYear = Integer.parseInt(viewModel.termStartList[0])
        viewModel.mMonth = Integer.parseInt(viewModel.termStartList[1])
        viewModel.mDay = Integer.parseInt(viewModel.termStartList[2])
    }

    private fun initEvent() {
        ll_table_name.setOnClickListener {
            ModifyTableNameFragment.newInstance(object : ModifyTableNameFragment.TableNameChangeListener {
                override fun onFinish(editText: EditText, dialog: Dialog) {
                    if (!editText.text.toString().isEmpty()) {
                        viewModel.table.tableName = editText.text.toString()
                        tv_table_name.text = editText.text.toString()
                        dialog.dismiss()
                    } else {
                        Toasty.error(applicationContext, "名称不能为空哦>_<").show()
                    }
                }
            }, viewModel.table.tableName).show(supportFragmentManager, "addTableFragment")
        }

        ib_back.setOnClickListener {
            finish()
        }

        ll_course_time.setOnClickListener {

        }

        ll_text_color.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("选取颜色")
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .initialColor(viewModel.table.textColor)
                    .setPositiveButton("确定") { _, colorInt, _ ->
                        viewModel.table.textColor = colorInt
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .build()
                    .show()
        }

        ll_widget_text_color.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("选取颜色")
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .initialColor(viewModel.table.widgetTextColor)
                    .setPositiveButton("确定") { _, colorInt, _ ->
                        viewModel.table.widgetTextColor = colorInt
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .build()
                    .show()
        }

        ll_course_text_color.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("选取颜色")
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .initialColor(viewModel.table.courseTextColor)
                    .setPositiveButton("确定") { _, colorInt, _ ->
                        viewModel.table.courseTextColor = colorInt
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .build()
                    .show()
        }

        ll_widget_course_text_color.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("选取颜色")
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .initialColor(viewModel.table.widgetCourseTextColor)
                    .setPositiveButton("确定") { _, colorInt, _ ->
                        viewModel.table.widgetCourseTextColor = colorInt
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .build()
                    .show()
        }

        ll_stroke_color.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("选取颜色")
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .initialColor(viewModel.table.strokeColor)
                    .setPositiveButton("确定") { _, colorInt, _ ->
                        viewModel.table.strokeColor = colorInt
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .build()
                    .show()
        }

        sb_widget_item_alpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_widget_item_alpha.text = "$progress"
                viewModel.table.widgetItemAlpha = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_alpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_alpha.text = "$progress"
                viewModel.table.itemAlpha = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_widget_item_height.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_widget_item_height.text = "${progress + 32}"
                viewModel.table.widgetItemHeight = progress + 32
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_text_size.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_text_size.text = "${progress + 11}"
                viewModel.table.itemTextSize = progress + 11
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_widget_text_size.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_widget_text_size.text = "${progress + 11}"
                viewModel.table.widgetItemTextSize = progress + 11
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_height.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_height.text = "${progress + 32}"
                viewModel.table.itemHeight = progress + 32
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_nodes.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_nodes.text = "${progress + 4}"
                viewModel.table.nodes = progress + 4
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_weeks.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_weeks.text = "${progress + 10}"
                viewModel.table.maxWeek = progress + 10
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        s_show_time_detail.setOnCheckedChangeListener { _, isChecked ->
            viewModel.table.showTime = isChecked
        }

        s_show.setOnCheckedChangeListener { _, isChecked ->
            viewModel.table.showOtherWeekCourse = isChecked
        }

        s_sunday_first.setOnCheckedChangeListener { _, isChecked ->
            viewModel.table.sundayFirst = isChecked
        }

        s_show_sunday.setOnCheckedChangeListener { _, isChecked ->
            viewModel.table.showSun = isChecked
        }

        s_show_sat.setOnCheckedChangeListener { _, isChecked ->
            viewModel.table.showSat = isChecked
        }

        ll_term_start.setOnClickListener {
            DatePickerDialog(this, mDateListener, viewModel.mYear, viewModel.mMonth - 1, viewModel.mDay).show()
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

        ll_schedule_bg.setOnLongClickListener {
            viewModel.table.background = ""
            Toasty.success(applicationContext, "恢复默认壁纸成功~").show()
            return@setOnLongClickListener true
        }
    }

    private val mDateListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        viewModel.mYear = year
        viewModel.mMonth = monthOfYear + 1
        viewModel.mDay = dayOfMonth
        val mDate = "${viewModel.mYear}-${viewModel.mMonth}-${viewModel.mDay}"
        tv_term_start.text = mDate
        viewModel.table.startDate = mDate
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
            viewModel.table.background = Matisse.obtainResult(data)[0].toString()
        }
    }

    override fun onDestroy() {
        viewModel.saveSettings()
        super.onDestroy()
    }
}
