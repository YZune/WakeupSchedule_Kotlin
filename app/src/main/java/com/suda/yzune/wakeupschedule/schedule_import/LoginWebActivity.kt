package com.suda.yzune.wakeupschedule.schedule_import

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.animation.*
import android.widget.CompoundButton
import android.widget.Toast
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login_web.*
import com.suda.yzune.wakeupschedule.MainActivity
import android.animation.IntEvaluator
import android.arch.lifecycle.LiveData
import android.content.Context
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.CountDownTimer
import android.view.inputmethod.InputMethodManager
import com.suda.yzune.wakeupschedule.AppDatabase

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
                "retry" -> Toasty.info(applicationContext, "需要加工").show()
                "插入异常" -> Toasty.error(applicationContext, "数据插入异常").show()
                else -> Toasty.error(applicationContext, it!!).show()
            }
        })

        val fragment = LoginWebFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_fragment, fragment, "sudaLogin")
        transaction.commit()
    }
}
