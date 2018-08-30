package com.suda.yzune.wakeupschedule.schedule_import

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty

class LoginWebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_web)

        val viewModel = ViewModelProviders.of(this).get(ImportViewModel::class.java)
        viewModel.getImportInfo().observe(this, Observer {
            when (it) {
                "ok" -> {
                    Toasty.success(applicationContext, "导入成功").show()
                    finish()
                }
                "retry" -> Toasty.info(applicationContext, "请到侧栏“反馈”中联系作者").show()
                "插入异常" -> Toasty.error(applicationContext, "数据插入异常").show()
                else -> Toasty.error(applicationContext, it!!).show()
            }
        })

        if (intent.getStringExtra("type") == "suda") {
            val fragment = LoginWebFragment.newInstance()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fl_fragment, fragment, "sudaLogin")
            transaction.commit()
        } else {
            val fragment = WebViewLoginFragment.newInstance(intent.getStringExtra("type"))
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fl_fragment, fragment, "webLogin")
            transaction.commit()
        }


    }
}
