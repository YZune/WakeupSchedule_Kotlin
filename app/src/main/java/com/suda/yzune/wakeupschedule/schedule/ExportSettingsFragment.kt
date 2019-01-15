package com.suda.yzune.wakeupschedule.schedule

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import es.dmoral.toasty.Toasty
import gdut.bsx.share2.FileUtil
import gdut.bsx.share2.Share2
import gdut.bsx.share2.ShareContentType
import kotlinx.android.synthetic.main.fragment_export_settings.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

class ExportSettingsFragment : BaseDialogFragment(), CoroutineScope {

    override val layoutId: Int
        get() = R.layout.fragment_export_settings

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        tv_export.setOnClickListener {
            launch {
                val task = async(Dispatchers.IO) {
                    try {
                        viewModel.exportData(Environment.getExternalStorageDirectory().absolutePath)
                        "ok"
                    } catch (e: Exception) {
                        e.message
                    }
                }.await()
                if (task == "ok") {
                    Toasty.success(activity!!.applicationContext, "导出成功").show()
                    dismiss()
                } else {
                    Toasty.error(activity!!.applicationContext, "出现异常>_<\n$task").show()
                }
            }
        }

        tv_share.setOnClickListener {
            launch {
                val task = async(Dispatchers.IO) {
                    try {
                        viewModel.exportData(Environment.getExternalStorageDirectory().absolutePath)
                    } catch (e: Exception) {
                        null
                    }
                }.await()
                if (task != null) {
                    Share2.Builder(activity)
                            .setContentType(ShareContentType.FILE)
                            .setShareFileUri(FileUtil.getFileUri(activity, null, File(task)))
                            .setTitle("导出并分享课程文件")
                            .build()
                            .shareBySystem()
                    dismiss()
                } else {
                    Toasty.error(activity!!.applicationContext, "出现异常>_<").show()
                }
            }
        }

        tv_cancel.setOnClickListener {
            dismiss()
        }
    }
}
