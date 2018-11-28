package com.suda.yzune.wakeupschedule.schedule_import

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import com.suda.yzune.wakeupschedule.BaseActivity
import com.suda.yzune.wakeupschedule.R
import es.dmoral.toasty.Toasty

class LoginWebActivity : BaseActivity() {

    private lateinit var viewModel: ImportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_web)

        viewModel = ViewModelProviders.of(this).get(ImportViewModel::class.java)

//        viewModel.getLastId().observe(this, Observer {
//            if (viewModel.newId == -1) {
//                if (it != null) {
//                    viewModel.newId = it + 1
//                } else {
//                    viewModel.newId = 0
//                }
//            }
//        })

        viewModel.importInfo.observe(this, Observer {
            when (it) {
                "ok" -> {
                    Toasty.success(applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                    finish()
                }
                "retry" -> Toasty.info(applicationContext, "请到侧栏“反馈”中联系作者").show()
                "插入异常" -> Toasty.error(applicationContext, "数据插入异常").show()
                else -> Toasty.error(applicationContext, it!!).show()
            }
        })

        viewModel.fileImportInfo.observe(this, Observer {
            when (it) {
                "ok" -> {
                    Toasty.success(applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                    finish()
                }
                "error" -> {
                    Toasty.success(applicationContext, "导入失败/(ㄒoㄒ)/~~").show()
                }
            }
        })

        when {
            intent.getStringExtra("type") == "苏州大学" -> {
                val fragment = LoginWebFragment.newInstance()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "sudaLogin")
                transaction.commit()
                showImportSettingDialog()
            }
            intent.getStringExtra("type") == "apply" -> {
                val fragment = SchoolInfoFragment.newInstance()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "schoolInfo")
                transaction.commit()
            }
            intent.getStringExtra("type") == "file" -> {
                val fragment = FileImportFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "fileImport")
                transaction.commit()
            }
            intent.action == Intent.ACTION_VIEW -> {
                val fragment = FileImportFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "fileImport")
                transaction.commit()
                viewModel.importFromFile(intent.data!!.path!!)
            }
            else -> {
                val fragment = WebViewLoginFragment.newInstance(intent.getStringExtra("type"), intent.getStringExtra("url"))
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "webLogin")
                transaction.commit()
                showImportSettingDialog()
            }
        }
    }

    private fun showImportSettingDialog() {
        ImportSettingFragment().apply {
            isCancelable = false
        }.show(supportFragmentManager, "showImportSettingDialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val filePath = data!!.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)
            viewModel.importFromFile(filePath)
        }
    }

}
