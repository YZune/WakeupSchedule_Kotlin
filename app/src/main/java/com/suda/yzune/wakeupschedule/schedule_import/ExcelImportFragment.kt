package com.suda.yzune.wakeupschedule.schedule_import

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.utils.Utils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_excel_import.*
import java.io.File
import java.util.regex.Pattern

class ExcelImportFragment : BaseFragment() {

    private val viewModel by activityViewModels<ImportViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_excel_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(context!!, v_status)

        val basePath = Environment.getExternalStorageDirectory().absolutePath

        tv_template.setOnClickListener {
            Utils.openUrl(activity!!, "https://pan.baidu.com/s/1m9gZ-grvQV6S9isu7NeMVQ")
        }

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

        val wechatPath = if (basePath.endsWith(File.separator)) {
            "${basePath}tencent/micromsg/Download/"
        } else {
            "$basePath/tencent/micromsg/Download/"
        }

        tv_qq.setOnClickListener {
            MaterialFilePicker()
                    .withActivity(activity)
                    .withRequestCode(2)
                    .withPath(qqPath)
                    .withFilter(Pattern.compile(".*\\.csv$")) // Filtering files and directories by file name using regexp
                    .start()
        }

        tv_tim.setOnClickListener {
            MaterialFilePicker()
                    .withActivity(activity)
                    .withRequestCode(2)
                    .withPath(timPath)
                    .withFilter(Pattern.compile(".*\\.csv$")) // Filtering files and directories by file name using regexp
                    .start()
        }

        tv_wechat.setOnClickListener {
            MaterialFilePicker()
                    .withActivity(activity)
                    .withRequestCode(2)
                    .withPath(wechatPath)
                    .withFilter(Pattern.compile(".*\\.csv$")) // Filtering files and directories by file name using regexp
                    .start()
        }

        tv_self.setOnClickListener {
            MaterialFilePicker()
                    .withActivity(activity)
                    .withRequestCode(2)
                    .withPath(basePath)
                    .withFilter(Pattern.compile(".*\\.csv$")) // Filtering files and directories by file name using regexp
                    .start()
        }

        ib_back.setOnClickListener {
            activity!!.finish()
        }
    }

}
