package com.suda.yzune.wakeupschedule.schedule_import

import android.animation.IntEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.view.animation.Transformation
import android.widget.RelativeLayout
import android.widget.Toast
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_login_web.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginWebFragment : BaseFragment() {

    private lateinit var cvLoginLayoutParams: RelativeLayout.LayoutParams
    private var loginBtnOldWidth = 0
    private var loginBtnOldHeight = 0
    private var loginBtnOldRadius = 0f
    private var name = ""
    private var year = ""
    private var term = ""
    private val years = arrayListOf<String>()

    private lateinit var viewModel: ImportViewModel

    private val timer = object : CountDownTimer(3000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            btn_text.text = "登录"
            cv_login.isClickable = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ImportViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshCode()
        initEvent()
    }

    private fun initEvent() {
        tv_vpn.setOnClickListener {
            CourseUtils.openUrl(context!!, "https://yzune.github.io/2018/08/13/%E4%BD%BF%E7%94%A8FortiClient%E8%BF%9E%E6%8E%A5%E6%A0%A1%E5%9B%AD%E7%BD%91/")
        }

        cvLoginLayoutParams = cv_login.layoutParams as RelativeLayout.LayoutParams

        iv_code.setOnClickListener {
            refreshCode()
        }

        iv_error.setOnClickListener {
            refreshCode()
        }

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
            val shake = AnimationUtils.loadAnimation(context, R.anim.edittext_shake)
            when {
                et_id.text.isEmpty() -> et_id.startAnimation(shake)
                et_pwd.text.isEmpty() -> et_pwd.startAnimation(shake)
                et_code.text.isEmpty() -> et_code.startAnimation(shake)
                else -> {
                    cardRe2C()
                    launch {
                        val task = async(Dispatchers.IO) {
                            try {
                                viewModel.login(et_id.text.toString(),
                                        et_pwd.text.toString(), et_code.text.toString())
                            } catch (e: Exception) {
                                e.message
                            }
                        }.await()
                        when {
                            task == null -> {
                                cardC2Re("请检查是否连接校园网")
                            }
                            task == "error" -> {
                                cardC2Re("请检查是否连接校园网")
                            }
                            task.contains("验证码不正确") -> {
                                et_code.startAnimation(shake)
                                et_code.setText("")
                                cardC2Re("验证码不正确哦")
                                refreshCode()
                            }
                            task.contains("密码错误") -> {
                                et_code.setText("")
                                et_pwd.startAnimation(shake)
                                refreshCode()
                                cardC2Re("密码错误哦")
                            }
                            task.contains("用户名不存在") -> {
                                et_code.setText("")
                                et_id.startAnimation(shake)
                                refreshCode()
                                cardC2Re("看看学号是不是输错啦")
                            }
                            task.contains("欢迎您：") -> {
                                getPrepared(et_id.text.toString())
                            }
                            task.contains("同学，你好") -> {
                                getPrepared(et_id.text.toString())
                            }
                            task.contains("请耐心排队") -> {
                                et_code.setText("")
                                refreshCode()
                                cardC2Re("选课排队中，稍后再试哦")
                            }
                            else -> {
                                et_code.setText("")
                                refreshCode()
                                cardC2Re("再试一次看看哦")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getPrepared(id: String) {
        launch {
            val task = async(Dispatchers.IO) {
                try {
                    viewModel.getPrepare(id)
                } catch (e: Exception) {
                    e.message!!
                }
            }.await()

            if (task != "error") {
                years.clear()
                years.addAll(viewModel.parseYears(task)!!)
                name = viewModel.parseName(task)
            }

            if (years.isEmpty()) {
                cardC2Re("获取学期数据失败:(")
            } else {
                cardC2Dialog(viewModel, years)
            }
        }
    }

    private fun getSchedule(viewModel: ImportViewModel, id: String, name: String, year: String, term: String) {
        if (year == viewModel.selectedYear && term == viewModel.selectedTerm) {
            launch {
                val import = async(Dispatchers.IO) {
                    try {
                        viewModel.importBean2CourseBean(viewModel.html2ImportBean(viewModel.selectedSchedule), viewModel.selectedSchedule)
                    } catch (e: Exception) {
                        e.message
                    }
                }.await()
                when (import) {
                    "ok" -> {
                        Toasty.success(activity!!.applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                        activity!!.finish()
                    }
                    else -> Toasty.error(activity!!.applicationContext, "发生异常>_<\n$import", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            launch {
                val task = async(Dispatchers.IO) {
                    try {
                        viewModel.toSchedule(id, name, year, term)
                    } catch (e: Exception) {
                        e.message
                    }
                }.await()

                if (task == null || task == "error") {
                    cardC2Re("网络错误")
                } else if (task.contains("您本学期课所选学分小于 0分")) {
                    cardC2Dialog(viewModel, years)
                    Toasty.error(context!!.applicationContext, "该学期貌似还没有课程").show()
                } else {
                    val import = async(Dispatchers.IO) {
                        try {
                            viewModel.importBean2CourseBean(viewModel.html2ImportBean(task), task)
                        } catch (e: Exception) {
                            e.message
                        }
                    }.await()
                    when (import) {
                        "ok" -> {
                            Toasty.success(activity!!.applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                            activity!!.setResult(RESULT_OK)
                            activity!!.finish()
                        }
                        else -> Toasty.error(activity!!.applicationContext, "发生异常>_<\n$import", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun refreshCode() {
        launch {
            rl_progress.visibility = View.VISIBLE
            iv_code.visibility = View.INVISIBLE
            iv_error.visibility = View.INVISIBLE
            val task = async(Dispatchers.IO) {
                try {
                    viewModel.getCheckCode()
                } catch (e: Exception) {
                    null
                }
            }.await()
            if (task != null) {
                rl_progress.visibility = View.GONE
                iv_code.visibility = View.VISIBLE
                iv_error.visibility = View.INVISIBLE
                iv_code.setImageBitmap(task)
            } else {
                rl_progress.visibility = View.GONE
                iv_code.visibility = View.INVISIBLE
                iv_error.visibility = View.VISIBLE
                cv_login.isClickable = false
                btn_text.text = "请检查是否连接校园网"
                timer.start()
            }
        }
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

//        cvLoginLayoutParams.topMargin = 10
//        cvLoginLayoutParams.setMargins()
//        ObjectAnimator.ofArgb(cv_login, "setMargins")

        val raiseUp = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                cvLoginLayoutParams.topMargin = (0 - SizeUtils.dp2px(context!!.applicationContext, 120f) * interpolatedTime).toInt()
                cv_login.layoutParams = cvLoginLayoutParams
            }
        }
        raiseUp.interpolator = OvershootInterpolator()
        raiseUp.duration = 500
        cv_login.startAnimation(raiseUp)
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

        cv_to_schedule.setOnClickListener {
            cardDialog2C()
            getSchedule(viewModel, et_id.text.toString(), name, year, term)
        }
    }

    private fun cardDialog2C() {
        cv_login.isClickable = false
        widthAnimation(cv_login, loginBtnOldWidth, loginBtnOldHeight, 300, 0)
        fadeAnimation(cpb, 0f, 1f, 100, 0)
        heightAnimation(cv_login, cv_login.height, loginBtnOldHeight, 300, 0)

        val dropDown = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                cvLoginLayoutParams.topMargin = (SizeUtils.dp2px(context!!.applicationContext, 56f) * interpolatedTime).toInt()
                cv_login.layoutParams = cvLoginLayoutParams
            }
        }
        dropDown.interpolator = OvershootInterpolator()
        dropDown.duration = 500
        cv_login.startAnimation(dropDown)

        val radiusOn = ObjectAnimator.ofFloat(cv_login, "radius", cv_login.radius, loginBtnOldRadius)
        radiusOn.duration = 100
        radiusOn.start()
        //rl_login.isClickable = true
        iv_mask.visibility = View.GONE

        ll_dialog.visibility = View.GONE

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginWebFragment()
    }
}
