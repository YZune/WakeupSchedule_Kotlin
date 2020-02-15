package com.suda.yzune.wakeupschedule.schedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.schedule_import.LoginWebActivity
import com.suda.yzune.wakeupschedule.schedule_import.SchoolListActivity
import com.suda.yzune.wakeupschedule.schedule_import.bean.SchoolInfo
import com.suda.yzune.wakeupschedule.utils.Const
import kotlinx.android.synthetic.main.fragment_import_choose.*
import splitties.activities.start

class ImportChooseFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_import_choose

    private val viewModel by activityViewModels<ScheduleViewModel>()
    private lateinit var importSchool: SchoolInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        importSchool = viewModel.getImportSchoolBean()
        initEvent()
    }

    private fun initEvent() {
        ib_close.setOnClickListener {
            dismiss()
        }

        tv_file.setOnClickListener {
            showSAFTips {
                activity!!.startActivityForResult(
                        Intent(activity, LoginWebActivity::class.java).apply {
                            putExtra("import_type", "file")
                        },
                        Const.REQUEST_CODE_IMPORT)
                this.dismiss()
            }
        }

        tv_html.setOnClickListener {
            showSAFTips {
                activity!!.startActivityForResult(
                        Intent(activity, LoginWebActivity::class.java).apply {
                            putExtra("import_type", "html")
                            putExtra("tableId", viewModel.table.id)
                        },
                        Const.REQUEST_CODE_IMPORT)
                this.dismiss()
            }
        }

        tv_excel.setOnClickListener {
            showSAFTips {
                activity!!.startActivityForResult(
                        Intent(activity, LoginWebActivity::class.java).apply {
                            putExtra("import_type", "excel")
                            putExtra("tableId", viewModel.table.id)
                        },
                        Const.REQUEST_CODE_IMPORT)
                this.dismiss()
            }
        }

        tv_school.text = "${importSchool.name}导入"
        tv_school.setOnClickListener {
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            activity!!.startActivityForResult(
                    Intent(activity, LoginWebActivity::class.java).apply {
                        putExtra("import_type", importSchool.type)
                        putExtra("tableId", viewModel.table.id)
                        putExtra("school_name", importSchool.name)
                        putExtra("url", importSchool.url)
                    },
                    Const.REQUEST_CODE_IMPORT)
            dismiss()
        }

        tv_more.setOnClickListener {
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            activity!!.startActivityForResult(
                    Intent(activity, SchoolListActivity::class.java),
                    Const.REQUEST_CODE_IMPORT)
            dismiss()
        }

        tv_feedback.setOnClickListener {
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            activity!!.start<LoginWebActivity> {
                putExtra("import_type", "apply")
            }
            dismiss()
        }
    }

    private fun showSAFTips(block: () -> Unit) {
        MaterialAlertDialogBuilder(activity)
                .setTitle("提示")
                .setMessage("为了避免使用敏感的外部存储读写权限，本应用采用了系统级的文件选择器来选择文件。如果找不到路径，请点选择器右上角的三个点，选择「显示内部存储设备」，然后通过侧栏选择路径。")
                .setPositiveButton(R.string.sure) { _, _ ->
                    block.invoke()
                }
                .show()
    }

}
