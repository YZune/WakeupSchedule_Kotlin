package com.suda.yzune.wakeupschedule.schedule_import

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login_web.*
import com.suda.yzune.wakeupschedule.MainActivity
import android.view.animation.AnimationUtils
import android.view.animation.Animation


class LoginWebActivity : AppCompatActivity() {

    var isInitLoginObserver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_web)

        val viewModel = ViewModelProviders.of(this).get(ImportViewModel::class.java)
        viewModel.getCheckCode().observe(this, Observer {
            if (it != null) {
                rl_progress.visibility = View.GONE
                iv_code.visibility = View.VISIBLE
                iv_error.visibility = View.INVISIBLE
                iv_code.setImageBitmap(it)
            } else {
                rl_progress.visibility = View.GONE
                iv_code.visibility = View.INVISIBLE
                iv_error.visibility = View.VISIBLE
                Toasty.error(this, resources.getString(R.string.check_code_get_error)).show()
            }
        })

        iv_code.setOnClickListener(View.OnClickListener {
            rl_progress.visibility = View.VISIBLE
            iv_code.visibility = View.INVISIBLE
            iv_error.visibility = View.INVISIBLE
            viewModel.getCheckCode()
        })

        iv_error.setOnClickListener(View.OnClickListener {
            refreshCode(viewModel)
        })

        cb_check_pwd.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                et_pwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                et_pwd.setSelection(et_pwd.text.length)
            } else {
                et_pwd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                et_pwd.setSelection(et_pwd.text.length)
            }
        }

        btn_login.setOnClickListener {
            if (!btn_login.isBegin) {
                val shake = AnimationUtils.loadAnimation(this, R.anim.edittext_shake)
                when {
                    et_id.text.isEmpty() -> et_id.startAnimation(shake)
                    et_pwd.text.isEmpty() -> et_pwd.startAnimation(shake)
                    et_code.text.isEmpty() -> et_code.startAnimation(shake)
                    else -> {
                        btn_login.start()
                        if (!isInitLoginObserver){
                            isInitLoginObserver = true
                            initLoginObserver(viewModel, et_id.text.toString(),
                                    et_pwd.text.toString(), et_code.text.toString(), shake)
                        }
                        else{
                            viewModel.login(et_id.text.toString(),
                                    et_pwd.text.toString(), et_code.text.toString())
                        }
                    }
                }
            } else {
                //btn_login.resetWH()
            }
        }
    }

    private fun initLoginObserver(viewModel: ImportViewModel, id: String, pwd: String, code: String, animation: Animation) {
        viewModel.login(id, pwd, code).observe(this, Observer {
            if (it != null) {
                when {
                    it == "Failure" -> {
                        btn_login.resetWH()
                        setLoginText("网络错误 检查是否连接校园网")
                    }
                    it.contains("验证码不正确") -> {
                        et_code.startAnimation(animation)
                        et_code.setText("")
                        btn_login.resetWH()
                        setLoginText("验证码不正确哦")
                        refreshCode(viewModel)
                    }
                    it.contains("密码错误") -> {
                        et_code.setText("")
                        btn_login.resetWH()
                        et_pwd.startAnimation(animation)
                        refreshCode(viewModel)
                        setLoginText("密码错误哦")
                    }
                    it.contains("用户名不存在") -> {
                        et_code.setText("")
                        btn_login.resetWH()
                        et_id.startAnimation(animation)
                        refreshCode(viewModel)
                        setLoginText("看看学号是不是输错啦")
                    }
                    it.contains("欢迎您：") -> {
                        //et_code.setText("")
                        btn_login.resetWH()
                        //refreshCode(viewModel)
                        setLoginText("成功！")
                        Handler().postDelayed({
                            viewModel.toSchedule(id, it, "2016-2017", "1")
                        }, 500)
                    }
                    else -> {
                        et_code.setText("")
                        btn_login.resetWH()
                        refreshCode(viewModel)
                        setLoginText("再试一次看看哦")
                    }
                }
            }
        })
    }

    private fun setLoginText(str: String) {
        btn_login.setBtnString(str)
        Handler().postDelayed({
            btn_login.setBtnString("登录")
        }, 3000)
    }

    private fun refreshCode(viewModel: ImportViewModel) {
        rl_progress.visibility = View.VISIBLE
        iv_code.visibility = View.INVISIBLE
        iv_error.visibility = View.INVISIBLE
        viewModel.getCheckCode()
    }
}
