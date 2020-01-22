package com.suda.yzune.wakeupschedule.schedule

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.BaseDialogFragment
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.schedule_import.LoginWebActivity
import com.suda.yzune.wakeupschedule.schedule_import.SchoolListActivity
import com.suda.yzune.wakeupschedule.schedule_import.bean.SchoolInfo
import kotlinx.android.synthetic.main.fragment_import_choose.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class ImportChooseFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_import_choose

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var importSchool: SchoolInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

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
            if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                dismiss()
            } else {
                activity!!.startActivityForResult<LoginWebActivity>(32, "import_type" to "file")
                dismiss()
            }
        }

        tv_html.setOnClickListener {
            if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 4)
                dismiss()
            } else {
                activity!!.startActivityForResult<LoginWebActivity>(32, "import_type" to "html", "tableId" to viewModel.table.id)
                dismiss()
            }
        }

        tv_excel.setOnClickListener {
            if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 3)
                dismiss()
            } else {
                activity!!.startActivityForResult<LoginWebActivity>(32, "import_type" to "excel", "tableId" to viewModel.table.id)
                dismiss()
            }
        }

        tv_school.text = "${importSchool.name}导入"
        tv_school.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            activity!!.startActivityForResult<LoginWebActivity>(
                    32,
                    "school_name" to importSchool.name,
                    "tableId" to viewModel.table.id,
                    "import_type" to importSchool.type,
                    "url" to importSchool.url
            )
            dismiss()
        }

        tv_more.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            activity!!.startActivityForResult<SchoolListActivity>(32)
            dismiss()
        }

        tv_feedback.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            activity!!.startActivity<LoginWebActivity>("import_type" to "apply")
            dismiss()
        }
    }

}
