package com.suda.yzune.wakeupschedule.schedule_import

import android.app.Activity.RESULT_OK
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.schedule_import.HUST.MobileHub
import com.suda.yzune.wakeupschedule.schedule_import.JLU.UIMS
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import es.dmoral.toasty.Toasty
import jahirfiquitiva.libs.textdrawable.TextDrawable
import kotlinx.android.synthetic.main.fragment_login_web.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.dip

class LoginWebFragment : BaseFragment() {

    private var name = ""
    private var year = ""
    private var term = ""
    private val years = arrayListOf<String>()
    private var type = "苏州大学"
    private var shanghaiPort = 0

    private lateinit var viewModel: ImportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ImportViewModel::class.java)
        type = arguments!!.getString("type", "苏州大学")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.text = type
        if (type != "苏州大学") {
            input_code.visibility = View.INVISIBLE
            rl_code.visibility = View.INVISIBLE
            tv_tip.visibility = View.GONE
        } else {
            refreshCode()
            tv_tip.setOnClickListener {
                CourseUtils.openUrl(context!!, "https://yzune.github.io/2018/08/13/%E4%BD%BF%E7%94%A8FortiClient%E8%BF%9E%E6%8E%A5%E6%A0%A1%E5%9B%AD%E7%BD%91/")
            }
        }
        if (type == "上海大学") {
            btg_ports.visibility = View.VISIBLE
            tv_thanks.text = "感谢 @Deep Sea\n能导入贵校课程离不开他无私贡献代码"
            btg_ports.addOnButtonCheckedListener { group, checkedId, isChecked ->
                if (isChecked) {
                    shanghaiPort = checkedId - R.id.btn_port1
                }
                if (!isChecked && shanghaiPort == checkedId - R.id.btn_port1) {
                    group.find<MaterialButton>(checkedId).isChecked = true
                }
            }
        }
        if (type == "清华大学") {
            input_id.hint = "用户名"
            tv_thanks.text = "感谢 @RikaSugisawa\n能导入贵校课程离不开他无私贡献代码"
            et_id.inputType = InputType.TYPE_CLASS_TEXT
        }
        if (type == "吉林大学") {
            tv_thanks.text = "感谢 @颩欥殘膤\n能导入贵校课程离不开他无私贡献代码"
        }
        if(type == "华中科技大学") {
            et_id.inputType = InputType.TYPE_CLASS_TEXT
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
                .fontSize(dip(24))
                .useFont(ResourcesCompat.getFont(context!!, R.font.iconfont)!!)
                .buildRect("\uE6DE", Color.TRANSPARENT)

        fab_login.setImageDrawable(textDrawable)

        iv_code.setOnClickListener {
            refreshCode()
        }

        iv_error.setOnClickListener {
            refreshCode()
        }

        sheet.setOnClickListener {
            fab_login.isExpanded = false
        }

        btn_to_schedule.setOnClickListener {
            getSchedule(viewModel, et_id.text.toString(), name, year, term)
        }

        btn_cancel.setOnClickListener {
            refreshCode()
            fab_login.isExpanded = false
        }

        fab_login.setOnClickListener {
            when {
                et_id.text!!.isEmpty() -> input_id.showError("学号不能为空")
                et_pwd.text!!.isEmpty() -> input_pwd.showError("密码不能为空")
                et_code.text!!.isEmpty() && type == "苏州大学" -> input_code.showError("验证码不能为空")
                else -> {
                    if (type == "苏州大学") {
                        launch {
                            val task = withContext(Dispatchers.IO) {
                                try {
                                    viewModel.login(et_id.text.toString(),
                                            et_pwd.text.toString(), et_code.text.toString())
                                } catch (e: Exception) {
                                    e.message
                                }
                            }
                            when {
                                task == null -> {
                                    Toasty.error(context!!.applicationContext, "请检查是否连接校园网", Toast.LENGTH_LONG).show()
                                }
                                task == "error" -> {
                                    Toasty.error(context!!.applicationContext, "请检查是否连接校园网", Toast.LENGTH_LONG).show()
                                }
                                task.contains("验证码不正确") -> {
                                    input_code.showError("验证码不正确哦", 5000)
                                    refreshCode()
                                }
                                task.contains("密码错误") -> {
                                    et_pwd.requestFocus()
                                    input_pwd.showError("密码错误哦", 5000)
                                    refreshCode()
                                }
                                task.contains("用户名不存在") -> {
                                    et_id.requestFocus()
                                    input_id.showError("看看学号是不是输错啦", 5000)
                                    refreshCode()
                                }
                                task.contains("欢迎您：") -> {
                                    getPrepared(et_id.text.toString())
                                }
                                task.contains("同学，你好") -> {
                                    getPrepared(et_id.text.toString())
                                }
                                task.contains("请耐心排队") -> {
                                    Log.d("登录", task)
                                    refreshCode()
                                    Toasty.error(context!!.applicationContext, "选课排队中，稍后再试哦", Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    refreshCode()
                                    Toasty.error(context!!.applicationContext, "再试一次看看哦", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    if (type == "清华大学") {
                        launch {
                            val task = withContext(Dispatchers.IO) {
                                try {
                                    viewModel.loginTsinghua(et_id.text.toString(),
                                            et_pwd.text.toString())
                                } catch (e: Exception) {
                                    e.message
                                }
                            }
                            when (task) {
                                "ok" -> {
                                    Toasty.success(activity!!.applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                                    activity!!.setResult(RESULT_OK)
                                    activity!!.finish()
                                }
                                else -> {
                                    Toasty.error(activity!!.applicationContext, "发生异常>_<\n$task", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    if (type == "上海大学") {
                        launch {
                            val task = withContext(Dispatchers.IO) {
                                try {
                                    viewModel.loginShanghai(et_id.text.toString(),
                                            et_pwd.text.toString(), shanghaiPort)
                                } catch (e: Exception) {
                                    e.message
                                }
                            }
                            when (task) {
                                "ok" -> {
                                    Toasty.success(activity!!.applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                                    activity!!.setResult(RESULT_OK)
                                    activity!!.finish()
                                }
                                else -> {
                                    Toasty.error(activity!!.applicationContext, "发生异常>_<\n$task", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    if (type == "吉林大学") {
                        launch {
                            val uims = UIMS(et_id.text.toString(), et_pwd.text.toString())
                            val task = withContext(Dispatchers.IO) {
                                try {
                                    uims.connectToUIMS()
                                    uims.login()
                                    uims.getCurrentUserInfo()
                                    uims.getCourseSchedule()
                                    viewModel.convertJLU(uims.courseJSON)
                                } catch (e: Exception) {
                                    e.message
                                }
                            }
                            when (task) {
                                "ok" -> {
                                    Toasty.success(activity!!.applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                                    activity!!.setResult(RESULT_OK)
                                    activity!!.finish()
                                }
                                else -> {
                                    Toasty.error(activity!!.applicationContext, "发生异常>_<\n$task", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    if (type == "华中科技大学") {
                        launch {
                            val task = withContext(Dispatchers.IO) {
                                val hub = MobileHub(et_id.text.toString(), et_pwd.text.toString())
                                try {
                                    if(!hub.login()) {
                                        "no login"
                                    } else {
                                        hub.getCourseSchedule()
                                        viewModel.convertHUST(hub.courseHTML)
                                    }

                                } catch (e: Exception) {
                                    e.message
                                }
                            }
                            when (task) {
                                "ok" -> {
                                    Toasty.success(activity!!.applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                                    activity!!.setResult(RESULT_OK)
                                    activity!!.finish()
                                }
                                "no login" -> {
                                    Toasty.error(activity!!.applicationContext, "学号或密码错误，请检查后再输入", Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    if(task?.contains("failed to connect") == true) {
                                        Toasty.error(activity!!.applicationContext, "无法访问HUB系统，请检查是否连接校园网", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toasty.error(activity!!.applicationContext, "发生异常>_<\n$task", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getPrepared(id: String) {
        launch {
            pb_loading.visibility = View.VISIBLE
            ll_dialog.visibility = View.INVISIBLE
            fab_login.isExpanded = !fab_login.isExpanded
            val task = withContext(Dispatchers.IO) {
                try {
                    viewModel.getPrepare(id)
                } catch (e: Exception) {
                    e.message!!
                }
            }

            if (task != "error") {
                years.clear()
                years.addAll(viewModel.parseYears(task)!!)
                name = viewModel.parseName(task)
            }

            if (years.isEmpty()) {
                fab_login.isExpanded = !fab_login.isExpanded
                Toasty.error(context!!.applicationContext, "获取学期数据失败:(", Toast.LENGTH_LONG).show()
            } else {
                pb_loading.visibility = View.GONE
                cardC2Dialog(years)
            }
        }
    }

    private fun getSchedule(viewModel: ImportViewModel, id: String, name: String, year: String, term: String) {
        if (year == viewModel.selectedYear && term == viewModel.selectedTerm) {
            launch {
                val import = withContext(Dispatchers.IO) {
                    try {
                        viewModel.importBean2CourseBean(viewModel.html2ImportBean(viewModel.selectedSchedule), viewModel.selectedSchedule)
                    } catch (e: Exception) {
                        e.message
                    }
                }
                when (import) {
                    "ok" -> {
                        Toasty.success(activity!!.applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
                        activity!!.finish()
                    }
                    else -> {
                        Toasty.error(activity!!.applicationContext, "发生异常>_<\n$import", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            launch {
                val task = withContext(Dispatchers.IO) {
                    try {
                        viewModel.toSchedule(id, name, year, term)
                    } catch (e: Exception) {
                        e.message
                    }
                }

                if (task == null || task == "error") {
                    Toasty.error(context!!.applicationContext, "网络错误", Toast.LENGTH_LONG).show()
                } else if (task.contains("您本学期课所选学分小于 0分")) {
                    Toasty.error(context!!.applicationContext, "该学期貌似还没有课程").show()
                } else {
                    val import = withContext(Dispatchers.IO) {
                        try {
                            viewModel.importBean2CourseBean(viewModel.html2ImportBean(task), task)
                        } catch (e: Exception) {
                            e.message
                        }
                    }
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
            et_code.setText("")
            progress_bar.visibility = View.VISIBLE
            iv_code.visibility = View.INVISIBLE
            iv_error.visibility = View.INVISIBLE
            val task = withContext(Dispatchers.IO) {
                try {
                    viewModel.getCheckCode()
                } catch (e: Exception) {
                    null
                }
            }
            if (task != null) {
                progress_bar.visibility = View.GONE
                iv_code.visibility = View.VISIBLE
                iv_error.visibility = View.INVISIBLE
                iv_code.setImageBitmap(task)
            } else {
                progress_bar.visibility = View.GONE
                iv_code.visibility = View.INVISIBLE
                iv_error.visibility = View.VISIBLE
                Toasty.error(context!!.applicationContext, "请检查是否连接校园网", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cardC2Dialog(years: List<String>) {
        ll_dialog.visibility = View.VISIBLE
        val terms = arrayOf("1", "2", "3")
        wp_term.displayedValues = terms
        wp_term.value = 0
        wp_term.minValue = 0
        wp_term.maxValue = terms.size - 1

        wp_years.displayedValues = years.toTypedArray()
        wp_years.value = 0
        wp_years.minValue = 0
        wp_years.maxValue = years.size - 1

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

    companion object {
        @JvmStatic
        fun newInstance(type: String) = LoginWebFragment().apply {
            arguments = Bundle().apply {
                putString("type", type)
            }
        }
    }
}
