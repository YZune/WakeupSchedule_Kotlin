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

    private var isInitLoginObserver = false
    private var isInitPrepareObserver = false
    private var isInitScheduleObserver = false
    private var loginBtnOldWidth = 0
    private var loginBtnOldHeight = 0
    private var loginBtnOldRadius = 0f
    private var name = ""
    private var year = ""
    private var term = ""

    private val timer = object : CountDownTimer(3000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            btn_text.text = "登录"
            cv_login.isClickable = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_web)

        val viewModel = ViewModelProviders.of(this).get(ImportViewModel::class.java)

        viewModel.insertResponse.observe(this, Observer {
            if (it != null) {
                Toasty.success(this, it).show()
            }
        })

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
                cv_login.isClickable = false
                btn_text.text = "请检查是否连接校园网"
                timer.start()
//                Handler().postDelayed({
//                    btn_text.text = "登录"
//                    cv_login.isClickable = true
//                }, 3000)
                //Toasty.error(this, resources.getString(R.string.check_code_get_error)).show()
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

        cv_login.setOnClickListener {
            val shake = AnimationUtils.loadAnimation(this, R.anim.edittext_shake)
            when {
                et_id.text.isEmpty() -> et_id.startAnimation(shake)
                et_pwd.text.isEmpty() -> et_pwd.startAnimation(shake)
                et_code.text.isEmpty() -> et_code.startAnimation(shake)
                else -> {
                    cardRe2C()
                    if (!isInitLoginObserver) {
                        isInitLoginObserver = true
                        initLoginObserver(viewModel, et_id.text.toString(),
                                et_pwd.text.toString(), et_code.text.toString(), shake)
                    } else {
                        viewModel.login(et_id.text.toString(),
                                et_pwd.text.toString(), et_code.text.toString())
                    }
                }
            }
        }
    }

    private fun initLoginObserver(viewModel: ImportViewModel, id: String, pwd: String, code: String, animation: Animation) {
        viewModel.login(id, pwd, code).observe(this, Observer {
            if (it != null) {
                when {
                    it == "Failure" -> {
                        cardC2Re("请检查是否连接校园网")
                    }
                    it.contains("验证码不正确") -> {
                        et_code.startAnimation(animation)
                        et_code.setText("")
                        cardC2Re("验证码不正确哦")
                        refreshCode(viewModel)
                    }
                    it.contains("密码错误") -> {
                        et_code.setText("")
                        et_pwd.startAnimation(animation)
                        refreshCode(viewModel)
                        cardC2Re("密码错误哦")
                    }
                    it.contains("用户名不存在") -> {
                        et_code.setText("")
                        et_id.startAnimation(animation)
                        refreshCode(viewModel)
                        cardC2Re("看看学号是不是输错啦")
                    }
                    it.contains("欢迎您：") -> {
                        //et_code.setText("")
                        //btn_login.resetWH()
                        //setLoginText("成功！")
//                        val intent = Intent(this, ChooseTermActivity::class.java)
//                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, btn_login, "btn_to_dialog").toBundle())
                        if (!isInitPrepareObserver) {
                            isInitPrepareObserver = true
                            initPrepareObserver(viewModel, et_id.text.toString())
                        } else {
                            viewModel.getPrepare(et_id.text.toString())
                        }
//                        Handler().postDelayed({
//                            viewModel.toSchedule(id, it, "2016-2017", "1")
//                        }, 500)
                    }
                    else -> {
                        et_code.setText("")
                        refreshCode(viewModel)
                        cardC2Re("再试一次看看哦")
                    }
                }
            }
        })
    }

    private fun initPrepareObserver(viewModel: ImportViewModel, id: String) {
        viewModel.getPrepare(id).observe(this, Observer {
            var years: List<String>? = null
            if (it != null) {
                years = viewModel.parseYears(it)
                name = viewModel.parseName(it)
            }
            if (years == null) {
                cardC2Re("获取学期数据失败:(")
            } else {
                cardC2Dialog(viewModel, years)
            }
        })
    }

    private fun initScheduleObserver(viewModel: ImportViewModel, id: String, name: String, year: String, term: String) {
        viewModel.toSchedule(id, name, year, term).observe(this, Observer {
            if (it == null || it == "Failure") {
                cardC2Re("网络错误")
            } else {
                Log.d("数据库", "插入")
                Log.d("课表", viewModel.html2ImportBean(it).toString())
                //viewModel.importBean2CourseBean(viewModel.html2ImportBean(it), "$year ${if (term.isBlank()) "1" else term}", applicationContext)
                //todo: 多课表管理
                viewModel.importBean2CourseBean(viewModel.html2ImportBean(it), "", applicationContext)
            }
        })
    }

    private fun refreshCode(viewModel: ImportViewModel) {
        rl_progress.visibility = View.VISIBLE
        iv_code.visibility = View.INVISIBLE
        iv_error.visibility = View.INVISIBLE
        viewModel.getCheckCode()
    }

    private fun widthAnimation(target: View, start: Int, end: Int, duration: Int, delay: Long) {
        val valueAnimator = ValueAnimator.ofInt(1, 100)
        valueAnimator.addUpdateListener { animation ->
            val intEvaluator = IntEvaluator()
            val fraction = animation.animatedFraction
            target.layoutParams.width = intEvaluator.evaluate(fraction, start, end)!!
            target.requestLayout()
        }
        valueAnimator.startDelay = delay
        valueAnimator.setDuration(duration.toLong()).start()
    }

    private fun heightAnimation(target: View, start: Int, end: Int, duration: Int, delay: Long) {
        val valueAnimator = ValueAnimator.ofInt(1, 100)
        valueAnimator.addUpdateListener { animation ->
            val intEvaluator = IntEvaluator()
            val fraction = animation.animatedFraction
            target.layoutParams.height = intEvaluator.evaluate(fraction, start, end)!!
            target.requestLayout()
        }
        valueAnimator.interpolator = OvershootInterpolator()
        valueAnimator.startDelay = delay
        valueAnimator.setDuration(duration.toLong()).start()
    }

    private fun fadeAnimation(target: View, start: Float, end: Float, duration: Long, delay: Long) {
        val fadeAnimator = ObjectAnimator.ofFloat(target, "alpha", start, end)
        fadeAnimator.duration = duration
        fadeAnimator.startDelay = delay
        fadeAnimator.start()
    }

    private fun cardRe2C() {
        loginBtnOldWidth = cv_login.width
        loginBtnOldHeight = cv_login.height
        loginBtnOldRadius = cv_login.radius
        widthAnimation(cv_login, loginBtnOldWidth, cv_login.height, 300, 0)
        fadeAnimation(btn_text, 1f, 0f, 200, 0)
        fadeAnimation(cpb, 0f, 1f, 100, 0)
        cv_login.isClickable = false
//        val valueAnimator = ValueAnimator.ofInt(1, 100)
//        valueAnimator.addUpdateListener { animation ->
//            val intEvaluator = IntEvaluator()
//            val fraction = animation.animatedFraction
//            cv_login.layoutParams.width = intEvaluator.evaluate(fraction, loginBtnOldWidth, cv_login.height)!!
//            btn_text.alpha = (cv_login.layoutParams.width - cv_login.height) / (loginBtnOldWidth - cv_login.height) * 1.0f
//            cpb.alpha = 1 - (cv_login.layoutParams.width - cv_login.height) / (loginBtnOldWidth - cv_login.height) * 1.0f
//            cv_login.requestLayout()
//        }
//        valueAnimator.duration = 300
    }

    private fun cardC2Re(msg: String) {
        cv_login.isClickable = false
        widthAnimation(cv_login, cv_login.height, loginBtnOldWidth, 300, 0)
        fadeAnimation(cpb, 1f, 0f, 100, 0)
        btn_text.text = msg
        fadeAnimation(btn_text, 0f, 1f, 200, 0)
//        Handler().postDelayed({
//            btn_text.text = "登录"
//            cv_login.isClickable = true
//        }, 3000)
        timer.start()
    }

    private fun cardC2Dialog(viewModel: ImportViewModel, years: List<String>) {
        cv_login.isClickable = false
        widthAnimation(cv_login, cv_login.height, loginBtnOldWidth, 300, 0)
        fadeAnimation(cpb, 1f, 0f, 100, 0)
        cv_login.pivotY = 0f
        val raiseUp = ObjectAnimator.ofFloat(cv_login, "translationY", 0f, -320f)
        raiseUp.interpolator = OvershootInterpolator()
        raiseUp.duration = 500
        raiseUp.start()
        heightAnimation(cv_login, cv_login.height, 6 * cv_login.height, 300, 0)

        val radiusOff = ObjectAnimator.ofFloat(cv_login, "radius", cv_login.radius, 2f)
        radiusOff.duration = 500
        radiusOff.start()
        rl_login.isClickable = true
        iv_mask.visibility = View.VISIBLE
        val maskOn = ObjectAnimator.ofFloat(iv_mask, "alpha", 0f, 1f)
        maskOn.duration = 500
        maskOn.start()

        ll_dialog.visibility = View.VISIBLE
        val terms = listOf<String>("1", "2", "3")
        wp_term.data = terms
        wp_years.data = years
        wp_years.setOnItemSelectedListener { _, data, _ ->
            year = data as String
            Log.d("选中", "选中学年$year")
        }
        wp_term.setOnItemSelectedListener { _, data, _ ->
            term = data as String
            Log.d("选中", "选中学期$term")
        }

        val contentOn = ObjectAnimator.ofFloat(ll_dialog, "alpha", 0f, 1f)
        contentOn.duration = 500
        contentOn.start()

        cv_to_schedule.setOnClickListener(View.OnClickListener {
            cardDialog2C()
            if (!isInitScheduleObserver) {
                isInitScheduleObserver = true
                initScheduleObserver(viewModel, et_id.text.toString(), name, year, term)
            } else {
                viewModel.toSchedule(et_id.text.toString(), name, year, term)
            }
        })
    }

    private fun cardDialog2C() {
        cv_login.isClickable = false
        widthAnimation(cv_login, loginBtnOldWidth, loginBtnOldHeight, 300, 0)
        fadeAnimation(cpb, 0f, 1f, 100, 0)
        cv_login.pivotY = 0f
        heightAnimation(cv_login, cv_login.height, loginBtnOldHeight, 300, 0)

        val radiusOn = ObjectAnimator.ofFloat(cv_login, "radius", cv_login.radius, loginBtnOldRadius)
        radiusOn.duration = 100
        radiusOn.start()
        rl_login.isClickable = true
        iv_mask.visibility = View.GONE

        ll_dialog.visibility = View.GONE

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}
