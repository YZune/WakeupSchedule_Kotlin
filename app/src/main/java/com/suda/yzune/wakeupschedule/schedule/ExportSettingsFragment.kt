package com.suda.yzune.wakeupschedule.schedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.activityViewModels
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.Const
import com.suda.yzune.wakeupschedule.utils.Utils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_export_settings.*

class ExportSettingsFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_export_settings

    private val viewModel by activityViewModels<ScheduleViewModel>()

    val tableName by lazy(LazyThreadSafetyMode.NONE) {
        if (viewModel.table.tableName == "") {
            "我的课表"
        } else {
            viewModel.table.tableName
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        tv_export.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_TITLE, "$tableName.wakeup_schedule")
            }
            Toasty.info(activity!!, "请自行选择导出的地方\n不要修改文件的扩展名哦", Toasty.LENGTH_LONG).show()
            activity?.startActivityForResult(intent, Const.REQUEST_CODE_EXPORT)
            dismiss()
        }

        tv_export_ics.setOnLongClickListener {
            Utils.openUrl(activity!!, "https://www.jianshu.com/p/de3524cbe8aa")
            return@setOnLongClickListener true
        }

        tv_export_ics.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/calendar"
                putExtra(Intent.EXTRA_TITLE, "日历-$tableName")
            }
            Toasty.info(activity!!, "请自行选择导出的地方\n不要修改文件的扩展名哦", Toasty.LENGTH_LONG).show()
            activity?.startActivityForResult(intent, Const.REQUEST_CODE_EXPORT_ICS)
            dismiss()
        }

        tv_cancel.setOnClickListener {
            dismiss()
        }
    }
}
