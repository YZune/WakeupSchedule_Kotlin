package com.suda.yzune.wakeupschedule.schedule

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import es.dmoral.toasty.Toasty
import gdut.bsx.share2.FileUtil
import gdut.bsx.share2.Share2
import gdut.bsx.share2.ShareContentType
import kotlinx.android.synthetic.main.fragment_export_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ExportSettingsFragment : BaseDialogFragment() {

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
            tv_export.text = "导出中…请稍候"
            launch {
                val task = withContext(Dispatchers.IO) {
                    try {
                        viewModel.exportData(Environment.getExternalStorageDirectory().absolutePath)
                        "ok"
                    } catch (e: Exception) {
                        e.message
                    }
                }
                if (task == "ok") {
                    Toasty.success(activity!!.applicationContext, "导出成功").show()
                    dismiss()
                } else {
                    Toasty.error(activity!!.applicationContext, "出现异常>_<\n$task").show()
                }
            }
        }

        tv_share.setOnClickListener {
            tv_share.text = "导出中…请稍候"
            launch {
                val task = withContext(Dispatchers.IO) {
                    try {
                        viewModel.exportData(Environment.getExternalStorageDirectory().absolutePath)
                    } catch (e: Exception) {
                        null
                    }
                }
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

        tv_export_ics.setOnLongClickListener {
            CourseUtils.openUrl(activity!!, "https://www.jianshu.com/p/de3524cbe8aa")
            return@setOnLongClickListener true
        }

        tv_export_ics.setOnClickListener {
            launch {
                tv_export_ics.text = "导出中…请稍候"
                val task = withContext(Dispatchers.IO) {
                    try {
                        viewModel.exportICS(Environment.getExternalStorageDirectory().absolutePath)
                        "ok"
                    } catch (e: Exception) {
                        e.message
                    }
                }
                if (task == "ok") {
                    Toasty.success(activity!!.applicationContext, "导出成功").show()
                    dismiss()
                } else {
                    Toasty.error(activity!!.applicationContext, "出现异常>_<\n$task").show()
                }
            }
        }

        tv_share_ics.setOnClickListener {
            tv_share_ics.text = "导出中…请稍候"
            launch {
                val task = withContext(Dispatchers.IO) {
                    try {
                        viewModel.exportICS(Environment.getExternalStorageDirectory().absolutePath)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (task != null) {
                    Share2.Builder(activity)
                            .setContentType(ShareContentType.FILE)
                            .setShareFileUri(FileUtil.getFileUri(activity, null, File(task)))
                            .setTitle("导出并分享日历文件")
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
