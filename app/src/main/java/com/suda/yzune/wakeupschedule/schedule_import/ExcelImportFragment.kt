package com.suda.yzune.wakeupschedule.schedule_import

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_excel_import.*
import java.util.regex.Pattern

class ExcelImportFragment : BaseFragment() {

    private lateinit var viewModel: ImportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ImportViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_excel_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(context!!.applicationContext, v_status)

        val basePath = Environment.getExternalStorageDirectory().absolutePath

        tv_template.setOnClickListener {
            CourseUtils.openUrl(activity!!, "https://pan.baidu.com/s/1m9gZ-grvQV6S9isu7NeMVQ")
        }

        tv_import.setOnClickListener {
            MaterialFilePicker()
                    .withActivity(activity)
                    .withRequestCode(2)
                    .withPath(basePath)
                    .withFilter(Pattern.compile(".*\\.xlsx$")) // Filtering files and directories by file name using regexp
                    .start()
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//                addCategory(Intent.CATEGORY_OPENABLE)
//                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
//            }
//            startActivityForResult(intent, 2)
        }

        ib_back.setOnClickListener {
            activity!!.finish()
        }
    }

}
