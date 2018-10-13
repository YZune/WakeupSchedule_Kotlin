package com.suda.yzune.wakeupschedule.schedule


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.suda.yzune.wakeupschedule.R
import es.dmoral.toasty.Toasty
import gdut.bsx.share2.FileUtil
import gdut.bsx.share2.Share2
import gdut.bsx.share2.ShareContentType
import kotlinx.android.synthetic.main.fragment_export_settings.*
import java.io.File

class ExportSettingsFragment : DialogFragment() {

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_export_settings, container, false)
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        dialog.window.setLayout((dm.widthPixels * 0.75).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        tv_export.setOnClickListener { _ ->
            viewModel.exportData(Environment.getExternalStorageDirectory().absolutePath)
            viewModel.exportImportInfo.observe(this, Observer {
                if (!it.isNullOrBlank()) {
                    Toasty.success(activity!!.applicationContext, "导出成功").show()
                    dismiss()
                }
            })
        }

        tv_share.setOnClickListener { _ ->
            viewModel.exportData(Environment.getExternalStorageDirectory().absolutePath)
            viewModel.exportImportInfo.observe(this, Observer {
                if (it.isNullOrBlank()) return@Observer
                Log.d("路径", it)
                Share2.Builder(activity)
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(FileUtil.getFileUri(activity, null, File(it)))
                        .setTitle("导出并分享课程文件")
                        .build()
                        .shareBySystem()
                dismiss()
            })
        }

        tv_cancel.setOnClickListener {
            dismiss()
        }
    }

}
