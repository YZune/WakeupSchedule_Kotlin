package com.suda.yzune.wakeupschedule.schedule_import

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.SearchEvent
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.SplashActivity
import com.suda.yzune.wakeupschedule.base_view.BaseActivity
import es.dmoral.toasty.Toasty
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_login_web.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginWebActivity : BaseActivity() {

    private lateinit var viewModel: ImportViewModel

    private var importPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        setContentView(R.layout.activity_login_web)

        viewModel = ViewModelProviders.of(this).get(ImportViewModel::class.java)

        val type = intent.getStringExtra("type")

        if (Fabric.isInitialized()) {
            Answers.getInstance().logSearch(SearchEvent().putQuery(type))
        }
        when {
            type == "苏州大学" -> {
                val fragment = LoginWebFragment.newInstance("苏州大学")
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "sudaLogin")
                transaction.commit()
                showImportSettingDialog()
            }
            type == "清华大学" -> {
                val fragment = LoginWebFragment.newInstance("清华大学")
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "tsinghuaLogin")
                transaction.commit()
                showImportSettingDialog()
            }
            type == "上海大学" -> {
                val fragment = LoginWebFragment.newInstance("上海大学")
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "shanghaiLogin")
                transaction.commit()
                showImportSettingDialog()
            }
            type == "吉林大学" -> {
                val fragment = LoginWebFragment.newInstance("吉林大学")
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "JLU")
                transaction.commit()
                showImportSettingDialog()
            }
            type == "华中科技大学" -> {
                val fragment = LoginWebFragment.newInstance("华中科技大学")
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "HUST")
                transaction.commit()
                showImportSettingDialog()
            }
            type == "apply" -> {
                val fragment = SchoolInfoFragment.newInstance()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "schoolInfo")
                transaction.commit()
            }
            type == "file" -> {
                val fragment = FileImportFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "fileImport")
                transaction.commit()
            }
            type == "excel" -> {
                val fragment = ExcelImportFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "excelImport")
                transaction.commit()
                showImportSettingDialog()
            }
            type == "html" -> {
                val fragment = HtmlImportFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "htmlImport")
                transaction.commit()
                showImportSettingDialog()
            }
            intent.action == Intent.ACTION_VIEW -> {
                val fragment = FileImportFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "fileImport")
                transaction.commit()
                importPath = intent.data!!.path!!.substringAfter("/external_files")
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
                } else {
                    importFromShareFile()
                }
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                val fragment = WebViewLoginFragment.newInstance(type, intent.getStringExtra("url"))
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "webLogin")
                transaction.commit()
                showImportSettingDialog()
            }
        }
    }

    private fun importFromShareFile() {
        launch {
            val import = withContext(Dispatchers.IO) {
                try {
                    viewModel.importFromFile(importPath)
                } catch (e: Exception) {
                    e.message
                }
            }
            when (import) {
                "ok" -> {
                    Toasty.success(applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@LoginWebActivity, SplashActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                else -> Toasty.error(applicationContext, "发生异常>_<\n$import", Toast.LENGTH_LONG).show()
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
            Log.d("路径", filePath!!)
            launch {
                val import = withContext(Dispatchers.IO) {
                    try {
                        viewModel.importFromFile(filePath)
                    } catch (e: Exception) {
                        e.message
                    }
                }
                when (import) {
                    "ok" -> {
                        Toasty.success(applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                    else -> Toasty.error(applicationContext, "发生异常>_<\n$import", Toast.LENGTH_LONG).show()
                }
            }
        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val filePath = data!!.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)
            launch {
                val import = withContext(Dispatchers.IO) {
                    try {
                        viewModel.importFromExcel(filePath)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        e.message
                    }
                }
                when (import) {
                    "ok" -> {
                        Toasty.success(applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                    "something" -> {
                        Toasty.info(applicationContext, "导入后请在右侧栏切换后查看。有部分数据导入失败，可能是某些格子空白导致的，请仔细检查。", Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                    else -> Toasty.error(applicationContext, "发生异常>_<请确保所有应填的格子不为空\n且没有更改模板的属性\n$import", Toast.LENGTH_LONG).show()
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
        val suda = supportFragmentManager.findFragmentByTag("sudaLogin")
        if (suda != null && fab_login.isExpanded) {
            fab_login.isExpanded = false
        } else {
            super.onBackPressed()
        }
    }

}
