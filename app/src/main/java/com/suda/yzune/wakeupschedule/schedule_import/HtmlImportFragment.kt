package com.suda.yzune.wakeupschedule.schedule_import


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_html_import.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivityForResult
import java.io.File
import java.nio.charset.Charset

class HtmlImportFragment : BaseFragment() {

    private lateinit var viewModel: ImportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ImportViewModel::class.java)
    }

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
            startActivityForResult<SchoolListActivity>(4, "fromLocal" to true)
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
                R.id.chip_qz5 -> {
                    qzChipId = id
                    viewModel.qzType = 4
                }
                R.id.chip_qz6 -> {
                    qzChipId = id
                    viewModel.qzType = 5
                }
                R.id.chip_qz7 -> {
                    qzChipId = id
                    viewModel.qzType = 6
                }
                else -> {
                    chipGroup.find<Chip>(qzChipId).isChecked = true
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
                    chipGroup.find<Chip>(zfChipId).isChecked = true
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
                it.longSnackbar("还没有选择文件呢>_<")
                return@setOnClickListener
            }
            launch {
                val task = withContext(Dispatchers.IO) {
                    try {
                        val html = File(viewModel.htmlPath).readText(if (cp_utf.isChecked) Charsets.UTF_8 else Charset.forName("gbk"))
                        when (viewModel.htmlName) {
                            "北京大学" -> viewModel.parsePeking(html)
                            "苏州大学" -> viewModel.importBean2CourseBean(viewModel.html2ImportBean(html), html)
                            "北京师范大学珠海分校" -> viewModel.parseZFNewer(html)
                            //"吉林大学" -> viewModel.convertJLU(JSONObject(html))
                            in viewModel.oldQZList1 -> viewModel.parseOldQZ1(html)
                            in viewModel.urpList -> viewModel.parseURP(html)
                            in viewModel.oldQZList -> viewModel.parseOldQZ(html)
                            in viewModel.gzChengFangList -> viewModel.parseGuangGong(html)
                            "正方教务" -> viewModel.importBean2CourseBean(viewModel.html2ImportBean(html), html)
                            "新正方教务" -> viewModel.parseNewZF(html)
                            "强智教务" -> {
                                when (viewModel.qzType) {
                                    0 -> viewModel.parseQZ(html, "北京林业大学")
                                    1 -> viewModel.parseQZ(html, "广东外语外贸大学")
                                    2 -> viewModel.parseQZ(html, "长春大学")
                                    3 -> viewModel.parseQZ(html, "青岛农业大学")
                                    4 -> viewModel.parseQZ(html, "锦州医科大学")
                                    5 -> viewModel.parseQZ(html, "山东科技大学")
                                    6 -> viewModel.parseQZ(html, "佛山科学技术学院")
                                    else -> "没有贵校的信息哦>_<"
                                }
                            }
                            "长春大学" -> viewModel.parseQZ(html, viewModel.htmlName)
                            "湖南信息职业技术学院" -> viewModel.parseHNIU(html)
                            in viewModel.qzAbnormalNodeList -> viewModel.parseQZ(html, viewModel.htmlName)
                            in viewModel.qzGuangwaiList -> viewModel.parseQZ(html, viewModel.htmlName)
                            in viewModel.ZFSchoolList -> {
                                viewModel.zfType = 0
                                viewModel.importBean2CourseBean(viewModel.html2ImportBean(html), html)
                            }
                            in viewModel.ZFSchoolList1 -> {
                                viewModel.zfType = 1
                                viewModel.importBean2CourseBean(viewModel.html2ImportBean(html), html)
                            }
                            in viewModel.newZFSchoolList -> viewModel.parseNewZF(html)
                            in viewModel.qzLessNodeSchoolList -> viewModel.parseQZ(html, viewModel.htmlName)
                            in viewModel.qzMoreNodeSchoolList -> viewModel.parseQZ(html, viewModel.htmlName)
                            else -> "没有贵校的信息哦>_<"
                        }
                    } catch (e: Exception) {
                        "导入失败>_<\n请尝试更换编码格式\n${e.message}"
                    }
                }

                when (task) {
                    "ok" -> {
                        Toasty.success(activity!!.applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看").show()
                        activity!!.setResult(RESULT_OK)
                        activity!!.finish()
                    }
                    else -> Toasty.error(activity!!.applicationContext, task, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showFilePicker(path: String) {
        if (viewModel.htmlName.isBlank()) {
            view!!.longSnackbar("请先点击第二个按钮选择类型哦")
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
