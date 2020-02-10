package com.suda.yzune.wakeupschedule.schedule_import

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import com.suda.yzune.wakeupschedule.SplashActivity
import com.suda.yzune.wakeupschedule.base_view.BaseActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_login_web.*

class LoginWebActivity : BaseActivity() {

    private val viewModel by viewModels<ImportViewModel>()
    private var importPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.getString("import_type")?.let {
            viewModel.importType = it
        }
        intent.extras?.getString("school_name")?.let {
            viewModel.school = it
        }

        val fragment = when (viewModel.importType) {
            "login" -> {
                LoginWebFragment()
            }
            "apply" -> {
                SchoolInfoFragment()
            }
            "file" -> {
                FileImportFragment()
            }
            "excel" -> {
                ExcelImportFragment()
            }
            "html" -> {
                HtmlImportFragment()
            }
            else -> {
                if (viewModel.importType.isNullOrEmpty() || viewModel.school.isNullOrEmpty()) {
                    null
                } else {
                    //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    WebViewLoginFragment.newInstance(intent.getStringExtra("url"))
                }
            }
        }
        fragment?.let { frag ->
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(android.R.id.content, frag, viewModel.school)
            transaction.commit()
            if (viewModel.importType != "apply" || viewModel.importType != "file") {
                showImportSettingDialog()
            }
        }

        if (fragment == null && intent.action == Intent.ACTION_VIEW) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(android.R.id.content, FileImportFragment(), null)
            transaction.commit()
            importPath = intent.data!!.path!!.substringAfter("/external_files")
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
            } else {
                importFromShareFile()
            }
        }
    }


    private fun importFromShareFile() {
        launch {
            try {
                viewModel.importFromFile(importPath)
                Toasty.success(applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                val intent = Intent(this@LoginWebActivity, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toasty.error(applicationContext, "发生异常>_<\n${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showImportSettingDialog() {
        ImportSettingFragment().apply {
            isCancelable = false
        }.show(supportFragmentManager, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val filePath = data!!.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)
            Log.d("路径", filePath!!)
            launch {
                try {
                    viewModel.importFromFile(filePath)
                    Toasty.success(applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } catch (e: Exception) {
                    Toasty.error(applicationContext, "发生异常>_<\n${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val filePath = data!!.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)
            launch {
                try {
                    viewModel.importFromExcel(filePath)
                    Toasty.success(applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } catch (e: Exception) {
                    Toasty.error(applicationContext, "发生异常>_<请确保所有应填的格子不为空\n且没有更改模板的属性\n${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    importFromShareFile()
                } else {
                    Toasty.error(applicationContext, "你取消了授权>_<无法导出", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        val suda = supportFragmentManager.findFragmentByTag("苏州大学")
        if (suda != null && fab_login.isExpanded) {
            fab_login.isExpanded = false
        } else {
            super.onBackPressed()
        }
    }

}
