package com.suda.yzune.wakeupschedule.settings

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.SeekBar
import android.widget.Toast
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.GlideAppEngine
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private val REQUEST_CODE_CHOOSE = 23
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ViewUtils.resizeStatusBar(this, v_status)

        initView()
        initEvent()
    }

    private fun initView() {
        s_show.isChecked = PreferenceUtils.getBooleanFromSP(this.applicationContext, "s_show", false)
        s_show_weekend.isChecked = PreferenceUtils.getBooleanFromSP(this.applicationContext, "s_show_weekend", true)
        s_text_white.isChecked = PreferenceUtils.getBooleanFromSP(this.applicationContext, "s_color", false)
        val itemHeight = PreferenceUtils.getIntFromSP(this.applicationContext, "item_height", 56)
        val nodesNum = PreferenceUtils.getIntFromSP(this.applicationContext, "classNum", 11)
        sb_height.progress = itemHeight - 32
        sb_nodes.progress = nodesNum - 8
        tv_height.text = itemHeight.toString()
        tv_nodes.text = nodesNum.toString()

        val termStart = PreferenceUtils.getStringFromSP(this.applicationContext, "termStart", "2018-09-03")
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
                tv_nodes.text = "${progress + 8}"
                PreferenceUtils.saveIntToSP(this@SettingsActivity.applicationContext, "classNum", progress + 8)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        s_show.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(this.applicationContext, "s_show", isChecked)
        }

        s_show_weekend.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(this.applicationContext, "s_show_weekend", isChecked)
        }

        s_text_white.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.saveBooleanToSP(this.applicationContext, "s_color", isChecked)
        }

        ll_term_start.setOnClickListener {
            DatePickerDialog(this, mDateListener, mYear, mMonth - 1, mDay).show()
        }

        ll_schedule_bg.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                Matisse.from(this)
                        .choose(MimeType.allOf())
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
        PreferenceUtils.saveStringToSP(this.applicationContext, "termStart", mDate)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Matisse.from(this)
                            .choose(MimeType.allOf())
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
            PreferenceUtils.saveStringToSP(this.applicationContext, "pic_uri", Matisse.obtainResult(data)[0].toString())
        }
    }
}
