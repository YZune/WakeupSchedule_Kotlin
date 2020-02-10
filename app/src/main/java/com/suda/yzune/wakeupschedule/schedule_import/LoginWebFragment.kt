package com.suda.yzune.wakeupschedule.schedule_import

import android.app.Activity.RESULT_OK
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.schedule_import.exception.CheckCodeErrorException
import com.suda.yzune.wakeupschedule.schedule_import.exception.PasswordErrorException
import com.suda.yzune.wakeupschedule.schedule_import.exception.UserNameErrorException
import com.suda.yzune.wakeupschedule.schedule_import.login_school.hust.MobileHub
import com.suda.yzune.wakeupschedule.schedule_import.login_school.jlu.UIMS
import com.suda.yzune.wakeupschedule.schedule_import.login_school.suda.SudaXK
import com.suda.yzune.wakeupschedule.utils.Utils
import es.dmoral.toasty.Toasty
import jahirfiquitiva.libs.textdrawable.TextDrawable
import kotlinx.android.synthetic.main.fragment_login_web.*
import kotlinx.coroutines.delay
import splitties.dimensions.dip
import java.io.IOException
import java.util.*

class LoginWebFragment : BaseFragment() {

    private var year = ""
    private var term = ""
    private var shanghaiPort = 0

    private val viewModel by activityViewModels<ImportViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.text = viewModel.school
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            input_id.setAutofillHints(View.AUTOFILL_HINT_USERNAME)
            input_pwd.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
        }
        if (viewModel.school != "苏州大学") {
            input_code.visibility = View.INVISIBLE
            rl_code.visibility = View.INVISIBLE
            tv_tip.visibility = View.GONE
        } else {
            viewModel.sudaXK = SudaXK()
            refreshCode()
            tv_tip.setOnClickListener {
                Utils.openUrl(context!!, "https://yzune.github.io/2018/08/13/%E4%BD%BF%E7%94%A8FortiClient%E8%BF%9E%E6%8E%A5%E6%A0%A1%E5%9B%AD%E7%BD%91/")
            }
        }
        if (viewModel.school == "上海大学") {
            btg_ports.visibility = View.VISIBLE
            tv_thanks.text = "感谢 @Deep Sea\n能导入贵校课程离不开他无私贡献代码"
            btg_ports.addOnButtonCheckedListener { group, checkedId, isChecked ->
                if (isChecked) {
                    shanghaiPort = checkedId - R.id.btn_port1
                }
                if (!isChecked && shanghaiPort == checkedId - R.id.btn_port1) {
                    group.findViewById<MaterialButton>(checkedId).isChecked = true
                }
            }
        }
        if (viewModel.school == "清华大学") {
            input_id.hint = "用户名"
            tv_thanks.text = "感谢 @RikaSugisawa\n能导入贵校课程离不开他无私贡献代码"
            et_id.inputType = InputType.TYPE_CLASS_TEXT
        }
        if (viewModel.school == "吉林大学") {
            tv_thanks.text = "感谢 @颩欥殘膤\n能导入贵校课程离不开他无私贡献代码"
        }
        if (viewModel.school == "华中科技大学") {
            et_id.inputType = InputType.TYPE_CLASS_TEXT
            tv_thanks.text = "感谢 @Lyt99\n能导入贵校课程离不开他无私贡献代码"
        }
        if (viewModel.school == "西北工业大学") {
            et_id.inputType = InputType.TYPE_CLASS_TEXT
            tv_thanks.text = "感谢 @ludoux\n能导入贵校课程离不开他无私贡献代码"
        }
        initEvent()
    }

    private fun TextInputLayout.showError(str: String, dur: Long = 3000) {
        launch {
            this@showError.error = str
            delay(dur)
            this@showError.error = null
        }
    }

    private fun initEvent() {

        val textDrawable = TextDrawable
                .builder()
                .textColor(Color.WHITE)
                .fontSize(context!!.dip(24))
                .useFont(ResourcesCompat.getFont(context!!, R.font.iconfont)!!)
                .buildRect("\uE6DE", Color.TRANSPARENT)

        fab_login.setImageDrawable(textDrawable)

        iv_code.setOnClickListener {
            refreshCode()
        }

        iv_error.setOnClickListener {
            refreshCode()
        }

//        sheet.setOnClickListener {
//            fab_login.isExpanded = false
//        }

        btn_to_schedule.setOnClickListener {
            when (viewModel.school) {
                "苏州大学" -> getSudaSchedule()
                "西北工业大学" -> getNWPUSchedule()
            }
        }

        btn_cancel.setOnClickListener {
            refreshCode()
            fab_login.isExpanded = false
        }

        fab_login.setOnClickListener {
            when {
                et_id.text!!.isEmpty() -> input_id.showError("学号不能为空")
                et_pwd.text!!.isEmpty() -> input_pwd.showError("密码不能为空")
                et_code.text!!.isEmpty() && viewModel.school == "苏州大学" -> input_code.showError("验证码不能为空")
                else -> launch { login() }
            }
        }
    }

    private suspend fun login() {
        var exception: Exception? = null
        var result = 0
        when (viewModel.school) {
            "苏州大学" -> {
                pb_loading.visibility = View.VISIBLE
                ll_dialog.visibility = View.INVISIBLE
                fab_login.isExpanded = true
                viewModel.sudaXK?.id = et_id.text.toString()
                viewModel.sudaXK?.password = et_pwd.text.toString()
                viewModel.sudaXK?.code = et_code.text.toString()
                try {
                    viewModel.sudaXK?.login()
                    pb_loading.visibility = View.GONE
                    cardC2Dialog(viewModel.sudaXK?.years!!)
                } catch (e: IOException) {
                    Toasty.error(activity!!, "请检查是否连接校园网", Toast.LENGTH_LONG).show()
                    delay(500)
                    fab_login.isExpanded = false
                } catch (e: Exception) {
                    when (e) {
                        is UserNameErrorException -> {
                            et_id.requestFocus()
                            input_id.showError(e.message ?: "", 5000)
                            refreshCode()
                        }
                        is PasswordErrorException -> {
                            et_pwd.requestFocus()
                            input_pwd.showError(e.message ?: "", 5000)
                            refreshCode()
                        }
                        is CheckCodeErrorException -> {
                            input_code.showError(e.message ?: "", 5000)
                            refreshCode()
                        }
                        else -> Toasty.error(activity!!, e.message
                                ?: "再试一次看看哦", Toast.LENGTH_LONG).show()
                    }
                    delay(500)
                    fab_login.isExpanded = false
                }
            }
            "清华大学" -> {
                try {
                    result = viewModel.loginTsinghua(et_id.text.toString(),
                            et_pwd.text.toString())
                } catch (e: Exception) {
                    exception = e
                }
            }
            "上海大学" -> {
                try {
                    result = viewModel.loginShanghai(et_id.text.toString(),
                            et_pwd.text.toString(), shanghaiPort)
                } catch (e: Exception) {
                    exception = e
                }
            }
            "吉林大学" -> {
                val uims = UIMS(et_id.text.toString(), et_pwd.text.toString())
                try {
                    uims.connectToUIMS()
                    uims.login()
                    uims.getCurrentUserInfo()
                    uims.getCourseSchedule()
                    result = viewModel.convertJLU(uims.courseJSON)
                } catch (e: Exception) {
                    exception = e
                }
            }
            "华中科技大学" -> {
                val hub = MobileHub(et_id.text.toString(), et_pwd.text.toString())
                try {
                    hub.login()
                    hub.getCourseSchedule()
                    result = viewModel.convertHUST(hub.courseHTML)
                } catch (e: Exception) {
                    exception = e
                }
            }
            "西北工业大学" -> {
                Toasty.info(activity!!.applicationContext, "年份为学年的起始年，学期[秋、春、夏]分别对应[1、2、3]\n例如[2019-2020春] 选择[2019 2]", Toast.LENGTH_LONG).show()
                pb_loading.visibility = View.INVISIBLE
                fab_login.isExpanded = true
                val year = Calendar.getInstance().get(Calendar.YEAR)
                val list = mutableListOf<String>()
                for (index in year - 7..year) {
                    list.add(index.toString())
                }
                cardC2Dialog(list, true)
            }
        }
        if (viewModel.school == "苏州大学" || viewModel.school == "西北工业大学") return
        when (exception) {
            null -> {
                showSuccess(result)
            }
            is UserNameErrorException -> {
                et_id.requestFocus()
                input_id.showError(exception.message ?: "", 5000)
            }
            is PasswordErrorException -> {
                et_pwd.requestFocus()
                input_pwd.showError(exception.message ?: "", 5000)
            }
            else -> Toasty.error(activity!!, exception.message
                    ?: "再试一次看看哦", Toast.LENGTH_LONG).show()
        }
    }

    private fun getNWPUSchedule() {
        launch {
            try {
                if (term.isEmpty()) {
                    term = "1"
                }
                val result = viewModel.loginNWPU(et_id.text.toString(), et_pwd.text.toString(), year, term)
                showSuccess(result)
            } catch (e: Exception) {
                fab_login.isExpanded = false
                when (e) {
                    is UserNameErrorException -> {
                        et_id.requestFocus()
                        input_id.showError(e.message ?: "", 5000)
                        refreshCode()
                    }
                    is PasswordErrorException -> {
                        et_pwd.requestFocus()
                        input_pwd.showError(e.message ?: "", 5000)
                        refreshCode()
                    }
                    is CheckCodeErrorException -> {
                        input_code.showError(e.message ?: "", 5000)
                        refreshCode()
                    }
                    else -> Toasty.error(activity!!, e.message
                            ?: "再试一次看看哦", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getSudaSchedule() {
        viewModel.importType = Common.TYPE_ZF
        launch {
            try {
                val result = viewModel.importSchedule(viewModel.sudaXK?.toSchedule(year, term)!!)
                showSuccess(result)
            } catch (e: Exception) {
                Toasty.error(activity!!,
                        "导入失败>_<\n${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun refreshCode() {
        launch {
            et_code.setText("")
            progress_bar.visibility = View.VISIBLE
            iv_code.visibility = View.INVISIBLE
            iv_error.visibility = View.INVISIBLE
            try {
                val bitmap = viewModel.sudaXK?.getCheckCode()
                progress_bar.visibility = View.GONE
                iv_code.visibility = View.VISIBLE
                iv_error.visibility = View.INVISIBLE
                iv_code.setImageBitmap(bitmap)
            } catch (e: Exception) {
                progress_bar.visibility = View.GONE
                iv_code.visibility = View.INVISIBLE
                iv_error.visibility = View.VISIBLE
                Toasty.error(context!!, "请检查是否连接校园网", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showSuccess(result: Int) {
        Toasty.success(activity!!,
                "成功导入 $result 门课程(ﾟ▽ﾟ)/\n请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
        activity!!.setResult(RESULT_OK)
        activity!!.finish()
    }

    private fun cardC2Dialog(years: List<String>, selectLastYear: Boolean = false) {
        ll_dialog.visibility = View.VISIBLE
        val terms = arrayOf("1", "2", "3")
        wp_term.displayedValues = terms
        wp_term.value = 0
        wp_term.minValue = 0
        wp_term.maxValue = terms.size - 1

        wp_years.displayedValues = years.toTypedArray()
        wp_years.minValue = 0
        wp_years.maxValue = years.size - 1
        if (!selectLastYear) {
            wp_years.value = 0
        } else {
            wp_years.value = wp_years.maxValue
        }

        wp_years.setOnValueChangedListener { _, _, newVal ->
            year = years[newVal]
            Log.d("选中", "选中学年$year")
        }
        wp_term.setOnValueChangedListener { _, _, newVal ->
            term = terms[newVal]
            Log.d("选中", "选中学期$term")
        }
    }

    override fun onDestroyView() {
        btg_ports.clearOnButtonCheckedListeners()
        super.onDestroyView()
    }

}
