package com.suda.yzune.wakeupschedule.schedule_import


import android.app.Activity.RESULT_OK
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.apply_info.ApplyInfoActivity
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_web_view_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

class WebViewLoginFragment : BaseFragment() {

    private val GET_FRAME_CONTENT_STR = "document.getElementById('iframeautoheight').contentWindow.document.body.innerHTML"

    private lateinit var type: String
    private lateinit var url: String
    private lateinit var viewModel: ImportViewModel
    private var qzType = 0
    private var isRefer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString("type")!!
            url = it.getString("url")!!
        }
        viewModel = ViewModelProviders.of(activity!!).get(ImportViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_web_view_login, container, false)
    }

    @JavascriptInterface
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(context!!.applicationContext, view.find(R.id.v_status))

        if (url != "") {
            et_url.setText(url)
            startVisit()
        } else {
            val url = PreferenceUtils.getStringFromSP(activity!!.applicationContext, "school_url", "")
            if (url != "") {
                et_url.setText(url)
                startVisit()
            }
        }

        if (type == "apply") {
            tv_tips.text = "1. 在上方输入教务网址，部分学校需要连接校园网\n2. 登录后点击到个人课表或者相关的页面\n3. 点击右下角的按钮抓取源码，并上传到服务器"
        }

        if (type == "强智教务") {
            cg_qz.visibility = View.VISIBLE
            chip_qz1.isChecked = true
        } else {
            cg_qz.visibility = View.INVISIBLE
        }

        wv_course.settings.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wv_course.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        wv_course.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                handler.proceed() //接受所有网站的证书
            }
        }
        wv_course.addJavascriptInterface(InJavaScriptLocalObj(), "local_obj")
        wv_course.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                ll_error.visibility = View.VISIBLE
                wv_course.visibility = View.GONE
            }
        }
        wv_course.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    pb_load.progress = newProgress
                    pb_load.visibility = View.GONE
                } else {
                    pb_load.progress = newProgress * 5
                    pb_load.visibility = View.VISIBLE
                }
            }
        }
        //设置自适应屏幕，两者合用
        wv_course.settings.useWideViewPort = true //将图片调整到适合WebView的大小
        wv_course.settings.loadWithOverviewMode = true // 缩放至屏幕的大小
        // 缩放操作
        wv_course.settings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        wv_course.settings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        wv_course.settings.displayZoomControls = false //隐藏原生的缩放控件wvCourse.settings
        wv_course.settings.javaScriptCanOpenWindowsAutomatically = true
        wv_course.settings.domStorageEnabled = true
        wv_course.settings.userAgentString = wv_course.settings.userAgentString.replace("Mobile", "eliboM").replace("Android", "diordnA")
        initEvent()
    }

    private fun initEvent() {

        var chipId = 0
        cg_qz.setOnCheckedChangeListener { chipGroup, id ->
            when (id) {
                R.id.chip_qz1 -> {
                    chipId = id
                    qzType = 0
                }
                R.id.chip_qz2 -> {
                    chipId = id
                    qzType = 1
                }
                R.id.chip_qz3 -> {
                    chipId = id
                    qzType = 2
                }
                R.id.chip_qz4 -> {
                    chipId = id
                    qzType = 3
                }
                R.id.chip_qz5 -> {
                    chipId = id
                    qzType = 4
                }
                R.id.chip_qz6 -> {
                    chipId = id
                    qzType = 5
                }
                else -> {
                    chipGroup.find<Chip>(chipId).isChecked = true
                }
            }
        }

        tv_got_it.setOnClickListener {
            tv_got_it.visibility = View.GONE
            tv_tips.visibility = View.GONE
            tv_tips.visibility = View.GONE
        }

        tv_go.setOnClickListener {
            startVisit()
        }

        et_url.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                startVisit()
            }
            return@setOnEditorActionListener false
        }

        fab_import.setOnClickListener {
            if (type in viewModel.gzChengFangList && !isRefer) {
                val referUrl = when (type) {
                    "广东工业大学" -> "http://jxfw.gdut.edu.cn/xsgrkbcx!getXsgrbkList.action"
                    "南方医科大学" -> "http://zhjw.smu.edu.cn/xsgrkbcx!getXsgrbkList.action"
                    "五邑大学" -> "http://jxgl.wyu.edu.cn/xsgrkbcx!getXsgrbkList.action"
                    "湖北医药学院" -> "http://jw.hbmu.edu.cn/xsgrkbcx!getXsgrbkList.action"
                    else -> if (wv_course.url.endsWith('/')) wv_course.url + "xsgrkbcx!getXsgrbkList.action" else wv_course.url + "/xsgrkbcx!getXsgrbkList.action"
                }
                wv_course.loadUrl(referUrl)
                it.longSnackbar("请重新选择一下学期再点按钮导入，要记得选择全部周，记得点查询按钮")
                isRefer = true
            } else if (viewModel.isUrp && !isRefer) {
                val referUrl = if (wv_course.url.endsWith('/')) wv_course.url.substringBeforeLast('/').substringBeforeLast('/') + "/xkAction.do?actionType=6" else wv_course.url.substringBeforeLast('/') + "/xkAction.do?actionType=6"
                wv_course.loadUrl(referUrl)
                it.longSnackbar("请在看到网页加载完成后，再点一次右下角按钮")
                isRefer = true
            } else {
                wv_course.loadUrl("javascript:var ifrs=document.getElementsByTagName(\"iframe\");" +
                        "var iframeContent=\"\";" +
                        "for(var i=0;i<ifrs.length;i++){" +
                        "iframeContent=iframeContent+ifrs[i].contentDocument.body.parentElement.outerHTML;" +
                        "}\n" +
                        "var frs=document.getElementsByTagName(\"frame\");" +
                        "var frameContent=\"\";" +
                        "for(var i=0;i<frs.length;i++){" +
                        "frameContent=frameContent+frs[i].contentDocument.body.parentElement.outerHTML;" +
                        "}\n" +
                        "window.local_obj.showSource(document.getElementsByTagName('html')[0].innerHTML + iframeContent + frameContent);")
            }
        }
    }

    private fun startVisit() {
        wv_course.visibility = View.VISIBLE
        ll_error.visibility = View.GONE
        val url = if (et_url.text.toString().startsWith("http://") || et_url.text.toString().startsWith("https://"))
            et_url.text.toString() else "http://" + et_url.text.toString()
        if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
            wv_course.loadUrl(url)
            PreferenceUtils.saveStringToSP(activity!!.applicationContext, "school_url", url)
        } else {
            Toasty.error(context!!.applicationContext, "请输入正确的网址╭(╯^╰)╮").show()
        }
    }

    internal inner class InJavaScriptLocalObj {
        @JavascriptInterface
        fun showSource(html: String) {
            if (type != "apply") {
                launch {
                    val task = withContext(Dispatchers.IO) {
                        try {
                            when (type) {
                                in viewModel.gzChengFangList -> viewModel.parseGuangGong(html)
                                "正方教务" -> viewModel.importBean2CourseBean(viewModel.html2ImportBean(html), html)
                                "新正方教务" -> viewModel.parseNewZF(html)
                                "强智教务" -> {
                                    when (qzType) {
                                        0 -> viewModel.parseQZ(html, "北京林业大学")
                                        1 -> viewModel.parseQZ(html, "广东外语外贸大学")
                                        2 -> viewModel.parseQZ(html, "长春大学")
                                        3 -> viewModel.parseQZ(html, "青岛农业大学")
                                        4 -> viewModel.parseQZ(html, "锦州医科大学")
                                        5 -> viewModel.parseQZ(html, "山东科技大学")
                                        else -> "没有贵校的信息哦>_<"
                                    }
                                }
                                "长春大学" -> viewModel.parseQZ(html, type)
                                "湖南信息职业技术学院" -> viewModel.parseHNIU(html)
                                in viewModel.qzAbnormalNodeList -> viewModel.parseQZ(html, type)
                                in viewModel.qzGuangwaiList -> viewModel.parseQZ(html, type)
                                in viewModel.ZFSchoolList -> viewModel.importBean2CourseBean(viewModel.html2ImportBean(html), html)
                                in viewModel.newZFSchoolList -> viewModel.parseNewZF(html)
                                in viewModel.qzLessNodeSchoolList -> viewModel.parseQZ(html, type)
                                in viewModel.qzMoreNodeSchoolList -> viewModel.parseQZ(html, type)
                                else -> "没有贵校的信息哦>_<"
                            }
                        } catch (e: Exception) {
                            "导入失败>_<\n${e.message}"
                        }
                    }

                    when (task) {
                        "ok" -> {
                            Toasty.success(activity!!.applicationContext, "导入成功(ﾟ▽ﾟ)/请在右侧栏切换后查看").show()
                            activity!!.setResult(RESULT_OK)
                            activity!!.finish()
                        }
                        else -> Toasty.error(activity!!.applicationContext, task, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                launch {
                    val task = withContext(Dispatchers.IO) {
                        try {
                            viewModel.postHtml(
                                    school = viewModel.schoolInfo[0],
                                    type = if (viewModel.isUrp) "URP" else viewModel.schoolInfo[1],
                                    qq = viewModel.schoolInfo[2],
                                    html = html)
                        } catch (e: Exception) {
                            "上传失败>_<\n" + e.message
                        }
                    }

                    when (task) {
                        "ok" -> {
                            Toasty.success(activity!!.applicationContext, "上传源码成功~请等待适配哦", Toast.LENGTH_LONG).show()
                            activity!!.startActivity<ApplyInfoActivity>()
                            activity!!.finish()
                        }
                        else -> {
                            Toasty.error(activity!!.applicationContext, task, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        wv_course.webViewClient = null
        wv_course.webChromeClient = null
        wv_course.clearCache(true)
        wv_course.clearHistory()
        wv_course.removeAllViews()
        wv_course.destroy()
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(param0: String, param1: String = "") =
                WebViewLoginFragment().apply {
                    arguments = Bundle().apply {
                        putString("type", param0)
                        putString("url", param1)
                    }
                }
    }
}
