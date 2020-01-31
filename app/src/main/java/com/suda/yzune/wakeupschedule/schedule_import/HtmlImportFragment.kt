package com.suda.yzune.wakeupschedule.schedule_import

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_html_import.*
import splitties.snackbar.longSnack
import java.io.File
import java.nio.charset.Charset

class HtmlImportFragment : BaseFragment() {

    private val viewModel by activityViewModels<ImportViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_html_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(context!!.applicationContext, v_status)

        tv_way.setOnClickListener {
            CourseUtils.openUrl(activity!!, "https://www.jianshu.com/p/4cd071697fed")
        }

        tv_type.setOnClickListener {
            startActivityForResult(Intent(activity, SchoolListActivity::class.java).apply {
                putExtra("fromLocal", true)
            }, 4)
        }

        cp_utf.isChecked = true

        cp_utf.setOnClickListener {
            cp_utf.isChecked = true
            cp_gbk.isChecked = false
        }

        cp_gbk.setOnClickListener {
            cp_gbk.isChecked = true
            cp_utf.isChecked = false
        }

        var qzChipId = 0
        cg_qz.setOnCheckedChangeListener { chipGroup, id ->
            when (id) {
                R.id.chip_qz1 -> {
                    qzChipId = id
                    viewModel.qzType = 0
                }
                R.id.chip_qz2 -> {
                    qzChipId = id
                    viewModel.qzType = 1
                }
                R.id.chip_qz3 -> {
                    qzChipId = id
                    viewModel.qzType = 2
                }
                R.id.chip_qz4 -> {
                    qzChipId = id
                    viewModel.qzType = 3
                }
                else -> {
                    chipGroup.findViewById<Chip>(qzChipId).isChecked = true
                }
            }
        }

        var zfChipId = 0
        cg_zf.setOnCheckedChangeListener { chipGroup, id ->
            when (id) {
                R.id.chip_zf1 -> {
                    zfChipId = id
                    viewModel.zfType = 0
                }
                R.id.chip_zf2 -> {
                    zfChipId = id
                    viewModel.zfType = 1
                }
                else -> {
                    chipGroup.findViewById<Chip>(zfChipId).isChecked = true
                }
            }
        }

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

        val wechatPath = if (basePath.endsWith(File.separator)) {
            "${basePath}tencent/micromsg/Download/"
        } else {
            "$basePath/tencent/micromsg/Download/"
        }

        tv_qq.setOnClickListener {
            showFilePicker(qqPath)
        }

        tv_tim.setOnClickListener {
            showFilePicker(timPath)
        }

        tv_wechat.setOnClickListener {
            showFilePicker(wechatPath)
        }

        tv_self.setOnClickListener {
            showFilePicker(basePath)
        }

        ib_back.setOnClickListener {
            activity!!.finish()
        }

        fab_import.setOnClickListener {
            if (viewModel.htmlPath.isBlank()) {
                it.longSnack("还没有选择文件呢>_<")
                return@setOnClickListener
            }
            launch {
                try {
                    val html = File(viewModel.htmlPath).readText(if (cp_utf.isChecked) Charsets.UTF_8 else Charset.forName("gbk"))
                    val result = viewModel.importSchedule(html)
                    Toasty.success(activity!!,
                            "成功导入 $result 门课程(ﾟ▽ﾟ)/\n请在右侧栏切换后查看").show()
                    activity!!.setResult(RESULT_OK)
                    activity!!.finish()
                } catch (e: Exception) {
                    Toasty.error(activity!!,
                            "导入失败>_<\n${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showFilePicker(path: String) {
        if (viewModel.htmlName.isBlank()) {
            view!!.longSnack("请先点击第二个按钮选择类型哦")
        } else {
            MaterialFilePicker()
                    .withSupportFragment(this)
                    .withRequestCode(3)
                    .withPath(path)
                    .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 3 && resultCode == RESULT_OK) {
            viewModel.htmlPath = data!!.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)
        }
        if (requestCode == 4 && resultCode == RESULT_OK) {
            viewModel.htmlName = data!!.getStringExtra("name")
            when (viewModel.htmlName) {
                "正方教务" -> {
                    chip_zf1.isChecked = true
                    cg_qz.visibility = View.GONE
                    cg_zf.visibility = View.VISIBLE
                }
                "强智教务" -> {
                    chip_qz1.isChecked = true
                    cg_qz.visibility = View.VISIBLE
                    cg_zf.visibility = View.GONE
                }
                else -> {
                    cg_qz.visibility = View.GONE
                    cg_zf.visibility = View.GONE
                }
            }
        }
    }

}
