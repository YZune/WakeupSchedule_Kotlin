package com.suda.yzune.wakeupschedule.schedule_import


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_file_import.*
import java.io.File
import java.util.regex.Pattern


class FileImportFragment : Fragment() {

    private lateinit var viewModel: ImportViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(activity!!).get(ImportViewModel::class.java)
        return inflater.inflate(R.layout.fragment_file_import, container, false)
    }

    override fun onStart() {
        super.onStart()
        ViewUtils.resizeStatusBar(context!!.applicationContext, v_status)

        val basePath = Environment.getExternalStorageDirectory().absolutePath

        val qqPath = if (basePath.endsWith(File.separator)) {
            "${basePath}tencent/QQfile_recv/"
        } else {
            "$basePath/tencent/QQfile_recv/"
        }

        val timPath = if (basePath.endsWith(File.separator)) {
            "${basePath}tencent/TIMfile_recv/"
        } else {
            "$basePath/tencent/TIMfile_recv/"
        }

        tv_qq.setOnClickListener {
            MaterialFilePicker()
                    .withActivity(activity)
                    .withRequestCode(1)
                    .withPath(qqPath)
                    .withFilter(Pattern.compile(".*\\.wakeup_schedule$")) // Filtering files and directories by file name using regexp
                    .start()
        }

        tv_tim.setOnClickListener {
            MaterialFilePicker()
                    .withActivity(activity)
                    .withRequestCode(1)
                    .withPath(timPath)
                    .withFilter(Pattern.compile(".*\\.wakeup_schedule$")) // Filtering files and directories by file name using regexp
                    .start()
        }

        tv_self.setOnClickListener {
            MaterialFilePicker()
                    .withActivity(activity)
                    .withRequestCode(1)
                    .withPath(basePath)
                    .withFilter(Pattern.compile(".*\\.wakeup_schedule$")) // Filtering files and directories by file name using regexp
                    .start()
        }

        ib_back.setOnClickListener {
            activity!!.finish()
        }
    }
}
